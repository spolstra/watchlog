package org.bluebike.watchlog;

import static android.provider.BaseColumns._ID;
import static org.bluebike.watchlog.Constants.TABLE_NAME;
import static org.bluebike.watchlog.Constants.TIME;
import static org.bluebike.watchlog.Constants.WTIME;
import static org.bluebike.watchlog.Constants.DIFF;
import static org.bluebike.watchlog.Constants.RATE;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WatchData extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "watchdata.db";
    private static final int DATABASE_VERSION = 1;

    /* Create helper object */
    public WatchData(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + _ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
                + TIME + " INTEGER, " 
                + WTIME + " INTEGER,"
                + DIFF + " INTEGER,"
                + RATE + " INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
            int newVersion) {
        db.execSQL("DROP TABLE IF EXITS " + TABLE_NAME);
        onCreate(db);
    }
}

