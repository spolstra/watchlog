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
import static org.bluebike.watchlog.Constants.WTIME;
import static org.bluebike.watchlog.Constants.DIFF;
import static org.bluebike.watchlog.Constants.RATE;
import static org.bluebike.watchlog.Constants.CONTENT_URI;

import static org.bluebike.watchlog.Constants.WATCHLOG_LOGNAME;


public class WatchActivity extends ListActivity
{
    private static final String TAG = "WatchActivity";
    private static String logname;
    private ListView timeList;
    private static SimpleDateFormat timestamp =
        new SimpleDateFormat("d/L HH:mm:ss");
    private static SimpleDateFormat watchtime =
        new SimpleDateFormat("HH:mm:ss");

    private static String[] FROM = { _ID, LOGNAME, TIME, WTIME, DIFF, RATE, };
    private static String[] FROM2 = { LOGNAME, TIME, WTIME, DIFF, RATE, };
    private static String ORDER_BY = TIME + " DESC";
    private static int[] TO = {R.id.logname, R.id.time,
        R.id.wtime, R.id.diff, R.id.rate, };
    // Store selected items in a set:
    private static Set<Long> selected = new TreeSet<Long>();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        logname = intent.getStringExtra(WATCHLOG_LOGNAME);

        setContentView(R.layout.main);
        timeList = (ListView) findViewById(android.R.id.list);
        timeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        timeList.setMultiChoiceModeListener(new MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                        int position, long id, boolean checked) {
                Log.d(TAG, "onItemCheckedStateChanged, pos:" + position
                    + " id: " + id + " checked: " + checked);
                if (checked) {
                    selected.add(id);
                } else {
                    selected.remove(id);
                }
            }
            @Override
            public boolean onActionItemClicked(ActionMode mode,
                                               MenuItem item) {
                // respond to clicks on the actions in the CAB
                switch(item.getItemId()) {
                    case R.id.menu_delete:
                        deleteSelectedItems();
                        Log.d(TAG, "delete button pressed");
                        Log.d(TAG, "selected items: " + selected);
                        selected.clear(); // clear selected items
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu){
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context, menu);
                return true;
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // actions when the CAB is removed.
                Log.d(TAG, "onDestroyActionMode");
                selected.clear(); // clear selected items
            }
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu){
                // updates to the CAB due to an invalidate() request.
                Log.d(TAG, "onPrepareActionMode");
                selected.clear(); // clear selected items
                return true;
            }
        });

        // Get cursor from our content provider.
        Cursor cursor = getData();
        // And show it
        showData(cursor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.new_entry:
                showTimePickerDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteSelectedItems() {
        for (Long e : selected) {
            Log.d(TAG, "deleting: " + e.toString());
            getContentResolver().delete(CONTENT_URI,
                    _ID + " ='" + e + "'",null);
        }
    }

    private Cursor getData() {
        Log.d(TAG, "getData");
        return managedQuery(CONTENT_URI, FROM, logname, null, ORDER_BY);
    }

    private void showData(Cursor cursor) {
        // Data binding
        // FIXME: better performance if we use LoaderManager/CursorLoader.
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.item, cursor, FROM2, TO);

        // Format time and watch time columns to HH:MM:SS
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor c, int col) {
                if (col == 2) {
                    TextView v = (TextView) view;
                    long time = c.getLong(col);
                    // * 1000 because we need millisecs
                    v.setText(timestamp.format(time * 1000));
                    Log.d(TAG, "setViewValue" + timestamp.format(time * 1000));
                    return true;
                } else if (col == 3) {
                    TextView v = (TextView) view;
                    long time = c.getLong(col);
                    // * 1000 because we need millisecs
                    v.setText(watchtime.format(time * 1000));
                    Log.d(TAG, "setViewValue" + watchtime.format(time * 1000));
                    return true;
                }
                return false;
            }
        });
        setListAdapter(adapter);
    }

    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

            private boolean first = true;
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the current time as the default values for the picker
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Create a new instance of TimePickerDialog and return it
                return new TimePickerDialog(getActivity(), this,
                        hour, minute,
                        DateFormat.is24HourFormat(getActivity()));
            }

            public void onTimeSet(TimePicker view, int hour,
                    int minute) {
                // we might get called twice. Cancel might call as well.
                if (first) {
                    // Get current time.
                    final Calendar c = Calendar.getInstance();

                    Date now = c.getTime();
                    // Use current time to create picked time.
                    c.set(Calendar.HOUR_OF_DAY, hour);
                    c.set(Calendar.MINUTE, minute);
                    c.set(Calendar.SECOND, 0);
                    Date picked = c.getTime();

                    addData(now, picked);
                    Log.d(TAG, "TimePicker:" + now + " : " + now.getTime());
                    first = false;
                }
        }
    }

    public void addData(Date timestamp, Date picked) {
        long timestamp_sec = timestamp.getTime()/1000;
        long picked_sec = picked.getTime()/1000;
        int diff_sec = (int) (picked_sec - timestamp_sec);

        ContentValues values = new ContentValues();
        values.put(LOGNAME, logname);
        values.put(TIME, timestamp_sec);
        values.put(WTIME, picked_sec);
        values.put(DIFF,  diff_sec);
        values.put(RATE, 0); // TODO: calculate from prev entry.
        getContentResolver().insert(CONTENT_URI, values);

        Log.d(TAG, "addData:" + picked_sec);
        Log.d(TAG, "addData:" + watchtime.format(new Date(picked_sec*1000)));
    }

    public void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onListItemClick(ListView l, View v, int p, long id) {
        Log.d(TAG, "onListItemClick, position: " + p + ", id: " + id);
    }
}
