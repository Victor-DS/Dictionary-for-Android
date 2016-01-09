package live.bar.dictionary.wiki.dictionarybar;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import java.io.IOException;
import live.bar.dictionary.wiki.dictionarybar.Adapter.DefinitionAdapter;
import live.bar.dictionary.wiki.dictionarybar.GoogleAnalytics.AnalyticsApplication;
import live.bar.dictionary.wiki.dictionarybar.Objects.Definition;
import live.bar.dictionary.wiki.dictionarybar.Util.Internet;
import live.bar.dictionary.wiki.dictionarybar.Util.Parser;
import live.bar.dictionary.wiki.dictionarybar.Util.StatusBarHelper;
import live.bar.dictionary.wiki.dictionarybar.Util.Util;

/**
 * Created by victor on 26/04/15.
 */
public class Meaning extends Activity {

    private EditText originalWord;
    private ProgressBar barra;
    private ListView listDefinitions;
    private Tracker t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meaning);

        init();

        if(getWord()!=null || (getDefinition() != null
                && getDefinition().getFullDefinition()
                .equals(getString(R.string.failed_to_get_definition))))
            new GetMeaningTask().execute(getWord());
        else
            showDefinition(getDefinition());
    }

    private void init() {
        barra = (ProgressBar) findViewById(R.id.progressBar);
        originalWord = (EditText) findViewById(R.id.tvOriginalWord);
        originalWord.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Word search")
                            .setAction("Search (Meaning Activity)")
                            .setLabel("Searched for " + originalWord.getText().toString())
                            .build());

                    new GetMeaningTask().execute(originalWord.getText().toString());
                    return true;
                }
                return false;
            }
        });
        listDefinitions = (ListView) findViewById(R.id.listDefinition);
        t = ((AnalyticsApplication) getApplication())
                .getTracker(AnalyticsApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Word definition");
        t.setLanguage(Util.getApiEndpoint(this));
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    private void showDefinition(Definition definition) {
        barra.setVisibility(View.GONE);

        originalWord.setVisibility(View.VISIBLE);
        listDefinitions.setVisibility(View.VISIBLE);

        originalWord.setText(definition.getOriginalWord());
        listDefinitions.setAdapter(new DefinitionAdapter(Meaning.this,
                definition.getFullDefinition()));

    }

    private void error() {
        barra.setVisibility(View.GONE);

        originalWord.setVisibility(View.VISIBLE);
        originalWord.setText(Meaning.this.getString(R.string.status_bar_et_text));

        listDefinitions.setVisibility(View.VISIBLE);
        listDefinitions.setAdapter(new DefinitionAdapter(Meaning.this,
                new String[]{Meaning.this.getString(R.string.failed_to_get_definition)}));
    }

    private String getWord() {
        return getIntent().getExtras().getString("Word");
    }

    private Definition getDefinition() {
        return getIntent().getExtras().getParcelable("Definition");
    }

    private class GetMeaningTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return Internet.getResponse(Internet
                        .getDefinitionURL(Util.getApiEndpoint(Meaning.this), params[0]));
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Meaning", "IOException. Check connection.");
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            barra.setVisibility(View.VISIBLE);
            originalWord.setVisibility(View.GONE);
            listDefinitions.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result != null) {
                Definition resultado = null;
                try {
                    resultado = Parser.getWordDefinition(Meaning.this, result);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //isSuccessful?
                if(resultado != null) {
                    showDefinition(resultado);
                } else {
                    error();
                }

            } else {
                error();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatusBarHelper.dismissNotification(Meaning.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
