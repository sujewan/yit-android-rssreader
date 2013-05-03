package com.sujewan.rssreader.model;

import java.util.List;

public class RSSFeed
{
    private String title;
    private String link;
    private String rssLink;
    private String description;
    private List<RSSItem> rssItem;

    public RSSFeed(String title, String link, String rssLink, String description)
    {
        super();
        this.title = title;
        this.link = link;
        this.rssLink = rssLink;
        this.description = description;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getRssLink()
    {
        return rssLink;
    }

    public void setRssLink(String rssLink)
    {
        this.rssLink = rssLink;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<RSSItem> getRssItem()
    {
        return rssItem;
    }

    public void setRssItem(List<RSSItem> rssItem)
    {
        this.rssItem = rssItem;
    }
}
