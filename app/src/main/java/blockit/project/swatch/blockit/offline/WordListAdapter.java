package blockit.project.swatch.blockit.offline;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import blockit.project.swatch.blockit.R;
import blockit.project.swatch.blockit.model.PhoneNumber;
import blockit.project.swatch.blockit.model.Word;

/**
 * Created by swatch on 12/12/16.
 */
public class WordListAdapter extends BaseAdapter {
    List<Word> menuList;
    LayoutInflater inflater;

    WordListAdapter(LayoutInflater inflater, List<Word> listOfItems) {
        this.inflater = inflater;
        menuList = listOfItems;
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItems = null;

        listItems = inflater.inflate(R.layout.phone_numbers_layout, parent, false);
        String menuString = ((Word) getItem(position)).getWord();
        TextView textView = ((TextView) listItems.findViewById(R.id.phone_numbers_layout_item));
        if (textView != null) {
            textView.setText(menuString);
        }

        return listItems;
    }
}
