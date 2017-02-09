package blockit.project.swatch.blockit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import blockit.project.swatch.blockit.application.ApplicationActivity;

public class MainActivity extends AppCompatActivity {
    LoginButton loginButton;
    CallbackManager callbackManager;
    private final String TAG="LBC";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);

        if(isLoggedIn()){
            Intent intent = new Intent(getApplicationContext(), ApplicationActivity.class);
            startActivity(intent);
            finish();
        }else {

            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions("email");
            callbackManager = CallbackManager.Factory.create();
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG, "Logged in");
                    Intent intent = new Intent(getApplicationContext(), ApplicationActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "Cancel ");
                }

                @Override
                public void onError(FacebookException exception) {
                    Log.d(TAG, "Error");
                }
            });

            findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MainActivity.this,blockit.project.swatch.blockit.offline.ApplicationActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }

    }
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
