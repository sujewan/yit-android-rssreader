package com.sujewan.rssreader.controller;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.sujewan.rssreader.R;
import com.sujewan.rssreader.model.RSSFeed;
import com.sujewan.rssreader.model.RSSItem;

public class RSSParser
{
    private Context context;

    // Constructor
    public RSSParser()
    {

    }

    public RSSFeed getRSSFeed(Context context, String url)
    {
        this.context = context;
        RSSFeed rssFeed = null;
        
        // Passing URL for getting XML Elements
        String rssXML = getXmlFromUrl(url);
        if (rssXML != null)
        {
            Document document = this.getDocumentElement(rssXML);

            NodeList nodeList = document.getElementsByTagName("channel");
            Element element = (Element) nodeList.item(0);

            String title = this.getValue(element, "title");
            String link = this.getValue(element, "link");
            String description = this.getValue(element, "description");
            rssFeed = new RSSFeed(title, link, url, description);
        }

        return rssFeed;
    }

    // Method to get xml content from url HTTP Get request
    public String getXmlFromUrl(String url)
    {
        String xml = null;
        try
        {
            // request method is GET
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            httpGet.toString();
            HttpResponse httpResponse = httpClient.execute(httpGet);
            StatusLine statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK)
            {
                HttpEntity httpEntity = httpResponse.getEntity();
                if (httpEntity != null)
                {
                    xml = EntityUtils.toString(httpEntity);
                }
                else
                {
                    Toast.makeText(context, R.string.have_not_rss_feed, Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e("Error: ", e.getMessage());
            e.printStackTrace();
        }
        catch (ClientProtocolException e)
        {
            Log.e("Error: ", e.getMessage());
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.e("Error: ", e.getMessage());
            e.printStackTrace();
        }
        // return XML
        return xml;
    }

    public Document getDocumentElement(String xmlUrl)
    {
        Document document = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try
        {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlUrl));
            document = (Document) db.parse(is);
        }
        catch (ParserConfigurationException e)
        {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        catch (SAXException e)
        {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        catch (IOException e)
        {
            Log.e("Error: ", e.getMessage());
            return null;
        }

        return document;
    }

    public String getValue(Element item, String tagName)
    {
        NodeList n = item.getElementsByTagName(tagName);
        return this.getElementValue(n.item(0));
    }

    public final String getElementValue(Node element)
    {
        Node child;
        if (element != null)
        {
            if (element.hasChildNodes())
            {
                for (child = element.getFirstChild(); child != null; child = child.getNextSibling())
                {
                    if (child.getNodeType() == Node.TEXT_NODE || (child.getNodeType() == Node.CDATA_SECTION_NODE))
                    {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    // Getting RSS Item Elements
    public List<RSSItem> getRSSFeedItems(String rssUrl)
    {
        List<RSSItem> itemsList = new ArrayList<RSSItem>();
        String rssFeedItem;

        // get RSS XML from rss url
        rssFeedItem = this.getXmlFromUrl(rssUrl);

        if (rssFeedItem != null)
        {
            try
            {
                Document doc = this.getDocumentElement(rssFeedItem);
                NodeList nodeList = doc.getElementsByTagName("channel");
                Element e = (Element) nodeList.item(0);

                // Getting items array
                NodeList items = e.getElementsByTagName("item");

                // looping through each item
                for (int i = 0; i < items.getLength(); i++)
                {
                    Element element = (Element) items.item(i);

                    String title = this.getValue(element, "title");
                    String link = this.getValue(element, "link");
                    String description = this.getValue(element, "description");
                    String pubdate = this.getValue(element, "pubDate");

                    RSSItem rssItem = new RSSItem(title, link, description, pubdate);

                    // adding item to list
                    itemsList.add(rssItem);
                }
            }
            catch (Exception e)
            {
                Log.e("Error: ", e.getMessage());
                e.printStackTrace();
            }
        }

        // return item list
        return itemsList;
    }
}
