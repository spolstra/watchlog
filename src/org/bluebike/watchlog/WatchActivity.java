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
    private EditText timeInput;
    private List<String> items;
    private ArrayAdapter<String> adapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        timeInput = (EditText) findViewById(R.id.time_input);
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
        timeInput.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event) {
                boolean handled = false;
                Log.d(TAG, "In onEditorAction");
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d(TAG, "onEditorAction");
                    adapter.add(timeInput.getText().toString().trim());
                    handled = true;
                    // Close that annoying softkeyboard
                    InputMethodManager imm = (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return handled;
            }
        });

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

            public void onTimeSet(TimePicker view, int hourOfDay,
                    int minute) {
                // Add time to list
                // Due to a bug described here:
                // http://stackoverflow.com/questions/11444238/jelly-bean-datepickerdialog-is-there-a-way-to-cancel
                // and the bug report here:
                // https://code.google.com/p/android/issues/detail?id=34833
                // we might get called twice.
                // Cancel will still call this however.
                if (first) {
                    addToList(hourOfDay + ":" + minute);
                    Log.d(TAG, "TimePicker:" + hourOfDay + ":" + minute);
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
