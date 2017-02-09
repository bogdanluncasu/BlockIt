package blockit.project.swatch.blockit.offline;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.DefaultAdListener;

import blockit.project.swatch.blockit.R;
import blockit.project.swatch.blockit.database.MyDatabaseHelper;
import blockit.project.swatch.blockit.model.History;
import blockit.project.swatch.blockit.model.PhoneNumber;
import blockit.project.swatch.blockit.model.Word;


public class ApplicationActivity extends AppCompatActivity {
    private AdLayout adView;
    String[] menuItems,smsOptions,callOptions;
    DrawerLayout drawerLayout;
    ListView drawerList,numberList,wordsList,historyList;
    MyDatabaseHelper databaseHelper;
    AlertDialog dialog;
    private ActionBarDrawerToggle mDrawerToggle;

    static final String TAG = "LBC";

    private static final String SMS ="SMS" ;
    private static final String CALL ="CALL" ;

    private int menuType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

        AdRegistration.setAppKey("6c5c58c16bb648918c795aa3fad14e7e");

        databaseHelper= new MyDatabaseHelper(this);
        this.adView = (AdLayout) findViewById(R.id.adview);
        this.adView.enableAutoShow();
        this.adView.loadAd();
        this.adView.showAd();

        menuType=0;
        menuItems = getResources().getStringArray(R.array.menu_items);
        smsOptions = getResources().getStringArray(R.array.menu_sms);
        callOptions = getResources().getStringArray(R.array.menu_call);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerList.setAdapter(new DrawListAdapter(getLayoutInflater(), menuItems,databaseHelper));

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
                    drawerList.setAdapter(new DrawListAdapter(getLayoutInflater(),smsOptions,databaseHelper));

                }else if(clickedOption.equals(CALL)){
                    menuType=2;
                    drawerList.setAdapter(new DrawListAdapter(getLayoutInflater(),callOptions,databaseHelper));

                }
            }else{
                FrameLayout layout=(FrameLayout)findViewById(R.id.content_frame);
                if(layout.getChildCount()>0){
                    layout.removeAllViews();
                }
                if(position==0){
                    menuType=0;
                    drawerList.setAdapter(new DrawListAdapter(getLayoutInflater(),menuItems,databaseHelper));
                }else if(menuType==1){
                    if(position==1){
                        settingListNumbers(menuType,layout);
                    }else if(position==2){
                        settingListWords(layout);
                    }else{
                        settingListHistory(menuType,layout);
                    }
                }else if(menuType==2){
                    if(position==1){
                        settingListNumbers(menuType,layout);

                    }else{
                        settingListHistory(menuType,layout);
                    }
                }
            }
        }
    }

    public void settingListNumbers(final int menuType,FrameLayout layout){
        getLayoutInflater().inflate(R.layout.offline_phone_numbers,layout);

        numberList= (ListView) findViewById(R.id.phone_numbers);

        numberList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final PhoneNumber number= (PhoneNumber) numberList.getAdapter().getItem(position);
                AlertDialog.Builder builder=new AlertDialog.Builder(ApplicationActivity.this);
                builder.setTitle("Delete Number");
                builder.setMessage("You want to delete this number?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.deleteNumber(number.getId());
                        numberList.setAdapter(new NumberListAdapter(getLayoutInflater(),databaseHelper.getAllNumbers(menuType)));
                    }
                });
                builder.setNegativeButton("No",null);
                builder.create().show();
                return true;
            }
        });
        findViewById(R.id.add_number).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addNumber(menuType);
                    }
                }
        );
        numberList.setAdapter(new NumberListAdapter(getLayoutInflater(),databaseHelper.getAllNumbers(menuType)));

        drawerLayout.closeDrawer(GravityCompat.START,true);
    }



    public void settingListWords(FrameLayout layout){
        getLayoutInflater().inflate(R.layout.offline_phone_numbers,layout);

        wordsList= (ListView) findViewById(R.id.phone_numbers);

        wordsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Word word= (Word) wordsList.getAdapter().getItem(position);
                AlertDialog.Builder builder=new AlertDialog.Builder(ApplicationActivity.this);
                builder.setTitle("Delete Word");
                builder.setMessage("You want to delete this word?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.deleteWord(word.getId());
                        wordsList.setAdapter(new WordListAdapter(getLayoutInflater(),databaseHelper.getWordList()));
                    }
                });
                builder.setNegativeButton("No",null);
                builder.create().show();
                return true;
            }
        });
        findViewById(R.id.add_number).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addWord();
                    }
                }
        );
        wordsList.setAdapter(new WordListAdapter(getLayoutInflater(),databaseHelper.getWordList()));

        drawerLayout.closeDrawer(GravityCompat.START,true);
    }

    public void settingListHistory(final int type,FrameLayout layout){
        getLayoutInflater().inflate(R.layout.history_layout,layout);

        historyList= (ListView) findViewById(R.id.history_list);

        historyList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final History history= (History) historyList.getAdapter().getItem(position);
                AlertDialog.Builder builder=new AlertDialog.Builder(ApplicationActivity.this);
                builder.setTitle("Delete data");
                builder.setMessage("You want to delete this?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.deleteHistory(history.getId());
                        historyList.setAdapter(new HistoryListAdapter(getLayoutInflater(),databaseHelper.getHistoryList(type),type));
                    }
                });
                builder.setNegativeButton("No",null);
                builder.create().show();
                return true;
            }
        });
        if(databaseHelper==null){
            Log.d("exceptiones","Helper is null");
        }
        HistoryListAdapter adapter=new HistoryListAdapter(getLayoutInflater(),databaseHelper.getHistoryList(type),type);
        historyList.setAdapter(adapter);
        drawerLayout.closeDrawer(GravityCompat.START,true);
    }

    public void addNumber(final Integer type){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(this.getLayoutInflater().inflate(R.layout.dialogbox_add_layout,null));
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int which) {
                String number=((EditText)((AlertDialog)d).findViewById(R.id.phone))
                        .getText().toString();

                boolean ok=true;
                for(PhoneNumber tmp:databaseHelper.getAllNumbers(type)){
                    if(tmp.getPhoneNumber().equals(number)){
                        ok=false;
                        break;
                    }
                }
                if(ok&&number.length()>0) {
                    databaseHelper.insertPhoneNumber(number, type);
                    numberList.setAdapter(new NumberListAdapter(getLayoutInflater(), databaseHelper.getAllNumbers(type)));
                }
            }
        });
        AlertDialog dialog=builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();
    }


    public void addWord(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setView(this.getLayoutInflater().inflate(R.layout.dialogbox_add__word_layout,null));
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface d, int which) {
                String word=((EditText)((AlertDialog)d).findViewById(R.id.word))
                        .getText().toString();

                boolean ok=true;
                for(Word tmp:databaseHelper.getWordList()){
                    if(tmp.getWord().equals(word)){
                        ok=false;
                        break;
                    }
                }
                if(ok&&word.length()>0) {
                    databaseHelper.insertForbiddenWord(word);
                    wordsList.setAdapter(new WordListAdapter(getLayoutInflater(), databaseHelper.getWordList()));
                }
            }
        });
        AlertDialog dialog=builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pref_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(ApplicationActivity.this,MyPrefsActivity.class));
                return true;
            default:
                return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}

