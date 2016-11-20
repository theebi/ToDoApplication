package com.theepika.todoapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class EditProfile extends AppCompatActivity {
    private TaskDBHelper taskDBHelper;
    String userId;
    private EditText txtName;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        taskDBHelper = new TaskDBHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("UserID");


        }

        SQLiteDatabase db = taskDBHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.UserEntry.TABLE,
                new String[]{TaskContract.UserEntry.COL_USER_NAME},
                TaskContract.TaskEntry.COL_USER_ID + "=?",new String[] { userId }, null, null, null, null);

        txtName = (EditText) findViewById(R.id.name);


        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            String personName = cursor.getString(cursor.getColumnIndex(TaskContract.UserEntry.COL_USER_NAME));
            txtName.setText(personName);
            db.close();


        }

    }

    public void updateTask(View view) {
        final SQLiteDatabase db = taskDBHelper.getWritableDatabase();

        //String data = (String) taskListView.getAdapter().getItem(position);
        EditText txtUserName = (EditText) findViewById(R.id.name);
        ContentValues values = new ContentValues();
        String userName = String.valueOf(txtUserName.getText());
        values.put(TaskContract.UserEntry.COL_USER_NAME, userName );

        // updating row
        db.update(TaskContract.UserEntry.TABLE, values, TaskContract.UserEntry.COL_USER_ID + " = ?",
                new String[]{userId});

        this.finish();
//        MainActivity mainactivty = new MainActivity();
//        mainactivty.updateUI();


    }
}