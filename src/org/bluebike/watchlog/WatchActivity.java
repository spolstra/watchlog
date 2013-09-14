package org.bluebike.watchlog;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.text.TextWatcher;
import android.widget.AdapterView.OnItemClickListener;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import android.widget.AdapterView;
import android.view.View;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView.OnEditorActionListener;
import android.view.inputmethod.InputMethodManager;
import android.app.DialogFragment;
import android.text.format.DateFormat;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
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


public class WatchActivity extends Activity
{
    private static final String TAG = "WatchActivity";
    private ListView timeList;
    private List<String> items;
    private ArrayAdapter<String> adapter;
    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private WatchData watchdata;
    private static String[] FROM = { _ID, TIME, WTIME, DIFF, RATE, };
    private static String ORDER_BY = TIME + " DESC";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        // Get the watch data log
        watchdata = new WatchData(this);

        timeList = (ListView) findViewById(R.id.time_list);

        // Setup adapter for timelist
        items = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        timeList.setAdapter(adapter);

        // First attempt is just to use the arrayadapter
        try {
            Cursor cursor = getData();
            showData(cursor);
        } finally {
            watchdata.close();
        }

        // Setup listeners
        OnItemClickListener clickListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                String query = (String) parent.getItemAtPosition(position);
                /* Log this for now */
                Log.d(TAG, "onItemClick: [" + query +"]");
            }
        };
        timeList.setOnItemClickListener(clickListener);

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
        // Clear list
        adapter.clear();

        // Add data from db to List
        while (cursor.moveToNext()) {
            // or use getColumnIndexOrThrow()
            long id = cursor.getLong(0);
            long time_sec = cursor.getLong(1);
            long wtime_sec = cursor.getLong(2);
            long diff = cursor.getLong(3);
            long rate = cursor.getLong(4);
            adapter.add("[" + sdf.format(new Date(time_sec*1000)) + "] " +
                    sdf.format(new Date(wtime_sec*1000)) + " => " + diff);
            Log.d(TAG, "ShowData:" + id);
        }
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
                // Add time to list
                // http://stackoverflow.com/questions/11444238/jelly-bean-datepickerdialog-is-there-a-way-to-cancel
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
        Log.d(TAG, "addData:" + picked_sec);
        Log.d(TAG, "addData:" + sdf.format(new Date(picked_sec*1000)));
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

}
