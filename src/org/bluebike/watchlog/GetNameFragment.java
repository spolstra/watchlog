package org.bluebike.watchlog;

import android.app.ListActivity;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.LayoutInflater;
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
import android.content.DialogInterface;

import static org.bluebike.watchlog.Constants.TAG;

public class GetNameFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface OnNameSetListener {
        public void onNameSet(String name);
    }

    // Use this instance of the interface to deliver action events
    OnNameSetListener listener;

    // Override the Fragment.onAttach() method to instantiate
    // the OnNameSetListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback
        // interface.
        try {
            // Instantiate the OnNameSetListener so we can send
            // events to the host
            listener = (OnNameSetListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw
            // exception
            throw new ClassCastException(activity.toString()
                    + " must implement OnNameSetListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =
            new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the
        // dialog layout
        builder.setView(inflater.inflate(R.layout.getname, null))
            // Add action buttons
            .setPositiveButton(R.string.create,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Log.d(TAG, "positive!");
                        }
                    })
        .setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GetNameFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}

