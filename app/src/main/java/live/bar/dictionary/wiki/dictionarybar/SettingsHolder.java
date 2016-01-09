package live.bar.dictionary.wiki.dictionarybar;

import android.app.Activity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import live.bar.dictionary.wiki.dictionarybar.GoogleAnalytics.AnalyticsApplication;
import live.bar.dictionary.wiki.dictionarybar.Util.Util;

/**
 * Created by victor on 17/05/15.
 */
public class SettingsHolder extends Activity {

    private Tracker t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new Settings())
                .commit();

        t = ((AnalyticsApplication) getApplication())
                .getTracker(AnalyticsApplication.TrackerName.APP_TRACKER);
        t.setScreenName("Home");
        t.setLanguage(Util.getApiEndpoint(this));
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
