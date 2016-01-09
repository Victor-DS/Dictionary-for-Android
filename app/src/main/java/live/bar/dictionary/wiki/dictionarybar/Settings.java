package live.bar.dictionary.wiki.dictionarybar;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by victor on 15/05/15.
 */
public class Settings extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
