package blockit.project.swatch.blockit.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import blockit.project.swatch.blockit.database.MyDatabaseHelper;
import blockit.project.swatch.blockit.model.PhoneNumber;
import blockit.project.swatch.blockit.model.Word;

/**
 * Created by swatch on 12/17/16.
 */
public class MySMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        MyDatabaseHelper helper = new MyDatabaseHelper(context);

        try {

            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    List<Word> words = helper.getWordList();
                    List<PhoneNumber> numbers = helper.getAllNumbers(1);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();


                    for (PhoneNumber number : numbers) {
                        if (phoneNumber != null && phoneNumber.equals(number.getPhoneNumber())) {
                            helper.insertHistory(phoneNumber, sdf.format(new Date()), message, 1);
                            abortBroadcast();
                            return;
                        }
                    }

                    if (phoneNumber != null) {
                        for (Word word : words) {
                            if (message.contains(word.getWord())) {
                                helper.insertHistory(phoneNumber, sdf.format(new Date()), message, 1);
                                abortBroadcast();
                                return;
                            }
                        }
                    }
                    //Anonymous

                    SharedPreferences preferences = context.getSharedPreferences("my_prefs", 0);
                    if (preferences.contains("asms")) {
                        String pref = preferences.getString("asms", "No");
                        if (pref.equals("Yes")) {
                            if (phoneNumber == null) {
                                helper.insertHistory("Anonymous", sdf.format(new Date()), message, 1);
                                abortBroadcast();
                                return;
                            }
                        }
                    }


                    if (phoneNumber!=null) {
                        if (preferences.contains("usms")) {
                            String pref = preferences.getString("usms", "No");
                            if (pref.equals("Yes")) {
                                if (blockUnknownNumber(phoneNumber, context)) {
                                    helper.insertHistory(phoneNumber, sdf.format(new Date()), message, 1);
                                    abortBroadcast();

                                }
                            }
                        }

                    }

                }
            }

        } catch (Exception e) {
            Log.e("exceptionSMS", "Exception smsReceiver" + e);

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

                        Log.d("exception",phoneNumber);

                        if (phoneNumber.equals(number)) {
                            return false;
                        }

                    }

                    result.close();

                }

            }

        }

        return true;

    }

}
