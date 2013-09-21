package org.bluebike.watchlog;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.text.TextWatcher;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.ActionMode;
import android.view.MenuItem;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView.OnEditorActionListener;
import android.view.inputmethod.InputMethodManager;
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
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

import static android.provider.BaseColumns._ID;
import static org.bluebike.watchlog.Constants.TABLE_NAME;
import static org.bluebike.watchlog.Constants.TIME;
import static org.bluebike.watchlog.Constants.WTIME;
import static org.bluebike.watchlog.Constants.DIFF;
import static org.bluebike.watchlog.Constants.RATE;


public class WatchActivity extends ListActivity
{
    private static final String TAG = "WatchActivity";
    private ListView timeList;
    private List<String> items;
    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private WatchData watchdata;
    private static String[] FROM = { _ID, TIME, WTIME, DIFF, RATE, };
    private static String ORDER_BY = TIME + " DESC";
    private static int[] TO = {R.id.rowid, R.id.time, R.id.wtime, R.id.diff,
                                R.id.rate, };
    // Store selected items in a set:
    private static Set<Long> selected = new TreeSet<Long>();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        // Get the watch data log
        watchdata = new WatchData(this);
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
                selected.clear(); // clear selected items
            }
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu){
                // updates to the CAB due to an invalidate() request.
                selected.clear(); // clear selected items
                return true;
            }
        });

        try {
            Cursor cursor = getData();
            showData(cursor);
        } finally {
            watchdata.close();
        }
    }

    private void deleteSelectedItems() {
        SQLiteDatabase db = watchdata.getWritableDatabase();
        for (Long e : selected) {
            Log.d(TAG, "deleting: " + e.toString());
            db.delete(TABLE_NAME, _ID + " ='" + e + "'",null);
        }
        showData(getData());
    }

    private Cursor getData() {
        SQLiteDatabase db = watchdata.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null,
                null, ORDER_BY);
        startManagingCursor(cursor);
        Log.d(TAG, "getData");
        return cursor;
    }

    private void showData(Cursor cursor) {
        // Data binding
        // FIXME: better performance if we use LoaderManager/CursorLoader.
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.item, cursor, FROM, TO);

        // Format time and watch time columns to HH:MM:SS
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor c, int col) {
                if (col == 1 || col == 2) {
                    //Log.d(TAG, "setViewValue");
                    TextView v = (TextView) view;
                    long time = c.getLong(col);
                    // * 1000 because we need millisecs
                    v.setText(sdf.format(time * 1000));
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

        SQLiteDatabase db = watchdata.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIME, timestamp_sec);
        values.put(WTIME, picked_sec);
        values.put(DIFF,  diff_sec);
        values.put(RATE, 0); // TODO: calculate from prev entry.
        db.insertOrThrow(TABLE_NAME, null, values);
        showData(getData());
        Log.d(TAG, "addData:" + picked_sec);
        Log.d(TAG, "addData:" + sdf.format(new Date(picked_sec*1000)));
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onListItemClick(ListView l, View v, int p, long id) {
        Log.d(TAG, "onListItemClick, position: " + p + ", id: " + id);
    }
}
