package live.bar.dictionary.wiki.dictionarybar.Receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * When booting is completed, it starts {@link ClipboardMonitor} service to
 * monitor the states of clipboard.
 */
public class ClipboardMonitorStarter extends BroadcastReceiver {
    /* This class should be public; otherwise, the system don't have privilege
     * to instantiate it and cause exception occurs.
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            ComponentName service = context.startService(
                    new Intent(context, ClipboardMonitor.class));
            if (service == null) {
                Log.e(getClass().getName(), "Can't start service "
                        + ClipboardMonitor.class.getName());
            }
        } else {
            Log.e(getClass().getName(), "Recieved unexpected intent " + intent.toString());
        }
    }
}