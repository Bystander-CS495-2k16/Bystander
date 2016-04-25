package com.cs495.bystander;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by appleowner on 4/5/16.
 */
public class DB extends SQLiteOpenHelper {
    final static int DB_VERSION = 1;
    final static String DB_NAME = "mydb.s3db";
    SQLiteDatabase db;
    Context context;

    public DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // Store the context for later use
        this.context = context;
    }

    public SQLiteDatabase makeDB() {
        try {
            db = context.openOrCreateDatabase("budget.db", context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "tokens"
                    + " (email TEXT PRIMARY KEY, token TEXT);");

            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "UserSettings"
                    + " ( email TEXT PRIMARY KEY, broadcastOn BOOLEAN, privacySetting TEXT, state TEXT, token TEXT);");

            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "Rights"
                    + " ( state TEXT PRIMARY KEY, rights TEXT);");

            // insert rights
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Alabama', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Alaska', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Arizona', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Arkansas', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'California', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Colorado', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Connecticut', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Delaware', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Florida', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Georgia', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Hawaii', 'priv' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Idaho', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Illinois', 'none' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Indiana', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Iowa', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Kansas', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Kentucky', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Louisiana', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Maine', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Maryland', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Massachusetts', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Michigan', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Minnesota', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Mississippi', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Missouri', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Montana', 'noti' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Nebraska', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Nevada', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'New Hampshire', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'New Jersey', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'New Mexico', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'New York', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'North Carolina', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'North Dakota', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Ohio', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Oklahoma', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Oregon', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Pennsylvania', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Rhode Island', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'South Carolina', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'South Dakota', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Tennessee', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Texas', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Utah', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Vermont', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Virginia', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Washington', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'West Virginia', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Wisconsin', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'Wyoming', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( 'DC', '1pc' );"
            );

            db.execSQL("CREATE TABLE IF NOT EXISTS videos (filename TEXT PRIMARY KEY);");

        } catch (Exception e) {
            for (int i = 0; i < 15; i++) {
                System.out.println("DB FAILURE");
            }
        }
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i = 0; i < 15; i++) {
            System.out.println("DB");
        }
        try {
            db = context.openOrCreateDatabase("budget.db", context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "tokens"
                    + " (email TEXT PRIMARY KEY, token TEXT);");

            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "UserSettings"
                    + " ( email TEXT PRIMARY KEY, broadcastOn BOOLEAN, privacySetting TEXT, state TEXT);");
            for (int i = 0; i < 15; i++) {
                System.out.println("DB");
            }

            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "Rights"
                    + " ( state TEXT PRIMARY KEY, rights TEXT);");

            for (int i = 0; i < 15; i++) {
                System.out.println("DB");
            }

        } catch (Exception e) {
            for (int i = 0; i < 15; i++) {
                System.out.println("DB FAILURE");
            }
        }
    }

    // need to add this probably
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

