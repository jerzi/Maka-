package sk.tuke.smart.maka;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class AchievementDatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "Achievements.db";

    public static final String TABLE_NAME = "achievements";
    public static final String VALUE_NAME = "compVal";

    public static final String COLUMN_ID = "_id";
    public static final String NAME = "name";
    public static final String DESC = "desc";
    public static final String TIME = "time";
    public static final String PACE = "pace";
    public static final String DISTANCE = "distance";
    private static final String BOOL = "active";

    private String path = "achievements.txt";
    
    private Context context;


    public AchievementDatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ACHIEVEMENT_TABLE = "CREATE TABLE " +
                TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + NAME + " TEXT,"
                + DESC + " TEXT,"
                + TIME + " TEXT,"
                + PACE + " TEXT,"
                + DISTANCE + " TEXT,"
                + BOOL + " TEXT DEFAULT LOCKED"
                + ")";

        db.execSQL(CREATE_ACHIEVEMENT_TABLE);

        String CREATE_VALUE_TABLE = "CREATE TABLE " +
                VALUE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + TIME + " TEXT,"
                + PACE + " TEXT,"
                + DISTANCE + " TEXT" + ")";

        db.execSQL(CREATE_VALUE_TABLE);
        
        readAchievements(db);
        initValues(db);
    }

    private void initValues(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(TIME, "00:00.00");
        values.put(PACE, "0.00");
        values.put(DISTANCE,"0.00");

        db.insert(VALUE_NAME, null, values);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean isEmpty(String table){
        String query = "Select * FROM " + table;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        boolean isEmpty = !cursor.moveToFirst();
        db.close();
        return isEmpty;
    }

    public void workWithValues(String time, String pace, String distance) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues value = new ContentValues();

            value.put(TIME, time);
            value.put(DISTANCE, pace);
            value.put(PACE, distance);
            db.update(VALUE_NAME,value, null,null);

        db.close();

    }

    private void readAchievements(SQLiteDatabase db){
        ContentValues values = new ContentValues();

        List<Achievement> achievements = new AchievementReader().read(context, path);

        for(Achievement achievement : achievements ){
            values.put(NAME, achievement.getName());
            values.put(DESC, achievement.getDescription());
            values.put(TIME, achievement.getTime());
            values.put(PACE, achievement.getPace());
            values.put(DISTANCE, achievement.getDistance());

            db.insert(TABLE_NAME, null,values);
        }

    }

    public List<Achievement> getAchievements() {
        ArrayList<Achievement> list = new ArrayList<>();

        String query = "Select * FROM " + TABLE_NAME;

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        do{
            list.add(new Achievement(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)));

        }while(cursor.moveToNext());

        return list;
    }

    public void upDateAchievement(String name){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BOOL, "UNLOCKED");

        db.update(TABLE_NAME, contentValues, NAME + " like " + "'"+name+"'", null);
        db.close();
    }

    public Result getValues(){
        String query = "Select * FROM " + VALUE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        return new Result(cursor.getString(1), cursor.getString(2), cursor.getString(3));
    }

    private class AchievementReader{
        public List<Achievement> read (Context context, String path) {
            List<Achievement> achievements = new ArrayList<>();

            AssetManager assetManager = context.getAssets();

            try {
                InputStream input = assetManager.open(path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line, name, description,time,distance,pace;
                String[] parts;

                while((line = reader.readLine()) != null){
                    parts = line.split("-");
                    name = parts[0];
                    description = parts[1];
                    time = parts[2];
                    distance = parts[3];
                    pace = parts[4];

                    achievements.add(new Achievement(name, description,time, pace, distance, "LOCKED"));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            return achievements;
        }

    }

}

