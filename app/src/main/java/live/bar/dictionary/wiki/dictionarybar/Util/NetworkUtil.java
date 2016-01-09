package live.bar.dictionary.wiki.dictionarybar.Util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by victor on 18/05/15.
 */
public class NetworkUtil {

    public static boolean isUsingWifi(Context c) {
        return ((ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    }

    public static boolean isUsingData(Context c) {
        return ((ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
    }
}
