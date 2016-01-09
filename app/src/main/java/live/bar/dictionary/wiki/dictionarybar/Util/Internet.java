package live.bar.dictionary.wiki.dictionarybar.Util;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by victor on 26/04/15.
 */
public class Internet {

    private static final int DEFAULT_TIMEOUT = 10000;

    public static String getResponse(String URL) throws IOException {
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_TIMEOUT);
        HttpClient httpclient = new DefaultHttpClient(httpParams);
        HttpGet httpGet = new HttpGet(URL.replaceAll(" ", "%20"));
        HttpResponse response = httpclient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        is.close();
        Log.i("RESPONSE", sb.toString());
        return sb.toString();
    }

    public static String getDefinitionURL(String apiEndpoint, String word) {
        return apiEndpoint + "w/api.php?action=query&prop=extracts&format=json&exsectionformat=raw" +
                "&titles="+word+"&redirects=";
    }

    public static String getWordListURL(String apiEndpoint, String word) {
        return apiEndpoint+"w/api.php?action=opensearch&format=json&search="+word;
    }
}
