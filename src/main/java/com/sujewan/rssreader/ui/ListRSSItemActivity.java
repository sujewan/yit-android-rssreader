package com.sujewan.rssreader.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sujewan.rssreader.R;
import com.sujewan.rssreader.connector.DatabaseHandler;
import com.sujewan.rssreader.controller.RSSParser;
import com.sujewan.rssreader.model.RSSFeed;
import com.sujewan.rssreader.model.RSSItem;
import com.sujewan.rssreader.model.RSSWebsite;

public class ListRSSItemActivity extends Activity
{
    private ListView lstRssItem;
    private ProgressDialog dialog;
    DatabaseHandler database;

    // Array list for list view
    ArrayList<HashMap<String, String>> rssItemList;
    RSSParser rssParser = new RSSParser();
    List<RSSItem> rssItems = new ArrayList<RSSItem>();
    RSSFeed rssFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_item);

        initComponents(); // Initialize GUI Components
        setListeners(); // Set the Listeners
        fillListView(); // Fill RSS Item for particular web site
    }

    // Initialize GUI Components
    private void initComponents()
    {
        rssItemList = new ArrayList<HashMap<String, String>>();
        lstRssItem = (ListView) findViewById(R.id.lstRSSItem);
    }

    // Set the Listeners
    private void setListeners()
    {
        // Launching new screen on Selecting Single ListItem
        lstRssItem.setOnItemClickListener(new OnItemClickListener()
        {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(getApplicationContext(), WebPageActivity.class);

                // getting page url
                String pageUrl = ((TextView) view.findViewById(R.id.rssItemLink)).getText().toString();
                intent.putExtra("pageUrl", pageUrl);
                startActivity(intent);
            }
        });
    }

    // Fill the List View
    private void fillListView()
    {
        database = new DatabaseHandler(getApplicationContext());
        // get intent data
        Intent intent = getIntent();
        Integer siteID = Integer.parseInt(intent.getStringExtra("id"));

        RSSWebsite site = database.getSite(siteID);
        String rssLink = site.getRssLink();
        new loadRSSFeedItems().execute(rssLink);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 1
        if (resultCode == 1)
        {
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }

    class loadRSSFeedItems extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            dialog = new ProgressDialog(ListRSSItemActivity.this);
            dialog.setMessage(getString(R.string.loading_recent_articles));
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... args)
        {
            String rssUrl = args[0];
            // list of rss items
            rssItems = rssParser.getRSSFeedItems(rssUrl);

            // looping through each item
            for (RSSItem item : rssItems)
            {
                // creating new HashMap
                HashMap<String, String> map = new HashMap<String, String>();

                // adding each child node to HashMap key => value
                map.put("title", item.getTitle());
                map.put("link", item.getLink());
                map.put("pubDate", item.getPublishDate());
                String description = item.getDescription();

                // taking only 200 chars from description
                if (description.length() > 100)
                {
                    description = description.substring(0, 97) + "..";
                }
                map.put("description", description);

                // adding HashList to ArrayList
                rssItemList.add(map);
            }

            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    ListAdapter adapter =
                        new SimpleAdapter(ListRSSItemActivity.this, rssItemList, R.layout.list_rss_item, new String[] {
                            "link", "title", "pubDate", "description"
                        }, new int[] {
                            R.id.rssItemLink, R.id.rssTitle, R.id.rssDate, R.id.rssContents
                        });

                    lstRssItem.setAdapter(adapter);
                    registerForContextMenu(lstRssItem);
                }
            });
            return null;
        }

        protected void onPostExecute(String args)
        {
            dialog.dismiss();
        }
    }
}
