package blockit.project.swatch.blockit.application;


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

/**
 * Created by swatch on 12/12/16.
 */
public class DrawListAdapter extends BaseAdapter {
    private static final String SMS ="SMS" ;
    private static final String CALL ="CALL" ;
    String[] menuList;
    LayoutInflater inflater;
    DrawListAdapter(LayoutInflater inflater,String[] listOfItems){
        this.inflater=inflater;
        menuList=listOfItems;
    }

    @Override
    public int getCount() {
        return menuList.length+1;
    }

    @Override
    public Object getItem(int position) {
        return menuList[position-1];
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {   View listItems=null;

        if(position!=0) {
            listItems = inflater.inflate(R.layout.drawer_list_item, parent, false);
            String menuString = (String) getItem(position);
            TextView textView = ((TextView) listItems.findViewById(R.id.text_element));
            if (textView != null) {
                ImageView imageView = ((ImageView) listItems.findViewById(R.id.text_image));
                if (menuString.equals(SMS)) {
                    imageView.setImageResource(R.drawable.smsblocker);
                } else if (menuString.equals(CALL)) {
                    imageView.setImageResource(R.drawable.callblocker);
                }else{
                    imageView.setImageResource(R.drawable.bullet);
                    if(!menuString.equals("History")){
                        TextView counter = ((TextView) listItems.findViewById(R.id.blockit_counter));
                        counter.setVisibility(View.GONE);
                    }
                }
                textView.setText(menuString);
            }
        }else{
            final View profile = inflater.inflate(R.layout.welcome_user_layout, parent, false);
            AccessToken accessToken=AccessToken.getCurrentAccessToken();
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                Log.i("Response",response.toString());
                                if(response.getJSONObject()!=null) {

                                    String firstName = response.getJSONObject().getString("first_name");
                                    String lastName = response.getJSONObject().getString("last_name");

                                    TextView textView = ((TextView) profile.findViewById(R.id.user_textView));
                                    textView.setText("Welcome, " + firstName + " " + lastName);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "first_name,last_name");
            request.setParameters(parameters);
            request.executeAsync();
            return profile;
        }
        return listItems;
    }
}
