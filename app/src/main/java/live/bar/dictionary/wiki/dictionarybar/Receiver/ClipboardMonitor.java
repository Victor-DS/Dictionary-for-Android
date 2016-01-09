package live.bar.dictionary.wiki.dictionarybar.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.FileObserver;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashSet;
import java.util.Set;

import live.bar.dictionary.wiki.dictionarybar.GoogleAnalytics.AnalyticsApplication;
import live.bar.dictionary.wiki.dictionarybar.R;
import live.bar.dictionary.wiki.dictionarybar.Util.ClipboardHelper;
import live.bar.dictionary.wiki.dictionarybar.Util.NetworkUtil;
import live.bar.dictionary.wiki.dictionarybar.Util.StatusBarHelper;
import live.bar.dictionary.wiki.dictionarybar.Util.Util;

/**
 * Starts a background thread to monitor the states of clipboard and stores
 * any new clips into the SQLite database.
 * <p>
 * <i>Note:</i> the current android clipboard system service only supports
 * text clips, so in browser, we can just save images to external storage
 * (SD card). This service also monitors the downloads of browser, if any
 * image is detected, it will be stored into SQLite database, too.
 */
public class ClipboardMonitor extends Service {

    private MonitorTask mTask = new MonitorTask();
    private ClipboardManager cManager;
    private Tracker t;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        cManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        mTask.start();
        t = ((AnalyticsApplication) getApplication())
                .getTracker(AnalyticsApplication.TrackerName.APP_TRACKER);
    }

    @Override
    public void onDestroy() {
        mTask.cancel();
    }

    /**
     * Monitor task: monitor new text clips in global system clipboard
     */
    private class MonitorTask extends Thread {

        private volatile boolean mKeepRunning = false;

        public MonitorTask() {
            super("ClipboardMonitor");
        }

        /** Cancel task */
        public void cancel() {
            mKeepRunning = false;
            interrupt();
        }

        @Override
        public void run() {
            mKeepRunning = true;
            while (true) {
                doTask();
                /*
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
                */
                if (!mKeepRunning) {
                    break;
                }
            }
        }

        private void doTask() {
            if (cManager.hasPrimaryClip()) {
                String newClip = ClipboardHelper.getWord(cManager);
                if (!newClip.equals(Util.getLastWord(ClipboardMonitor.this))
                        && !newClip.isEmpty()) {
                    Log.i(getClass().getName(), "detect new text clip: " + newClip.toString());
                    Util.setLastWord(ClipboardMonitor.this, newClip);
                    Log.i(getClass().getName(), "new text clip inserted: " + newClip.toString());
                    if(!ClipboardHelper.isTooBig(newClip) &&
                            ClipboardHelper.hasWord(newClip) &&
                            checkConnection() && showNotification())
                        new StatusBarHelper.AsyncNotification(ClipboardMonitor.this)
                                .execute(ClipboardHelper.getWord(cManager));
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Off app")
                            .setAction("Word notification")
                            .setLabel("Sent user a word notification")
                            .build());

                }
            }
        }
    }

    private boolean checkConnection() {
        if(PreferenceManager.getDefaultSharedPreferences(ClipboardMonitor.this).getBoolean("useData", true))
            return true;

        return NetworkUtil.isUsingWifi(this);
    }

    private boolean showNotification() {
        return PreferenceManager.getDefaultSharedPreferences(ClipboardMonitor.this).getBoolean("showNotification",
                true);
    }
}