package live.bar.dictionary.wiki.dictionarybar;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import live.bar.dictionary.wiki.dictionarybar.GoogleAnalytics.AnalyticsApplication;
import live.bar.dictionary.wiki.dictionarybar.Receiver.ClipboardMonitor;
import live.bar.dictionary.wiki.dictionarybar.Util.Internet;
import live.bar.dictionary.wiki.dictionarybar.Util.Parser;
import live.bar.dictionary.wiki.dictionarybar.Util.Util;

public class MainActivity extends ActionBarActivity {

    private EditText searchWord;
    private NotificationManager nManager;
    private ListView wordList;
    private String[] words;
    private Tracker t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startClipboardMonitor();

        init();
    }

    private void init() {
        wordList = (ListView) findViewById(R.id.listViewSearchResults);
        wordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this, Meaning.class).putExtra("Word", words[position]));
            }
        });

        searchWord = (EditText) findViewById(R.id.editTextQueryMain);
        searchWord.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Word search")
                            .setAction("Search (Main Page)")
                            .setLabel("Searched for " + searchWord.getText().toString())
                            .build());

                    startActivity(new Intent(MainActivity.this, Meaning.class)
                        .putExtra("Word", searchWord.getText().toString()));

                    return true;
                }
                return false;
            }
        });
        searchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing...
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Nothing...
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    new WordList().execute(URLEncoder.encode(s.toString().trim(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        t = ((AnalyticsApplication) getApplication())
                .getTracker(AnalyticsApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Home");
        t.setLanguage(Util.getApiEndpoint(this));
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsHolder.class));
                return true;

            case R.id.action_about:
                Util.AboutPopUp(this);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("PopUp")
                        .setAction("About")
                        .setLabel("About popup")
                        .build());
                return true;

            case R.id.rate:
                Util.launchGooglePlay(this);
                t.send(new HitBuilders.EventBuilder()
                    .setCategory("Other")
                    .setAction("Rate")
                    .setLabel("Rate app")
                    .build());
                Toast.makeText(this, R.string.thanks, Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * When ClipboardMonitor doesn't start on boot due to the reason like we
     * install new app after android phone boots, causing it won't receive boot
     * broadcast, this method makes sure ClipboardMonitor starts when MyClips
     * activity created.
     */
    private void startClipboardMonitor() {
        ComponentName service = startService(new Intent(this,
                ClipboardMonitor.class));
        if (service == null) {
            Log.e(getClass().getName(), "Can't start service "
                    + ClipboardMonitor.class.getName());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Util.showRatePopUp(this))
            Util.RatePopUp(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class WordList extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return Internet.getResponse(Internet
                        .getWordListURL(Util.getApiEndpoint(MainActivity.this), params[0]));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String strings) {
            super.onPostExecute(strings);

            if(strings != null) {
                try {
                    words = Parser.getWords(strings);
                    wordList.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_list_item_1, words));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
