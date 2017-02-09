package blockit.project.swatch.blockit.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import blockit.project.swatch.blockit.model.History;
import blockit.project.swatch.blockit.model.PhoneNumber;
import blockit.project.swatch.blockit.model.Word;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "blockit.db";
    private static final String NUMBERS_TABLE = "phone_numbers";
    private static final String WORDS_TABLE = "words";
    private static final String HISTORY_TABLE = "history";

    private static final String ID = "id";
    private static final String PHONE = "phone_number";
    private static final String TYPE = "type";

    private static final String WORD = "word";

    private static final String SENDER = "sender";
    private static final String CONTENT = "content";
    private static final String TIMESTAMP = "timestamp";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + NUMBERS_TABLE +
                "( " + ID + " integer  primary key autoincrement," +
                PHONE + " text," + TYPE + " integer);");

        db.execSQL("create table " + WORDS_TABLE +
                "(" + ID + " integer  primary key autoincrement," +
                WORD + " text);");

        db.execSQL("create table " + HISTORY_TABLE +
                "(" + ID + " integer  primary key autoincrement," +
                SENDER + " text, "+CONTENT+" text,"+TYPE+" integer,"+TIMESTAMP+" text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertPhoneNumber(String number, Integer type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(PHONE, number);
        content.put(TYPE, type);
        db.insert(NUMBERS_TABLE, null, content);
    }

    public void insertForbiddenWord(String word) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(WORD, word);
        db.insert(WORDS_TABLE, null, content);
    }

    public void insertHistory(String sender,String date,String smscontent,Integer type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(SENDER,sender);
        content.put(TIMESTAMP,date);
        content.put(CONTENT,smscontent);
        content.put(TYPE,type);
        db.insert(HISTORY_TABLE, null, content);
    }


    public void deleteNumber(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(NUMBERS_TABLE, ID + "=?", new String[]{Integer.toString(id)});
    }

    public void deleteWord(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(WORDS_TABLE, ID + "=?", new String[]{Integer.toString(id)});
    }

    public void deleteHistory(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(HISTORY_TABLE, ID + "=?", new String[]{Integer.toString(id)});
    }

    public List<PhoneNumber> getAllNumbers(int type) {
        List<PhoneNumber> numbers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.query(NUMBERS_TABLE, new String[]{ID, PHONE}, TYPE+"=?", new String[]{Integer.toString(type)}, null, null, null);
        if (result != null && result.getCount() > 0) {
            while (result.moveToNext()) {
                PhoneNumber temp = new PhoneNumber(result.getInt(result.getColumnIndex(ID)),
                        result.getString(result.getColumnIndex(PHONE)));
                numbers.add(temp);
            }
        }
        if (result != null) {
            result.close();
        }
        return numbers;
    }

    public List<Word> getWordList() {
        List<Word> words = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.query(WORDS_TABLE, new String[]{ID, WORD}, null, null, null, null, null);
        if (result != null && result.getCount() > 0) {
            while (result.moveToNext()) {
                Word temp = new Word(result.getInt(result.getColumnIndex(ID)),
                        result.getString(result.getColumnIndex(WORD)));
                words.add(temp);
            }
        }
        if (result != null) {
            result.close();
        }
        return words;
    }

    public List<History> getHistoryList(Integer type) {
        List<History> blockedlist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.query(HISTORY_TABLE, new String[]{ID, SENDER,CONTENT,TIMESTAMP}, TYPE+"=?", new String[]{Integer.toString(type)}, null, null, null);
        if (result != null && result.getCount() > 0) {
            while (result.moveToNext()) {
                History temp = new History(result.getInt(result.getColumnIndex(ID)),
                        result.getString(result.getColumnIndex(SENDER)),result.getString(result.getColumnIndex(CONTENT)),
                        result.getString(result.getColumnIndex(TIMESTAMP)));
                blockedlist.add(temp);
            }
        }
        if (result != null) {
            result.close();
        }
        return blockedlist;
    }


}
