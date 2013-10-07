package org.bluebike.watchlog;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.view.ActionMode;
import android.view.MenuItem;
import android.view.Menu;
import android.view.MenuInflater;
import android.app.DialogFragment;
import android.text.format.DateFormat;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.widget.TimePicker;
import android.widget.AbsListView.MultiChoiceModeListener;

import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import android.database.Cursor;
import android.content.ContentValues;

import static android.provider.BaseColumns._ID;
import static org.bluebike.watchlog.Constants.TABLE_NAME;
import static org.bluebike.watchlog.Constants.TIME;
import static org.bluebike.watchlog.Constants.WTIME;
import static org.bluebike.watchlog.Constants.DIFF;
import static org.bluebike.watchlog.Constants.RATE;
import static org.bluebike.watchlog.Constants.CONTENT_URI;


public class SelectActivity extends ListActivity
{
    private static final String TAG = "SelectActivity";
    private ListView logList;

    private static String[] FROM = { _ID, TIME, WTIME, DIFF, RATE, };
    private static String ORDER_BY = TIME + " DESC";
    private static int[] TO = {R.id.rowid, R.id.time, R.id.wtime, R.id.diff,
                                R.id.rate, };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // try default layout first.
        //setContentView(R.layout.select);
        logList = (ListView) findViewById(android.R.id.list);

        // Get cursor from our content provider.
        Cursor cursor = getData();
        // And show it
        //showData(cursor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.new_entry:
                Log.d(TAG, "User pressed new button");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Cursor getData() {
        Log.d(TAG, "getData");
        return managedQuery(CONTENT_URI, FROM, null, null, ORDER_BY);
    }

    private void showData(Cursor cursor) {
        // Data binding
        // TODO need dummy binding here.
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.item, cursor, FROM, TO);

        setListAdapter(adapter);
    }

    public void addData(Date timestamp, Date picked) {
        /*
        ContentValues values = new ContentValues();
        values.put(TIME, timestamp_sec);
        values.put(WTIME, picked_sec);
        values.put(DIFF,  diff_sec);
        values.put(RATE, 0); // TODO: calculate from prev entry.
        getContentResolver().insert(CONTENT_URI, values);
    */
        Log.d(TAG, "addData");
    }

    @Override
    public void onListItemClick(ListView l, View v, int p, long id) {
        Log.d(TAG, "onListItemClick, position: " + p + ", id: " + id);
    }
}
