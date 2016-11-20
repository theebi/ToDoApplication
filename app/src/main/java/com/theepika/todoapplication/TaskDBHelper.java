package com.theepika.todoapplication;


 import android.content.Context;
 import android.database.sqlite.SQLiteDatabase;
 import android.database.sqlite.SQLiteOpenHelper;

 public class TaskDBHelper extends SQLiteOpenHelper {

 public TaskDBHelper(Context context) {
    super(context, TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
 }

 @Override
 public void onCreate(SQLiteDatabase db) {
     String createTable = "CREATE TABLE " + TaskContract.TaskEntry.TABLE + " ( " +
     TaskContract.TaskEntry.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
     TaskContract.TaskEntry.COL_USER_ID + " TEXT, " +
     TaskContract.TaskEntry.COL_TASK_TITLE + " TEXT NOT NULL);";

     String createTableUser = "CREATE TABLE " + TaskContract.UserEntry.TABLE + " ( " +
             TaskContract.UserEntry.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
             TaskContract.UserEntry.COL_USER_ID + " TEXT NOT NULL, " +
             TaskContract.UserEntry.COL_EMAIL + " TEXT, " +
             TaskContract.UserEntry.COL_PICTURE + " TEXT, " +
             TaskContract.UserEntry.COL_USER_NAME + " TEXT );";

     db.execSQL(createTable);
     db.execSQL(createTableUser);
 }

 @Override
 public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
     db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE);
     db.execSQL("DROP TABLE IF EXISTS " + TaskContract.UserEntry.TABLE);
     onCreate(db);

     // If you need to add a new column
     if (newVersion > oldVersion) {
  //       db.execSQL("ALTER TABLE tasks ADD COLUMN userID TEXT");
     }
 }


 }