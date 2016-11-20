package com.theepika.todoapplication;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ViewTask extends AppCompatActivity {
    private TextView taskList;
    String data;
    private Button button;
    private TaskDBHelper taskDBHelper;
    String taskId;
    String taskTitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskDBHelper = new TaskDBHelper(this);

        setContentView(R.layout.activity_view_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        EditText txtTaskTitle = (EditText) findViewById(R.id.task_title);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //String id = extras.getString("ID_EXTRA");
            taskId = extras.getString("taskId");
            taskTitle = extras.getString("taskTitle");
            txtTaskTitle.setText(taskTitle);

        }
        }

    public void updateTask(View view) {
        final SQLiteDatabase db = taskDBHelper.getWritableDatabase();

        //String data = (String) taskListView.getAdapter().getItem(position);
        EditText txtTaskTitle = (EditText) findViewById(R.id.task_title);
        ContentValues values = new ContentValues();
        String taskTitle = String.valueOf(txtTaskTitle.getText());
        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, taskTitle );

        // updating row
        db.update(TaskContract.TaskEntry.TABLE, values, TaskContract.TaskEntry.COL_ID + " = ?",
                        new String[]{taskId});

        this.finish();


    }


}

