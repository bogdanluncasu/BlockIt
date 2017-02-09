package blockit.project.swatch.blockit.application;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import blockit.project.swatch.blockit.R;

/**
 * Created by swatch on 12/12/16.
 */
public class NumberListAdapter extends BaseAdapter {
    String[] menuList;
    LayoutInflater inflater;

    NumberListAdapter(LayoutInflater inflater, String[] listOfItems) {
        this.inflater = inflater;
        menuList = listOfItems;
    }

    @Override
    public int getCount() {
        return menuList.length;
    }

    @Override
    public Object getItem(int position) {
        return menuList[position];
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItems = null;

        listItems = inflater.inflate(R.layout.phone_numbers_layout, parent, false);
        String menuString = (String) getItem(position);
        TextView textView = ((TextView) listItems.findViewById(R.id.phone_numbers_layout_item));
        if (textView != null) {
            textView.setText(menuString);
        }

        return listItems;
    }
}
