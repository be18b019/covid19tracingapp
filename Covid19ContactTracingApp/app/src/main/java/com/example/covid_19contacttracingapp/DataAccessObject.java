package com.example.covid_19contacttracingapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataAccessObject extends SQLiteOpenHelper {

    private final String TAG = "DataAccessObject";

    private final String USER_TABLE = "Registered_Users";
    private final String COVID_19_INFO_TABLE = "Covid_Info";
    private final String COL_USERNAME = "Username";
    private final String COL_PASSWORD = "Password";
    private final String COL_LATITUDE = "Latitude_Location";
    private final String COL_LONGITUDE = "Longitude_Location";
    private final String COLUMN_DATETIME = "Date_and_Time";
    private final String COLUMN_COV19_POSITIVE = "Covid_19_Positive";
    private final String COLUMN_ID = "ID";
    private final int DISTANCE_THRESHOLD = 100;
    private final int TIME_DIF_THRESHOLD = 1000*60*60;

    public final String CREATE_USER_TABLE = "CREATE TABLE " + USER_TABLE +
            " (" + COL_USERNAME + " TEXT PRIMARY KEY, "
            + COL_PASSWORD + " TEXT);";
    public final String CREATE_COVID_19_INFO_TABLE = "CREATE TABLE " + COVID_19_INFO_TABLE +
            " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_LATITUDE + " REAL, " + COL_LONGITUDE + " REAL, " + COLUMN_DATETIME + " INT, " + COLUMN_COV19_POSITIVE + " NUMERIC, " + COL_USERNAME + " TEXT, " + " FOREIGN KEY(" + COL_USERNAME + ") REFERENCES " + USER_TABLE + "(" + COL_USERNAME + "));";

    public final String DROP_TABLE = "DROP TABLE IF EXISTS " + USER_TABLE;


    public DataAccessObject(@Nullable Context context) {
        super(context, "Covid_19_Tracing.db", null, 4);
    }

    public boolean addUserToCovidInfoTable(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USERNAME, user.getUsername());
        cv.put(COL_LATITUDE, user.getLatitudeLocation());
        cv.put(COL_LONGITUDE, user.getLongitudeLocation());
        cv.put(COLUMN_COV19_POSITIVE, user.getCovid19Positive());
        cv.put(COLUMN_DATETIME, user.getDateTimeInMillis());

        long insert = db.insert(COVID_19_INFO_TABLE, null, cv);
        if (insert == -1) {
            return false;
        } else {
            db.close();
            return true;
        }
    }

    public Integer checkForCovidContacts(User user, List<User> userSubList) {
        Integer covidContacts = 0;
        Location userLocation = new Location("user");
        userLocation.setLatitude(user.getLatitudeLocation());
        userLocation.setLongitude(user.getLongitudeLocation());
        for (User otherUser : userSubList) {
            Location otherUserLocation = new Location("otherUser");
            otherUserLocation.setLatitude(user.getLatitudeLocation());
            otherUserLocation.setLongitude(user.getLongitudeLocation());
            if (userLocation.distanceTo(otherUserLocation) < DISTANCE_THRESHOLD && Math.abs(user.getDateTimeInMillis() - otherUser.getDateTimeInMillis()) < TIME_DIF_THRESHOLD) {
                covidContacts++;
            }
        }
        return covidContacts;
    }

    public List<User> getCovid19InfoSubList(String username) {
        List<User> userSubList = new ArrayList<>();
        String queryString = "SELECT * FROM " + COVID_19_INFO_TABLE + " WHERE " + COL_USERNAME + " != '" + username + "'" + " AND " + COLUMN_COV19_POSITIVE + " == 1";

        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(queryString, null);) {
            if (cursor.moveToFirst()) {
                do {
                    String Username = cursor.getString(cursor.getColumnIndex(COL_USERNAME));
                    Double LatitudeLocation = cursor.getDouble(cursor.getColumnIndex(COL_LATITUDE));
                    Double LongitudeLocation = cursor.getDouble(cursor.getColumnIndex(COL_LONGITUDE));
                    long DateTimeInMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DATETIME));
                    Integer ID = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    Boolean Covid19Positive = cursor.getInt(cursor.getColumnIndex(COLUMN_COV19_POSITIVE)) == 1;
                    User user = new User(ID, Username, LatitudeLocation, LongitudeLocation, DateTimeInMillis, Covid19Positive);
                    userSubList.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return userSubList;
        }
    }

    public List<User> getUserTuples(String username) {
        List<User> userTuplesList = new ArrayList<>();
        String queryString = "SELECT * FROM " + COVID_19_INFO_TABLE + " WHERE " + COL_USERNAME + " == '" + username + "'";

        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(queryString, null);) {
            if (cursor.moveToFirst()) {
                do {
                    String Username = cursor.getString(cursor.getColumnIndex(COL_USERNAME));
                    Double LatitudeLocation = cursor.getDouble(cursor.getColumnIndex(COL_LATITUDE));
                    Double LongitudeLocation = cursor.getDouble(cursor.getColumnIndex(COL_LONGITUDE));
                    long DateTimeInMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_DATETIME));
                    Integer ID = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    Boolean Covid19Positive = cursor.getInt(cursor.getColumnIndex(COLUMN_COV19_POSITIVE)) == 1;
                    User user = new User(ID, Username, LatitudeLocation, LongitudeLocation, DateTimeInMillis, Covid19Positive);
                    userTuplesList.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return userTuplesList;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "before Created database");
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_COVID_19_INFO_TABLE);
        Log.i(TAG, "Created database");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
        Log.i(TAG, "Upgraded database version");
    }

    public Boolean registerUser(User u) {
        SQLiteDatabase writeableDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, u.getUsername());
        values.put(COL_PASSWORD, u.getPassword());

        long insertedData = writeableDatabase.insert(USER_TABLE, null, values);
        Log.i(TAG, "Tried to insert:" + insertedData);
        writeableDatabase.close();
        if (insertedData == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean checkUserName(String username) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String[] FIELDS = {COL_USERNAME};
        String WHERE = COL_USERNAME + "=?";
        String[] ARGS = {username};
        Cursor cursor = readableDatabase.query(USER_TABLE, FIELDS, WHERE, ARGS, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String u = cursor.getString(cursor.getColumnIndex(COL_USERNAME));
                Log.i(TAG, "u:" + u);
            }
            readableDatabase.close();
            return true;
        } else {
            readableDatabase.close();
            return false;
        }
    }

    public Boolean checkUserNamePassword(String username, String password) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        String[] FIELDS = {COL_USERNAME, COL_PASSWORD};
        String WHERE = COL_USERNAME + "=?" + " and " + COL_PASSWORD + "=?";
        String[] ARGS = {username, password};
        Cursor cursor = readableDatabase.query(USER_TABLE, FIELDS, WHERE, ARGS, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String u = cursor.getString(cursor.getColumnIndex(COL_USERNAME));
                String p = cursor.getString(cursor.getColumnIndex(COL_PASSWORD));
                Log.i(TAG, "u:" + u + "p:" + p);
            }
            readableDatabase.close();
            return true;
        } else {
            readableDatabase.close();
            return false;
        }
    }
}