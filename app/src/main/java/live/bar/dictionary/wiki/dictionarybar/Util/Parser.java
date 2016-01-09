package live.bar.dictionary.wiki.dictionarybar.Util;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Iterator;
import live.bar.dictionary.wiki.dictionarybar.Objects.Definition;
import live.bar.dictionary.wiki.dictionarybar.R;

/**
 * Created by victor on 26/04/15.
 */
public class Parser {

    public static Definition getWordDefinition(Context c, String json)
            throws JSONException, IOException{
        Definition definition = new Definition(c);

        JSONObject jObject = new JSONObject(json).getJSONObject("query").getJSONObject("pages");

        Iterator keys = jObject.keys();

        while(keys.hasNext()) {
            String dynamicKey = (String) keys.next();

            JSONObject jO = jObject.getJSONObject(dynamicKey);

            if(jO.has("title") && !jO.equals("-1")) {
                definition.setOriginalWord(jO.getString("title"));
                definition.setShortDefinition(WikitionaryHelper
                        .getShortDescription(c, jO.getString("extract")));
                definition.setFullDefinition(WikitionaryHelper
                        .getFullDescription(c, jO.getString("extract")));
            } else if (jO.has("title") && jO.equals("-1")) {
                definition.setOriginalWord(c.getString(R.string.no_match));
                definition.setShortDefinition(c.getString(R.string.no_match));
                definition.setFullDefinition(new String[] {c.getString(R.string.no_match)});
            }

        }

        return definition;
    }

    public static String[] getWords(String json) throws JSONException{
        JSONArray jArray = new JSONArray(json).getJSONArray(1);

        String[] words = new String[jArray.length()];

        for(int i = 0; i < jArray.length(); i++)
            words[i] = jArray.getString(i);

        return words;
    }
}
