package com.example.meghan.movielistviewwithcursoradapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager {

    private Context context;
    private SQLHelper helper;
    private SQLiteDatabase db;

    protected static final String DB_NAME = "movies";
    protected static final int DB_VERSION = 1;
    protected static final String DB_TABLE = "ratings";

    protected static final String ID_COL = "_id";
    protected static final String MOVIE_NAME_COL = "name";
    protected static final String MOVIE_RATING_COL = "rating";
    //my code below hopefully doesn't break program
    protected static final String MOVIE_DATE_COL = "date"; //Adding date varitable

    private static final String DB_TAG = "DatabaseManager" ;
    private static final String SQLTAG = "SQLHelper" ;

    public DatabaseManager(Context c) {
        this.context = c;
        helper = new SQLHelper(c);
        this.db = helper.getWritableDatabase();
    }

    public void close() {
        helper.close(); //Closes the database - very important!
    }

    //A Bridge between the Database and the ListView, it provides data from the Cursor to the list
    public Cursor getAllMovies() {
        //Fetch all records, sort by movie name
        Cursor cursor = db.query(DB_TABLE, null, null, null, null, null, MOVIE_NAME_COL);
        return cursor;
    }


    // Add a movie and its rating to the database.
    // Returns true if movie added, false if movie is already in the database

    //my code below hopefully doesn't break program.     \/    \/
    public boolean addMovie(String name, float rating, String date) {
        ContentValues newProduct = new ContentValues();
        newProduct.put(MOVIE_NAME_COL, name); //adding name to database
        newProduct.put(MOVIE_RATING_COL, rating); //adding rating
        //my code below hopefully doesn't break program
        newProduct.put(MOVIE_DATE_COL, date); //When saving should add date as string

        try {
            db.insertOrThrow(DB_TABLE, null, newProduct); // My code                \/            \/
            Log.d(DB_TAG, "Added movie: " + name + " to with rating: " + rating + "and date: " + date);
            return true;

        } catch (SQLiteConstraintException sqlce) {
            Log.e(DB_TAG, "error inserting data into table. " +
                    "Name:" + name + " rating:" + rating + "Date:" + date, sqlce);
            return false; //                    my code     /\       /\
        }
    }

    //Method to update the database
    //Update rating by movie ID. Return true if update successful; false otherwise.
    public boolean updateRating(int movieID, float rating) {
    //TODO add date update for movie or make own one of these
        Log.d(DB_TAG, "About to update rating for " + movieID + " to " + rating);
        ContentValues updateVals = new ContentValues();
        updateVals.put(MOVIE_RATING_COL, rating);
        String where = ID_COL  + " = ? ";
        String[] whereArgs = { Integer.toString(movieID) };
        int rowsMod = db.update(DB_TABLE, updateVals, where, whereArgs);
        Log.d(DB_TAG, "After update for " +movieID + " update " + rowsMod + " rows updated (should be 1");
        if (rowsMod == 1) {
            return true;
        }
        else {
            return false;  //e.g. if no rows updates
        }
    }

    public boolean updateDate(int movieId, String date) { //my code to add date to movie app
        Log.d(DB_TAG, "About to update date for " + movieId + "to" + date);
        ContentValues updateVals = new ContentValues();
        updateVals.put(MOVIE_DATE_COL, date);
        String where = ID_COL + " = ? ";
        String[] whereArgs = { Integer.toString(movieId) };
        int rowsMod = db.update(DB_TABLE, updateVals, where, whereArgs);
        Log.d(DB_TAG, "After update for " + movieId + " update " + rowsMod + " rows updated (should be 1)");

        if (rowsMod == 1) {
            return true;
        }
        else {
            return false;  //e.g. if no rows updates
        }
    }


    //It's a good idea to delegate Database interaction to a dedicated class
    public class SQLHelper extends SQLiteOpenHelper {
        public SQLHelper(Context c){
            super(c, DB_NAME, null, DB_VERSION);
        }

        @Override //The code to create a table from the SQL stuff
        public void onCreate(SQLiteDatabase db) {
            String createSQLbase = "CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT UNIQUE, %s FLOAT, %s date )"; //added %s date
            String createSQL = String.format(createSQLbase, DB_TABLE, ID_COL, MOVIE_NAME_COL, MOVIE_RATING_COL, MOVIE_DATE_COL); //added MOVIE_DATE_COL
            db.execSQL(createSQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(db);
            Log.w(SQLTAG, "Upgrade table - drop and recreate it");
        }
    }
}



