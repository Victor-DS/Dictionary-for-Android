package live.bar.dictionary.wiki.dictionarybar.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import live.bar.dictionary.wiki.dictionarybar.R;

/**
 * Created by victor on 15/05/15.
 */
public class DefinitionAdapter extends BaseAdapter{

    private String[] definitions;
    private Context mContext;

    public DefinitionAdapter(Context c, String[] definitions) {
        this.definitions = definitions;
        mContext = c;
    }

    @Override
    public int getCount() {
        return definitions.length;
    }

    @Override
    public Object getItem(int position) {
        return definitions[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.row_definition, parent, false);
        }

        final TextView definition = (TextView) row.findViewById(R.id.tvDefinition);
        definition.setText(Html.fromHtml(definitions[position]));

        if(definitions[position].isEmpty())
            row.setVisibility(View.GONE);

        return row;
    }
}
