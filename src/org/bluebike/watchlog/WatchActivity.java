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

import java.util.ArrayList;
import java.util.List;


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
}
