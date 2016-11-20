package com.theepika.todoapplication;

/**
 * Created by Theepika on 11/15/16.
 */
import android.provider.BaseColumns;

public class TaskContract {
    public static final String DB_NAME = "com.theepika.todolist.db";
    public static final int DB_VERSION = 4;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "tasks";

        public static final String COL_TASK_TITLE = "title";
        public static final String COL_USER_ID = "user_id";
        public static final String COL_ID = "id";

    }

    public class UserEntry implements BaseColumns {
        public static final String TABLE = "users";
        public static final String COL_ID = "id";
        public static final String COL_USER_ID = "user_id";
        public static final String COL_USER_NAME = "name";
        public static final String COL_EMAIL = "email";
        public static final String COL_PICTURE = "picture";

    }


}