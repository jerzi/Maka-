package sk.tuke.smart.maka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "makacDB.db";
    private static final String TABLE_MAKAC = "makac";

    public static final String COLUMN_ID = "_id";
    public static final String TIME = "time";
    public static final String PACE = "pace";
    public static final String DISTANCE = "distance";


    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MAKAC_TABLE = "CREATE TABLE " +
                TABLE_MAKAC + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + TIME + " TEXT,"
                + PACE + " TEXT,"
                + DISTANCE + " TEXT" + ")";

        db.execSQL(CREATE_MAKAC_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAKAC);
    }

    public void addItem(String time, String pace, String distance) {
        ContentValues values = new ContentValues();
        values.put(TIME, time);
        values.put(PACE, pace);
        values.put(DISTANCE, distance);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_MAKAC, null,values);
        db.close();
    }

    public String[] findItem(String time){

        String[] items = new String[3];
        String query = "Select * FROM " + TABLE_MAKAC + " WHERE " + TIME + " =  \"" + time + "\"";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            items[0] = cursor.getString(1);
            items[1] = cursor.getString(2);
            items[2] = cursor.getString(3);
            cursor.close();
        } else {
            items = null;
        }
        db.close();

        return items;
    }

    public String[] getFirstItem() {
        String[] items = new String[3];

        String query = "Select * FROM " + TABLE_MAKAC;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            items[0] = cursor.getString(1);
            items[1] = cursor.getString(2);
            items[2] = cursor.getString(3);
            cursor.close();
        } else {
            items = null;
        }
        db.close();

        return items;
    }

    public ArrayList<Result> getAllEntries() {
        ArrayList<Result> list = new ArrayList<>();

        String query = "Select * FROM " + TABLE_MAKAC;

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToLast();

        do{
                list.add(new Result(cursor.getString(1), cursor.getString(2), cursor.getString(3)));

        }while(cursor.moveToPrevious());

        return list;
    }

    public void cleanDatabase() {
        SQLiteDatabase db = getReadableDatabase();

        db.execSQL("delete from " + TABLE_MAKAC);
    }

}
