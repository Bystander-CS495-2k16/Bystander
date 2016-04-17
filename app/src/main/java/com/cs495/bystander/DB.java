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

            db.execSQL("CREATE TABLE IF NOT EXISTS videos (filename TEXT PRIMARY KEY);");

            for (int i = 0; i < 15; i++) {
                System.out.println("DB");
            }

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

