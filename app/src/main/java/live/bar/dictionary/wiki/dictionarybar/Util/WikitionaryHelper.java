package live.bar.dictionary.wiki.dictionarybar.Util;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Collections of methods related to helping with the Wiktionaries stuff.
 * E.g.: Getting a short and full description of the word using the API's
 * HTML response.
 * Created by victor on 12/05/15.
 */
public class WikitionaryHelper {

    //TODO FIX GERMAN and Polish!

    public static String getShortDescription(Context c, String HTML) throws IOException{
        return Jsoup.parse(getFullDescription(c, HTML)[0]).text();
    }

    //TODO Fix Spanish
    public static String[] getFullDescription(Context c, String HTML) throws IOException {
        Document doc = Jsoup.parse(HTML);

        String apiEndpoint = Util.getApiEndpoint(c);

        Elements e;

        if(apiEndpoint.equals("http://es.wiktionary.org/"))
            e = doc.getElementsByTag("dd");
        else if(apiEndpoint.equals("http://ko.wiktionary.org/"))
            e = doc.select("ul").first().getElementsByTag("li");
        else
            e = doc.select("ol").first().getElementsByTag("li");

        String[] meanings = new String[e.size()];

        for(int i = 0; i < meanings.length; i++)
            meanings[i] = e.get(i).outerHtml();

        return meanings;
    }

}
