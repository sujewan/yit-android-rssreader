package com.sujewan.rssreader.connector;

import java.util.ArrayList;
import java.util.List;

import com.sujewan.rssreader.model.RSSWebsite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1; // Database Version
    private static final String DATABASE_NAME = "RSSReader"; // Database Name
    private static final String TABLE_RSS = "RSSWebsite"; // Table name

    //  Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_LINK = "link";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_RSS_LINK = "rss_link";

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Table
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_RSS_TABLE =
            "CREATE TABLE " + TABLE_RSS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT," + KEY_LINK
                + " TEXT," + KEY_RSS_LINK + " TEXT," + KEY_DESCRIPTION + " TEXT" + ")";
        db.execSQL(CREATE_RSS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RSS);

        // Create tables again
        onCreate(db);
    }

    public void addSite(RSSWebsite website)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, website.getTitle()); // site title
        values.put(KEY_LINK, website.getLink()); // site url
        values.put(KEY_RSS_LINK, website.getRssLink()); // rss link url
        values.put(KEY_DESCRIPTION, website.getDescription()); // site description

        // Check if row already existed in database
        if (!isSiteExists(db, website.getRssLink()))
        {
            // site not existed, create a new row
            db.insert(TABLE_RSS, null, values);
            db.close();
        }
        else
        {
            // site already existed update the row
            updateSite(website);
            db.close();
        }
    }

    // Reading all rows from database
    public List<RSSWebsite> getAllSites()
    {
        List<RSSWebsite> siteList = new ArrayList<RSSWebsite>();

        String selectQuery = "SELECT  * FROM " + TABLE_RSS + " ORDER BY id DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                RSSWebsite site = new RSSWebsite();
                site.setId(Integer.parseInt(cursor.getString(0)));
                site.setTitle(cursor.getString(1));
                site.setLink(cursor.getString(2));
                site.setRssLink(cursor.getString(3));
                site.setDescription(cursor.getString(4));

                // Adding contact to list
                siteList.add(site);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return contact list
        return siteList;
    }

    // Reading all rows from database
    public List<RSSWebsite> getWebsiteLink(String url)
    {
        List<RSSWebsite> siteList = new ArrayList<RSSWebsite>();
        String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_RSS + " WHERE " + KEY_RSS_LINK + " = '" + url + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                RSSWebsite site = new RSSWebsite();
                site.setId(Integer.parseInt(cursor.getString(0)));
                siteList.add(site);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return contact list
        return siteList;
    }

    // Updating a single row row will be identified by rss link (in future feature)
    public int updateSite(RSSWebsite website)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, website.getTitle());
        values.put(KEY_LINK, website.getLink());
        values.put(KEY_RSS_LINK, website.getRssLink());
        values.put(KEY_DESCRIPTION, website.getDescription());

        // updating row return
        int update = db.update(TABLE_RSS, values, KEY_RSS_LINK + " = ?", new String[] {
            String.valueOf(website.getRssLink())
        });
        db.close();
        return update;

    }

    // Reading a row is identified by row id
    public RSSWebsite getSite(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RSS, new String[] {
            KEY_ID, KEY_TITLE, KEY_LINK, KEY_RSS_LINK, KEY_DESCRIPTION
        }, KEY_ID + "=?", new String[] {
            String.valueOf(id)
        }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        RSSWebsite site =
            new RSSWebsite(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));

        site.setId(Integer.parseInt(cursor.getString(0)));
        site.setTitle(cursor.getString(1));
        site.setLink(cursor.getString(2));
        site.setRssLink(cursor.getString(3));
        site.setDescription(cursor.getString(4));
        cursor.close();
        db.close();
        return site;
    }

    // Deleting single row
    public void deleteSite(RSSWebsite site)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RSS, KEY_ID + " = ?", new String[] {
            String.valueOf(site.getId())
        });
        db.close();
    }

    // Checking whether a site is already existed check is done by matching rss
    public boolean isSiteExists(SQLiteDatabase db, String rss_link)
    {

        Cursor cursor =
            db.rawQuery("SELECT 1 FROM " + TABLE_RSS + " WHERE rss_link = '" + rss_link + "'", new String[] {});
        boolean exists = (cursor.getCount() > 0);
        return exists;
    }

}
