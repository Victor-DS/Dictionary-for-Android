package live.bar.dictionary.wiki.dictionarybar.Util;

import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;

import org.jsoup.Jsoup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import live.bar.dictionary.wiki.dictionarybar.R;

/**
 * Created by victor on 10/05/15.
 */
public class ClipboardHelper {

    private static boolean hasText(Context c) {
        ClipboardManager clipboard = (ClipboardManager)
                c.getSystemService(Context.CLIPBOARD_SERVICE);

        return clipboard.hasPrimaryClip() &&
                clipboard.getPrimaryClipDescription()
                        .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
    }

    public static boolean hasWord(String text) {
        return !text.isEmpty();
        //TODO Verify if is a word, aka contains white space. Or check online? Will be like this for now.
        //return isWord(getText(c));
    }

    private static boolean isWord(String s) {
        return s.trim().contains(" ");
    }

    public static String getWord(Context c) {
        ClipboardManager clipboard = (ClipboardManager)
                c.getSystemService(Context.CLIPBOARD_SERVICE);
        return getWord(clipboard);
    }

    public static String getWord(ClipboardManager clipboard) {
        if(clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
            return String.valueOf(clipboard.getPrimaryClip().getItemAt(0).getText());
        else if(clipboard.getPrimaryClipDescription()
                .hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML))
            return Jsoup.parse(clipboard.getPrimaryClip().getItemAt(0).getHtmlText().toString())
                    .text().replaceAll("\\p{P}", "").trim();
        else return "";
    }

    public static boolean isTooBig(String s) {
        return s.length() >= 50;
    }
}
