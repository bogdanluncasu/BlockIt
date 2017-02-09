package blockit.project.swatch.blockit.offline;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

import blockit.project.swatch.blockit.R;
import blockit.project.swatch.blockit.database.MyDatabaseHelper;

/**
 * Created by swatch on 12/12/16.
 */
public class DrawListAdapter extends BaseAdapter {
    private static final String SMS = "SMS";
    private static final String CALL = "CALL";
    String[] menuList;
    LayoutInflater inflater;
    MyDatabaseHelper helper;

    DrawListAdapter(LayoutInflater inflater, String[] listOfItems,MyDatabaseHelper helper) {
        this.helper=helper;
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


        listItems = inflater.inflate(R.layout.drawer_list_item, parent, false);
        String menuString = (String) getItem(position);
        TextView textView = ((TextView) listItems.findViewById(R.id.text_element));
        if (textView != null) {
            ImageView imageView = ((ImageView) listItems.findViewById(R.id.text_image));
            if (menuString.equals(SMS)) {
                imageView.setImageResource(R.drawable.smsblocker);
                ((TextView) listItems.findViewById(R.id.blockit_counter)).setText(
                        ""+helper.getHistoryList(1).size()
                );
            } else if (menuString.equals(CALL)) {
                imageView.setImageResource(R.drawable.callblocker);
                ((TextView) listItems.findViewById(R.id.blockit_counter)).setText(
                        ""+helper.getHistoryList(2).size()
                );
            } else {
                imageView.setImageResource(R.drawable.bullet);
                TextView counter = ((TextView) listItems.findViewById(R.id.blockit_counter));
                counter.setVisibility(View.GONE);
            }
            textView.setText(menuString);
        }


        return listItems;
    }
}
