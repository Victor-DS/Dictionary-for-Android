package live.bar.dictionary.wiki.dictionarybar.Util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.json.JSONException;
import java.io.IOException;
import live.bar.dictionary.wiki.dictionarybar.MainActivity;
import live.bar.dictionary.wiki.dictionarybar.Meaning;
import live.bar.dictionary.wiki.dictionarybar.Objects.Definition;
import live.bar.dictionary.wiki.dictionarybar.R;

/**
 * Created by victor on 11/05/15.
 */
public class StatusBarHelper {

    private static void launchNotification(Context c) {
        StatusBarHelper.launchNotification(c, new Definition(c));
    }

    private static void launchNotification(Context c, Definition definition) {
        Intent resultIntent;
        if(!ClipboardHelper.hasWord((definition.getOriginalWord()
                .equals(c.getString(R.string.loading))) ? "" : definition.getOriginalWord()))
            resultIntent = new Intent(c, MainActivity.class);
        else
            resultIntent = new Intent(c, Meaning.class)
                    .putExtra("Definition", definition);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String title = (definition.getOriginalWord()
                .equals(c.getString(R.string.loading))) ? c.getString(R.string.app_name) :
                definition.getOriginalWord();

        int priority = showHeadsUp(c) ? NotificationCompat.PRIORITY_HIGH :
                NotificationCompat.PRIORITY_DEFAULT;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c)
                .setSmallIcon(R.mipmap.ic_stat_book_pictogram)
                .setLargeIcon(BitmapFactory.decodeResource(c.getResources(),
                            R.mipmap.ic_stat_book_pictogram))
                .setContentTitle(title.toUpperCase())
                .setContentText(definition.getShortDefinition())
                .setOngoing(false)
                .setPriority(priority)
                .setVibrate(new long[0])
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(definition.getShortDefinition()))
                .setContentIntent(resultPendingIntent);

        NotificationManager nManager = (NotificationManager)
                c.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(100, mBuilder.build());
    }

    public static void dismissNotification(Context c) {
        NotificationManager nManager = (NotificationManager)
                c.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancel(100);
    }

    public static class AsyncNotification extends AsyncTask<String, Void, String> {

        private Context mContext;

        public AsyncNotification(Context c) {
            super();
            mContext = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //StatusBarHelper.launchNotification(mContext);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return Internet.getResponse(Internet
                        .getDefinitionURL(Util.getApiEndpoint(mContext), params[0].toLowerCase()));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null) {
                try {
                    StatusBarHelper.launchNotification(mContext,
                            Parser.getWordDefinition(mContext, s));

                    if(toastIt(mContext))
                        for(int i = 0; i < 2; i++)
                            Toast.makeText(mContext, Parser.getWordDefinition(mContext, s)
                                    .getShortDefinition(), Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                    StatusBarHelper.launchNotification(mContext,
                            new Definition(mContext.getString(R.string.app_name),
                                    mContext.getString(R.string.failed_to_get_definition),
                                    new String[] {mContext
                                            .getString(R.string.failed_to_get_definition)}));
                } catch (IOException e) {
                    e.printStackTrace();
                    StatusBarHelper.launchNotification(mContext,
                            new Definition(mContext.getString(R.string.app_name),
                                    mContext.getString(R.string.failed_to_get_definition),
                                    new String[] {mContext
                                            .getString(R.string.failed_to_get_definition)}));
                }
            } else {
                StatusBarHelper.launchNotification(mContext,
                        new Definition(mContext.getString(R.string.app_name),
                                mContext.getString(R.string.failed_to_get_definition),
                                new String[] {mContext
                                        .getString(R.string.failed_to_get_definition)}));
            }
        }
    }

    private static boolean toastIt(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c)
                .getBoolean("toastDefinition", false);
    }

    private static boolean showHeadsUp(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c)
                .getBoolean("showHeadsUp", false);
    }
}
