package com.example.covid_19contacttracingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class LoginDAO extends SQLiteOpenHelper{

    private final String TAG="LoginDAO";

    private final String TABLE_NAME="RegisteredUsers";
    private final String COL_USERNAME="Username";
    private final String COL_PASSWORD="Password";

    public final String CREATE_TABLE="CREATE TABLE " + TABLE_NAME +
                                    " (" + COL_USERNAME + " TEXT PRIMARY KEY, "
                                    +COL_PASSWORD+" TEXT);";

    public final String DROP_TABLE="DROP TABLE IF EXISTS "+TABLE_NAME;


    public LoginDAO(@Nullable Context context) {
        super(context, "Login.db", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG,"before Created database");
        db.execSQL(CREATE_TABLE);
        Log.i(TAG,"Created database");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
        Log.i(TAG,"Upgraded database version");
    }

    public Boolean registerUser(User u) {
        SQLiteDatabase writeableDatabase=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(COL_USERNAME, u.getUsername());
        values.put(COL_PASSWORD, u.getPassword());

        long insertedData=writeableDatabase.insert(TABLE_NAME,null,values);
        Log.i(TAG, "Tried to insert:"+insertedData);
        writeableDatabase.close();
        if (insertedData==-1){
            return false;
        }
        else{
            return true;
        }
    }

    public Boolean checkUserName(String username){
        SQLiteDatabase readableDatabase=getReadableDatabase();
        String[] FIELDS = { COL_USERNAME};
        String WHERE=COL_USERNAME+ "=?";
        String[] ARGS = {username};
        Cursor cursor=readableDatabase.query(TABLE_NAME,FIELDS,WHERE,ARGS,null,null,null );

        if (cursor.getCount()>0){
            while(cursor.moveToNext()) {
                String u = cursor.getString(cursor.getColumnIndex(COL_USERNAME));
                Log.i(TAG, "u:"+u);
            }
            readableDatabase.close();
            return true;
        }
        else{
            readableDatabase.close();
            return false;
        }
    }

    public Boolean checkUserNamePassword(String username, String password){
        SQLiteDatabase readableDatabase=getReadableDatabase();
        String[] FIELDS = { COL_USERNAME, COL_PASSWORD};
        String WHERE=COL_USERNAME+ "=?" + " and " + COL_PASSWORD+ "=?";
        String[] ARGS = {username, password};
        Cursor cursor=readableDatabase.query(TABLE_NAME,FIELDS,WHERE,ARGS,null,null,null );

        if (cursor.getCount()>0){
            while(cursor.moveToNext()) {
                String u = cursor.getString(cursor.getColumnIndex(COL_USERNAME));
                String p = cursor.getString(cursor.getColumnIndex(COL_PASSWORD));
                Log.i(TAG, "u:"+u+ "p:"+p);
            }
            readableDatabase.close();
            return true;
        }
        else{
            readableDatabase.close();
            return false;
        }
    }
}