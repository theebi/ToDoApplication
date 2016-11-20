package com.theepika.todoapplication;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener{

    private TextView txtName, txtEmail;
    private ImageView imgNavHeaderBg, imgBg;
    GoogleApiClient googleApiClient;
    private View navHeader;
    private TaskDBHelper taskDBHelper;
    private static final String TAG = "MainActivity";
    private ListView taskListView;
    private ArrayAdapter<String> arrayAdapter;
    ListAdapter adapter;
    ArrayList<HashMap<String, String>> taskList;
    NavigationView navigationView;
    public static String userId;

    private static final String urlNavHeaderBg = "https://lh3.googleusercontent.com/proxy/xm_1j45fU-DIHPPxZ7JUrkNcrP3Vt4g89c6EmJs9J-xRurCVcaDRka0Ds39IKpRYM3kKo36aSEjUtI9VMzODjpjA-LNQJ9nAPKRsk5_4AiYQkksM_1BYK90ijg=w506-h284";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        taskListView = (ListView) findViewById(R.id.list_todo);
        taskListView.setEmptyView(findViewById(R.id.emptyElement));
        taskDBHelper = new TaskDBHelper(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("UserID");
        }


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customizing G+ button
        googleApiClient.connect();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String taskId = (String) taskList.get(position).get("id");
                String taskTitle = (String) taskList.get(position).get("title");
                Intent intent = new Intent(MainActivity.this, ViewTask.class);
                intent.putExtra("taskId", taskId);
                intent.putExtra("taskTitle", taskTitle);
                startActivity(intent);

            }
        });
        updateUI();
    }

    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleApiClient.connect();
        super.onStart();
    }

    public void deleteTask(final View view) {
        new AlertDialog.Builder(this)
                .setTitle("Resolve task")
                .setMessage("Mark this task as resolved?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        View parent = (View) view.getParent();

                        ListView listView = (ListView) parent.getParent();
                        final int position = listView.getPositionForView(parent);

                        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
                        //String taskId = String.valueOf(taskTextView.getText());
                        String taskId = (String) taskList.get(position).get("id");
                        SQLiteDatabase db = taskDBHelper.getWritableDatabase();
                        db.delete(TaskContract.TaskEntry.TABLE,
                                TaskContract.TaskEntry.COL_ID + " = ?",
                                new String[]{taskId});
                        db.close();
                        updateUI();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                //.setIcon(android.R.drawable.)
                .show();


    }
    public void updateUI() {
        final Bundle extras = getIntent().getExtras();
        //ArrayList<String> taskList = new ArrayList<>();
        taskList = new ArrayList<HashMap<String, String>>();;
        SQLiteDatabase db = taskDBHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry.COL_ID, TaskContract.TaskEntry.COL_TASK_TITLE},
                TaskContract.TaskEntry.COL_USER_ID + "=?",new String[] { userId }, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_ID);
            int task_title = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);

            HashMap<String, String> task = new HashMap<String, String>();
            task.put("id", cursor.getString(idx));
            task.put("title", cursor.getString(task_title));
            taskList.add(task);
        }

       adapter = new SimpleAdapter(
                MainActivity.this,
                taskList,
                R.layout.todo,
                new String[]{"title"},
                new int[]{R.id.task_title}
        );


        taskListView.setAdapter(adapter);

        cursor.close();

        cursor = db.query(TaskContract.UserEntry.TABLE,
                new String[]{TaskContract.UserEntry.COL_ID,
                        TaskContract.UserEntry.COL_USER_ID,
                        TaskContract.UserEntry.COL_USER_NAME,
                        TaskContract.UserEntry.COL_EMAIL,
                        TaskContract.UserEntry.COL_PICTURE,},
                TaskContract.TaskEntry.COL_USER_ID + "=?",new String[] { userId }, null, null, null, null);

        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.txtName);
        txtEmail = (TextView) navHeader.findViewById(R.id.txtEmail);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.imageView);
        imgBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);

        //Check if count is more than 0
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            String personName = cursor.getString(cursor.getColumnIndex(TaskContract.UserEntry.COL_USER_NAME));
            txtName.setText(personName);
            String email = cursor.getString(cursor.getColumnIndex(TaskContract.UserEntry.COL_EMAIL));
            txtEmail.setText(email);
            String personPhotoUrl = cursor.getString(cursor.getColumnIndex(TaskContract.UserEntry.COL_PICTURE));

            Glide.with(this).load(urlNavHeaderBg)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgBg);

            Glide.with(this).load(personPhotoUrl)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgNavHeaderBg);
            db.close();
        }

    }
    public void signOut() {

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Intent i = new Intent(getApplicationContext(), Login.class);
                        startActivity(i);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh the UI
        updateUI();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Bundle extras = getIntent().getExtras();
        switch (item.getItemId()) {
            case R.id.action_add_task:
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new task")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String taskTitle = String.valueOf(taskEditText.getText());

                                SQLiteDatabase db = taskDBHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(TaskContract.TaskEntry.COL_TASK_TITLE, taskTitle);
                                values.put(TaskContract.TaskEntry.COL_USER_ID, extras.getString("UserID"));
                                db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();
                                updateUI();

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.signout) {
            signOut();

        } else if (id == R.id.editProfile) {
            Intent intent = new Intent(MainActivity.this, EditProfile.class);
            intent.putExtra("UserID", userId);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    public void onStop()  {
        super.onStop();
        googleApiClient.disconnect();
    }

}
