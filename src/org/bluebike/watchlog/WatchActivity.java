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
        TextWatcher textWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start,
                    int count, int after) {
                /* Do nothing */
            }
            public void onTextChanged(CharSequence s, int start,
                    int before, int count) {
                /* Do nothing */
                Log.d(TAG, "onTextChanged");
            }
            public void afterTextChanged(Editable s) {
                /* Do nothing */
                Log.d(TAG, "afterTextChanged");
                // What is an Editable ?
                adapter.add(timeInput.getText().toString().trim());
            }
        };
        timeInput.addTextChangedListener(textWatcher);

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
