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
import android.content.Intent;

import static android.provider.BaseColumns._ID;
import static org.bluebike.watchlog.Constants.TABLE_NAME;
import static org.bluebike.watchlog.Constants.LOGNAME;
import static org.bluebike.watchlog.Constants.TIME;
import static org.bluebike.watchlog.Constants.CONTENT_LOG_URI;

import static org.bluebike.watchlog.Constants.WATCHLOG_LOGNAME;


public class SelectActivity extends ListActivity
{
    private static final String TAG = "SelectActivity";
    private ListView logList;
    private static SimpleDateFormat sdf = new SimpleDateFormat("d/L HH:mm:ss");

    private static String[] FROM = { _ID, LOGNAME, TIME };
    // TODO: What order would make sense here?
    private static String ORDER_BY = TIME + " ASC";
    // TODO: Currently misusing using rowid so we dont show _ID.
    // Must be a better way right?
    private static int[] TO = { R.id.rowid, R.id.logname, R.id.time };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Could try default layout first.
        setContentView(R.layout.select);
        logList = (ListView) findViewById(android.R.id.list);

        // Get cursor from our content provider.
        Cursor cursor = getData();
        // And show it
        showData(cursor);
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
                // TODO: start fragment to ask for new log name.
                Log.d(TAG, "User pressed new button");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Cursor getData() {
        Log.d(TAG, "getData");
        return managedQuery(CONTENT_LOG_URI, FROM, null, null, ORDER_BY);
    }

    private void showData(Cursor cursor) {
        // Data binding
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.select_item, cursor, FROM, TO);
        // Format time and watch time columns to HH:MM:SS
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor c, int col) {
                if (col == 2) {
                    TextView v = (TextView) view;
                    long time = c.getLong(col);
                    // * 1000 because we need millisecs
                    v.setText(sdf.format(time * 1000));
                    Log.d(TAG, "setViewValue" + sdf.format(time * 1000));
                    return true;
                }
                return false;
            }
        });

        setListAdapter(adapter);
    }

    public void addData(Date timestamp, Date picked) {
        // Cannot add data from selector.
        Log.d(TAG, "addData");
    }

    @Override
    public void onListItemClick(ListView l, View v, int p, long id) {
        // TODO: start WatchLog activity for this log.
        // Extract log name.
        String logname =
            ((TextView) v.findViewById(R.id.logname)).getText().toString();
        Log.d(TAG, "onListItemClick, position: " + p + ", id: " + id
                 + "text: " + logname);
        Intent intent = new Intent(this, WatchActivity.class);
        intent.putExtra(WATCHLOG_LOGNAME, logname);
        startActivity(intent);
    }
}
