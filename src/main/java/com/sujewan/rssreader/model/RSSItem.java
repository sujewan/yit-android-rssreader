package com.sujewan.rssreader.model;

public class RSSItem
{
    private String title;
    private String link;
    private String description;
    private String publishDate;

    public RSSItem(String title, String link, String description, String publishDate)
    {
        super();
        this.title = title;
        this.link = link;
        this.description = description;
        this.publishDate = publishDate;
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

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getPublishDate()
    {
        return publishDate;
    }

    public void setPublishDate(String publishDate)
    {
        this.publishDate = publishDate;
    }

}
