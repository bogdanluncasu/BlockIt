package blockit.project.swatch.blockit.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import blockit.project.swatch.blockit.database.MyDatabaseHelper;
import blockit.project.swatch.blockit.model.PhoneNumber;

/**
 * Created by swatch on 12/18/16.
 */
public class MyCallReceiver extends BroadcastReceiver {
    MyDatabaseHelper helper;
    final static int CALL=2;
    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (state != null && state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

            Log.d("exception 126", TelephonyManager.EXTRA_INCOMING_NUMBER);

            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            //Toast.makeText(context, number == null ? "null" : number, Toast.LENGTH_LONG).show();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            SharedPreferences preferences = context.getSharedPreferences("my_prefs", 0);
            helper = new MyDatabaseHelper(context);

            if (number == null) {
                //Anonymous
                if (preferences.contains("acall")) {
                    String pref = preferences.getString("acall", "No");
                    if (pref.equals("Yes")) {
                        helper.insertHistory("Anonymous", sdf.format(new Date()), "", CALL);
                        disconnectPhoneItelephony(context);
                    }
                }
            } else {

                for (PhoneNumber num : helper.getAllNumbers(CALL)) {
                    if (number.equals(num.getPhoneNumber())) {
                        helper.insertHistory(number, sdf.format(new Date()), "", CALL);
                        disconnectPhoneItelephony(context);
                        return;
                    }
                }


                if (preferences.contains("ucall")) {
                    String pref = preferences.getString("ucall", "No");
                    if (pref.equals("Yes")) {
                        if (blockUnknownNumber(number, context)) {
                            helper.insertHistory(number, sdf.format(new Date()), "", CALL);
                            disconnectPhoneItelephony(context);
                        }
                    }
                }
            }
        }


    }

    private boolean blockUnknownNumber(String number, Context context) {

        ContentResolver cRes = context.getContentResolver();

        Cursor c = cRes.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (c.getCount() > 0) {

            while (c.moveToNext()) {

                String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                String tmp = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                int numberID = Integer.parseInt(tmp);
                if (numberID > 0) {

                    Cursor result = cRes.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    while (result.moveToNext()) {

                        String phoneNumber = result.getString(result.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        if (phoneNumber.equals(number)) {
                            c.close();
                            return false;
                        }

                    }

                    result.close();

                }

            }

        }
        c.close();
        return true;

    }

    private void disconnectPhoneItelephony(Context context) {
        ITelephony telephonyService;
        TelephonyManager telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            //telephonyService.silenceRinger();
            telephonyService.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


