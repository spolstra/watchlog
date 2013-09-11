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


public class WatchActivity extends Activity
{
    private static final String TAG = "WatchActivity";
    private ListView timeList;
    private List<String> items;
    private ArrayAdapter<String> adapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        timeList = (ListView) findViewById(R.id.time_list);

        // Setup adapter for timelist
        items = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        timeList.setAdapter(adapter);

        // Set initial dummy text (ok to do it here?)
        adapter.clear();
        adapter.add("Sam");
        adapter.add("and");
        adapter.add("Max");

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
                // Due to a bug described here:
                // http://stackoverflow.com/questions/11444238/jelly-bean-datepickerdialog-is-there-a-way-to-cancel
                // and the bug report here:
                // https://code.google.com/p/android/issues/detail?id=34833
                // we might get called twice.
                // Cancel will still call this however.
                if (first) {
                    // Get current time to time step this entry.
                    final Calendar c = Calendar.getInstance();
                    int cur_hour = c.get(Calendar.HOUR_OF_DAY);
                    int cur_minute = c.get(Calendar.MINUTE);
                    int cur_second = c.get(Calendar.SECOND);

                    int sec_diff = (hour - cur_hour)*3600 +
                        (minute - cur_minute)*60 - cur_second;
                    addToList(hour + ":" + minute
                            + " [" + cur_hour + ":" + cur_minute +
                            ":" + cur_second + "] => " + sec_diff);
                    Log.d(TAG, "TimePicker:" + hour + ":" + minute);
                    first = false;
                }
        }
    }

    public void addToList(String time) {
        adapter.add(time);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

}
