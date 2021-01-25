package com.example.covid_19contacttracingapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class Covid19DialogFragment extends DialogFragment {
    // Use this instance of the interface to deliver action events
    Covid19DialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.Covid19Dialog)
                .setPositiveButton(R.string.Covid19PositiveAnswer, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        listener.onCovid19DialogPositiveClick(Covid19DialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.Covid19NegativeAnswer, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        listener.onCovid19DialogNegativeClick(Covid19DialogFragment.this);
                    }
                });
        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface Covid19DialogListener {
        public void onCovid19DialogPositiveClick(DialogFragment dialog);

        public void onCovid19DialogNegativeClick(DialogFragment dialog);
    }

    // Override the Fragment.onAttach() method to instantiate the Covid19DialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the Covid19DialogListener so we can send events to the host
            listener = (Covid19DialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(new HomeActivity().toString()
                    + " must implement Covid19DialogListener");
        }
    }
}
