package com.scoreboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "scoreboard";

    // scoreboard database table
    private static final String TABLE_SCORES = "scores";

    // Scores Table - column names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SCORE = "score";

    // Scores Table create statement
    private static final String CREATE_TABLE_SCORES = "CREATE TABLE " + TABLE_SCORES
            + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME + " TEXT NOT NULL, "
            + KEY_SCORE + " TEXT NOT NULL " + ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create "scores" table
        db.execSQL(CREATE_TABLE_SCORES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }

    public boolean isPlayerExists(Player player) {
        SQLiteDatabase db = this.getReadableDatabase();

        String sql ="SELECT " + KEY_NAME + " FROM " + TABLE_SCORES + " WHERE " + KEY_NAME
                + " = '" + player.getName() + "'";
        Cursor cursor = db.rawQuery(sql,null);

        if(cursor.getCount()>0){
            cursor.close();
            return true;
        }else{
            cursor.close();
            return false;
        }
    }

    public void addNewPlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, player.getName());
        values.put(KEY_SCORE, String.valueOf(player.getScore()));
        db.insert(TABLE_SCORES,null,values);
    }

    public void updatePlayerScore (Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SCORE, String.valueOf(player.getScore()));
        db.update(TABLE_SCORES, values, " " + KEY_NAME + " = '" + player.getName() + "'", null);
    }

    public String getPlayerScore(String playerName) {
        String score = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String sql ="SELECT " + KEY_NAME + ", " + KEY_SCORE + " FROM " + TABLE_SCORES + " WHERE " + KEY_NAME
                + " = '" + playerName + "'";
        Cursor cursor = db.rawQuery(sql,null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                score = cursor.getString(cursor.getColumnIndex(KEY_SCORE));
            }
        }

        return score;
    }

    public List<Player> getAllPlayers() {
        List<Player> playerList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(" SELECT * FROM " + TABLE_SCORES,null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String username = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                    String score = cursor.getString(cursor.getColumnIndex(KEY_SCORE));

                    Player player = new Player(username, Integer.parseInt(score));
                    // Add player into playerList
                    playerList.add(player);
                    cursor.moveToNext();
                }
            }
        }

        return playerList;
    }

}
