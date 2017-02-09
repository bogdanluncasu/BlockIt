package blockit.project.swatch.blockit.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import blockit.project.swatch.blockit.R;


public class ApplicationActivity extends AppCompatActivity {

    String[] menuItems,smsOptions,callOptions;
    DrawerLayout drawerLayout;
    ListView drawerList;
    AlertDialog dialog;
    private ActionBarDrawerToggle mDrawerToggle;
    private AccessTokenTracker accessTokenTracker;
    static final String ACTION1 = "android.net.conn.CONNECTIVITY_CHANGE";
    static final String ACTION2 = "android.net.wifi.WIFI_STATE_CHANGED";
    static final String TAG = "LBC";

    private static final String SMS ="SMS" ;
    private static final String CALL ="CALL" ;

    private int menuType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);


        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION1);
        filter.addAction(ACTION2);
        this.registerReceiver(networkReceiver, filter);

        menuType=0;
        menuItems = getResources().getStringArray(R.array.menu_items);
        smsOptions = getResources().getStringArray(R.array.menu_sms);
        callOptions = getResources().getStringArray(R.array.menu_call);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerList.setAdapter(new DrawListAdapter(getLayoutInflater(), menuItems));

        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Options");
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("BlockIt");
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    finish();
                }
            }
        };


    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String clickedOption= (String) drawerList.getAdapter().getItem(position);
            if(menuType==0){
                //sunt in meniul principal
                if(clickedOption.equals(SMS)){
                    menuType=1;
                    drawerList.setAdapter(new DrawListAdapter(getLayoutInflater(),smsOptions));

                }else if(clickedOption.equals(CALL)){
                    menuType=2;
                    drawerList.setAdapter(new DrawListAdapter(getLayoutInflater(),callOptions));

                }
            }else{
                //TODO: afisarea optiunilor apasate,database,incercare de cod nativ,offline
                if(position==1){
                    menuType=0;
                    drawerList.setAdapter(new DrawListAdapter(getLayoutInflater(),menuItems));
                }else if(menuType==1){
                    FrameLayout layout=(FrameLayout)findViewById(R.id.content_frame);
                    if(layout.getChildCount()>0){
                        layout.removeAllViews();
                    }

                    if(position==2){
                        getLayoutInflater().inflate(R.layout.phone_numbers,layout);

                        ListView numberlist= (ListView) findViewById(R.id.phone_numbers);
                        numberlist.setAdapter(new NumberListAdapter(getLayoutInflater(),new String[]{"a","b","c"}));

                        drawerLayout.closeDrawer(GravityCompat.START,true);

                    }else if(position==3){

                    }else{
                        //history
                    }
                }else if(menuType==3){
                    if(position==2){

                    }else{
                        //history
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(networkReceiver);
    }

    private AlertDialog showPopup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("No Internet Connection")
                .setNegativeButton("Go out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginManager.getInstance().logOut();
                    }
                });
        return builder.setCancelable(false).create();
    }

    private final BroadcastReceiver networkReceiver=new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null == activeNetwork) {
                Log.d(TAG, "ActiveNetwork is NULL");
                if(dialog==null) {
                    dialog=showPopup();
                    dialog.show();
                }else{
                    dialog.show();
                }
            }else if(dialog!=null){
                dialog.cancel();
                dialog=null;
                final TextView textView=((TextView)drawerList.getChildAt(0).findViewById(R.id.user_textView));
                if(textView.getText().toString().length()<8){
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
                }
            }
        }


    };
}

