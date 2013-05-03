package com.sujewan.rssreader.ui;

import java.util.List;

import com.sujewan.rssreader.R;
import com.sujewan.rssreader.connector.DatabaseHandler;
import com.sujewan.rssreader.controller.RSSParser;
import com.sujewan.rssreader.model.RSSFeed;
import com.sujewan.rssreader.model.RSSWebsite;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewURLActivity extends Activity implements OnClickListener
{
    private EditText txtAddUrl;
    private Button btnSubmit, btnCancel;
    public static String url;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_url);

        initComponents(); // Initialize GUI Components
        setListeners(); // Set the Listeners
    }

    // Initialize GUI Components
    private void initComponents()
    {
        txtAddUrl = (EditText) findViewById(R.id.txtAddURL);
        btnSubmit = (Button) findViewById(R.id.btnSaveURL);
        btnCancel = (Button) findViewById(R.id.btnCancel);
    }

    // Set the Listeners
    private void setListeners()
    {
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    // Button clicked event
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnSaveURL:
                saveURL(); // Save New URL
                break;

            case R.id.btnCancel:
                finish(); // Close the Current Activity
                break;

            default:
                break;
        }
    }

    private void saveURL()
    {
        url = txtAddUrl.getText().toString().trim();
        if (validation(url))
        {
            if (chkWebsiteLink(url))
            {
                new LoadRSSFeed().execute();
            }
            else
            {
                Toast.makeText(getApplicationContext(), getString(R.string.already_added), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validation(String url)
    {
        if (url.length() > 0)
        {
            String urlPattern = "[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";
            if (url.matches(urlPattern))
            {
                // valid URL
                return true;
            }
            else
            {
                // URL not valid
                Toast.makeText(getApplicationContext(), getString(R.string.warning_valid_url), Toast.LENGTH_SHORT)
                    .show();
                return false;
            }
        }
        else
        {
            // Empty Field
            Toast.makeText(getApplicationContext(), getString(R.string.warning_website_url_null_check),
                Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean chkWebsiteLink(String url)
    {
        DatabaseHandler database = new DatabaseHandler(getApplicationContext());

        List<RSSWebsite> siteList = database.getWebsiteLink("http://".concat(url));
        if (siteList.isEmpty())
        {
            return true;
        }
        return false;
    }

    // Hide Keyboard
    private void hideKeyboard()
    {
        InputMethodManager inputMethodManager =
            (InputMethodManager) AddNewURLActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(AddNewURLActivity.this.getCurrentFocus().getWindowToken(), 0);
    }

    private class LoadRSSFeed extends AsyncTask<String, String, String>
    {
        private ProgressDialog progressDialog;
        RSSParser rssParser = new RSSParser();
        RSSFeed rssFeed;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AddNewURLActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false); //  Can't cancel function while running progress
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            rssFeed = rssParser.getRSSFeed(getApplicationContext(), "http://".concat(AddNewURLActivity.url));
            if (rssFeed != null)
            {
                DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
                RSSWebsite website =
                    new RSSWebsite(rssFeed.getTitle(), rssFeed.getLink(), rssFeed.getDescription(),
                        rssFeed.getRssLink());
                databaseHandler.addSite(website);

                // Hide Keyboard
                hideKeyboard();

                // Pass the result code to Main Activity for Refresh
                Intent intent = getIntent();
                setResult(1, intent);
                finish();
            }
            else
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            progressDialog.dismiss();
        }
    }
}
