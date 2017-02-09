package blockit.project.swatch.blockit.offline;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceActivity;

import blockit.project.swatch.blockit.R;


public class MyPrefsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName("my_prefs");
        addPreferencesFromResource(R.xml.my_preference);
    }
}
