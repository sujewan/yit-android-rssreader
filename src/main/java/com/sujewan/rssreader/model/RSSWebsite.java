package com.sujewan.rssreader.model;

public class RSSWebsite
{
    private Integer id;
    private String title;
    private String link;
    private String description;
    private String rssLink;

    public RSSWebsite()
    {
        
    }
    
    public RSSWebsite(String title, String link, String description, String rssLink)
    {
        super();
        this.title = title;
        this.link = link;
        this.description = description;
        this.rssLink = rssLink;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
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

    public String getRssLink()
    {
        return rssLink;
    }

    public void setRssLink(String rssLink)
    {
        this.rssLink = rssLink;
    }

}
