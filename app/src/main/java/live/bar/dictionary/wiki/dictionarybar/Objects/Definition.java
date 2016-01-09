package live.bar.dictionary.wiki.dictionarybar.Objects;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import live.bar.dictionary.wiki.dictionarybar.R;

/**
 * Created by victor on 10/05/15.
 */
public class Definition implements Parcelable {

    private String originalWord;
    private String shortDefinition;
    private String[] fullDefinition;

    public Definition(String originalWord, String shortDefinition, String[] fullDefinition) {
        this.originalWord = originalWord;
        this.shortDefinition = shortDefinition;
        this.fullDefinition = fullDefinition;
    }

    public Definition(Context c, String originalWord) {
        this.originalWord = originalWord;
        this.shortDefinition = c.getString(R.string.loading);
        this.fullDefinition = null;
    }

    public Definition(Context c) {
        this.originalWord = c.getString(R.string.loading);
        this.shortDefinition = c.getString(R.string.loading);
        this.fullDefinition = null;
    }

    public String getOriginalWord() {
        return originalWord;
    }

    public void setOriginalWord(String originalWord) {
        this.originalWord = originalWord;
    }

    public String getShortDefinition() {
        return shortDefinition;
    }

    public void setShortDefinition(String shortDefinition) {
        this.shortDefinition = shortDefinition;
    }

    public String[] getFullDefinition() {
        return fullDefinition;
    }

    public void setFullDefinition(String[] fullDefinition) {
        this.fullDefinition = fullDefinition;
    }

    public boolean isWord() {
        return originalWord!=null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeArray(new Object[] {originalWord, shortDefinition, fullDefinition});
    }

    public static final Creator<Definition> CREATOR =  new Creator<Definition>() {
        @Override
        public Definition createFromParcel(Parcel source) {
            return new Definition(source);
        }

        @Override
        public Definition[] newArray(int size) {
            return new Definition[size];
        }
    };

    private Definition(Parcel in) {
        Object[] o = in.readArray(null);
        originalWord = (String) o[0];
        shortDefinition = (String) o[1];
        fullDefinition = (String[]) o[2];
    }
}
