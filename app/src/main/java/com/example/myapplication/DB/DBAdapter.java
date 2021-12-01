    package com.example.myapplication.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBAdapter {
    private static final String DB_NAME = "properties.db";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "tb_properties";

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_TYPE = "type";

    private SQLiteDatabase db;
    private final Context context;
    private DBOpenHelper dbOpenHelper;

    public DBAdapter(Context _context) {
        context = _context;
    }

    /** Close the database */
    public void close() {
        if (db != null){
            db.close();
            db = null;
        }
    }

    /** Open the database */
    public void open() throws SQLiteException {
        dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbOpenHelper.getWritableDatabase();
        }
        catch (SQLiteException ex) {
            db = dbOpenHelper.getReadableDatabase();
        }
    }

    public long insert(Properties properties) {
        ContentValues newValues = new ContentValues();

        newValues.put(KEY_NAME, properties.getName());
        newValues.put(KEY_TYPE, properties.getType());

        return db.insert(DB_TABLE, null, newValues);
    }

    public Properties[] queryAllData() {
        Cursor results =  db.query(DB_TABLE, new String[] { KEY_ID, KEY_NAME, KEY_TYPE},
                null, null, null, null, null);
        return ConvertToRules(results);
    }

    public Properties[] queryOneData(long id) {
        Cursor results =  db.query(DB_TABLE, new String[] { KEY_ID, KEY_NAME, KEY_TYPE},
                KEY_ID + "=" + id, null, null, null, null);
        return ConvertToRules(results);
    }

    private Properties[] ConvertToRules(Cursor cursor){
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()){
            return null;
        }
        Properties[] properties = new Properties[resultCounts];
        for (int i = 0 ; i<resultCounts; i++){
            properties[i] = new Properties();
            properties[i].setID(cursor.getInt(0));
            properties[i].setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));;
            properties[i].setType(cursor.getString(cursor.getColumnIndex(KEY_TYPE)));

            cursor.moveToNext();
        }
        return properties;
    }

    public long deleteAllData() {
        return db.delete(DB_TABLE, null, null);
    }

    public long deleteOneData(long id) {
        return db.delete(DB_TABLE,  KEY_ID + "=" + id, null);
    }

    public long updateOneData(long id , Properties properties){
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_NAME, properties.getName());
        updateValues.put(KEY_TYPE, properties.getType());

        return db.update(DB_TABLE, updateValues,  KEY_ID + "=" + id, null);
    }
    /** 静态Helper类，用于建立、更新和打开数据库*/
    private static class DBOpenHelper extends SQLiteOpenHelper {

        public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        private static final String DB_CREATE ="create table " +
                DB_TABLE + " (" + KEY_ID + " integer primary key autoincrement, " +
                KEY_NAME+ " text not null, " + KEY_TYPE+ " text not null);";

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
            _db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(_db);
        }
    }
}


