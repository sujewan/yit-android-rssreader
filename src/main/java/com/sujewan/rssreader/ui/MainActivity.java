package com.sujewan.rssreader.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sujewan.rssreader.R;
import com.sujewan.rssreader.connector.ConnectionDetector;
import com.sujewan.rssreader.connector.DatabaseHandler;
import com.sujewan.rssreader.controller.RSSParser;
import com.sujewan.rssreader.model.RSSWebsite;

public class MainActivity extends Activity implements OnClickListener
{
    private ImageView btnAddUrl;
    private ListView lstWebsite;
    private ProgressDialog dialog;

    ArrayList<HashMap<String, String>> rssFeedList;
    String[] sqliteID;
    RSSParser rssParser = new RSSParser();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents(); // Initialize GUI Components
        setListeners(); // Set the Listeners
        new websiteList().execute();
    }

    // Initialize GUI Components
    private void initComponents()
    {
        rssFeedList = new ArrayList<HashMap<String, String>>();
        btnAddUrl = (ImageView) findViewById(R.id.btnAddURL);
        lstWebsite = (ListView) findViewById(R.id.lstWesbsite);
        btnAddUrl.setVisibility(View.VISIBLE);
    }

    // Set the Listeners
    private void setListeners()
    {
        btnAddUrl.setOnClickListener(this);

        lstWebsite.setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // getting values from selected ListItem
                String sqliteID = ((TextView) view.findViewById(R.id.websiteID)).getText().toString();
                // Starting new intent
                Intent intent = new Intent(getApplicationContext(), ListRSSItemActivity.class);

                // passing row id
                intent.putExtra("id", sqliteID);
                startActivity(intent);
            }
        });
    }

    // Button clicked event
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnAddURL:
                addWebsite(); // Adding Web Site
                break;

            default:
                break;
        }
    }

    // Adding Web Site
    public void addWebsite()
    {
        // Check Internet Connection
        if (new ConnectionDetector(getApplicationContext()).isConnectingToInternet())
        {
            Intent intentAddURL = new Intent(MainActivity.this, AddNewURLActivity.class);
            startActivityForResult(intentAddURL, 1);
        }
        else
        {
            // Connection isn't available
            showAlertDialog(MainActivity.this, getString(R.string.connection),
                getString(R.string.chk_internet_connection));
        }

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

    // Create Context Menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        if (v.getId() == R.id.lstWesbsite)
        {
            menu.setHeaderTitle("Delete");
            menu.add(Menu.CATEGORY_SYSTEM, 0, 0, "Delete RSS Feed");
        }
    }

    // Responding Context Menu
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        if (menuItemIndex == 0)
        {
            DatabaseHandler database = new DatabaseHandler(getApplicationContext());
            RSSWebsite site = new RSSWebsite();
            site.setId(Integer.parseInt(sqliteID[info.position]));
            database.deleteSite(site);
            
            // refresh
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

        return true;
    }

    private class websiteList extends AsyncTask<Void, Long, Boolean>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage(getString(R.string.loading));
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(final Void... params)
        {
            // updating UI from Background Thread
            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    DatabaseHandler database = new DatabaseHandler(getApplicationContext());

                    // listing all web sites from SQLite
                    List<RSSWebsite> siteList = database.getAllSites();

                    sqliteID = new String[siteList.size()];

                    // loop through each web site
                    for (int i = 0; i < siteList.size(); i++)
                    {
                        RSSWebsite site = siteList.get(i);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put("id", site.getId().toString());
                        map.put("title", site.getTitle());
                        map.put("link", site.getLink());

                        // adding HashList to ArrayList
                        rssFeedList.add(map);

                        // add sqlite id to array
                        // used when deleting a website from sqlite
                        sqliteID[i] = site.getId().toString();
                    }

                    // Updating list view with web sites
                    ListAdapter adapter =
                        new SimpleAdapter(MainActivity.this, rssFeedList, R.layout.list_item_website, new String[] {
                            "id", "title", "link"
                        }, new int[] {
                            R.id.websiteID, R.id.websiteTitle, R.id.websiteLink
                        });
                    // updating list view
                    lstWebsite.setAdapter(adapter);
                    registerForContextMenu(lstWebsite);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(final Boolean result)
        {
            super.onPostExecute(result);
            dialog.dismiss();
        }
    }

    // Show the Alert Dialog
    @SuppressWarnings("deprecation")
    public void showAlertDialog(Context context, String title, String message)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title); // Setting Dialog Title
        alertDialog.setMessage(message); // Setting Dialog Message
        alertDialog.setIcon(R.drawable.rss_icon); // Setting alert dialog icon

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(final DialogInterface dialog, final int which)
            {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName("com.android.phone", "com.android.phone.Settings");
                startActivity(intent);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
