/**************
* Brian Burns
* Amy Puente
* Amy Chockley
* Bystander
* A database to store state rights
* DB.java
**************/

package com.cs495.bystander;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The SQLite Database to hold state's rights
 */
public class DB extends SQLiteOpenHelper {
    // State variables
    final static int DB_VERSION = 1;
    final static String DB_NAME = "mydb.s3db";
    SQLiteDatabase db;
    Context context;

    /**
     * Constructor
     * @param context The context to use
     */
    public DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // Store the context for later use
        this.context = context;
    }

    /**
     * Called by MainActivity, inserts state rights
     * @return The initialized database
     */
    public SQLiteDatabase makeDB() {
        try {
            // Open the database
            db = context.openOrCreateDatabase("budget.db", Context.MODE_PRIVATE, null);
            // Create tables
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "tokens"
                    + " (email TEXT PRIMARY KEY, token TEXT);");

            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "UserSettings"
                    + " ( email TEXT PRIMARY KEY, broadcastOn BOOLEAN, privacySetting TEXT, state TEXT, token TEXT);");

            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "Rights"
                    + " ( state TEXT PRIMARY KEY, rights TEXT);");

            // Insert state rights
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '0', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '1', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '2', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '3', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '4', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '5', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '6', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '7', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '8', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '9', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '10', 'priv' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '11', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '12', 'none' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '13', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '14', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '15', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '16', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '17', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '18', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '19', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '20', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '21', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '22', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '23', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '24', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '25', 'noti' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '26', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '27', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '28', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '29', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '30', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '31', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '32', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '33', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '34', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '35', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '36', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '37', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '38', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '39', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '40', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '41', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '42', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '43', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '44', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '45', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '46', '2pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '47', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '48', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '49', '1pc' );"
            );
            db.execSQL("INSERT OR IGNORE INTO "
                    + "Rights (state, rights) "
                    + "VALUES ( '50', '1pc' );"
            );

            db.execSQL("CREATE TABLE IF NOT EXISTS videos (filename TEXT PRIMARY KEY);");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("DB FAILURE");
        }
        return db;
    }

    /**
     * Creates a new database if one does not exist
     * @param db The database to create
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {

            // Tables only to be created once
            db = context.openOrCreateDatabase("budget.db", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "tokens"
                    + " (email TEXT PRIMARY KEY, token TEXT);");

            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "UserSettings"
                    + " ( email TEXT PRIMARY KEY, broadcastOn BOOLEAN, privacySetting TEXT, state TEXT);");

            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "Rights"
                    + " ( state TEXT PRIMARY KEY, rights TEXT);");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("DB FAILURE");
        }
    }

    /**
     * Upgrades the database when the version number is changed
     * @param db The database to update
     * @param oldVersion The old database version number
     * @param newVersion The new database version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
