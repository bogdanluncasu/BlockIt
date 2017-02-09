package blockit.project.swatch.blockit.offline;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import blockit.project.swatch.blockit.R;
import blockit.project.swatch.blockit.model.History;
import blockit.project.swatch.blockit.model.PhoneNumber;

/**
 * Created by swatch on 12/12/16.
 */
public class HistoryListAdapter extends BaseAdapter {
    List<History> menuList;
    LayoutInflater inflater;
    int type;

    HistoryListAdapter(LayoutInflater inflater, List<History> listOfItems,int type) {
        this.inflater = inflater;
        menuList = listOfItems;
        this.type=type;
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
        if(type==1){
            listItems = inflater.inflate(R.layout.history_sms_item_layout, parent, false);
        }else{
            listItems = inflater.inflate(R.layout.history_call_item_layout, parent, false);
        }

        String sender= ((History) getItem(position)).getSender();
        String date= ((History) getItem(position)).getDate();
        TextView senderView = ((TextView) listItems.findViewById(R.id.sender));
        TextView dateView = ((TextView) listItems.findViewById(R.id.date));

        if (senderView != null && dateView!=null) {
            senderView.setText(sender);
            dateView.setText(date);
            if(type==1){
                ((TextView) listItems.findViewById(R.id.content)).setText(((History) getItem(position)).getContent());
            }
        }

        return listItems;
    }
}
