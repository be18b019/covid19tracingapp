package com.example.covid_19contacttracingapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class SetLocationDialogFragment extends DialogFragment {
    private final int LAUNCH_GOOGLE_MAPS_PAST_LOC = 1;
    //private final int LAUNCH_GOOGLE_MAPS_PRESENT_LOC = 2;
    SetLocationDialogFragment.SetLocationDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.chooseLocation)
                .setPositiveButton(R.string.getCurrentLocation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //getActivity().startActivityForResult(googleMapsIntent, LAUNCH_GOOGLE_MAPS_PRESENT_LOC);
                        listener.onSetLocationDialogPositiveClick(SetLocationDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.setPreviousLocation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent googleMapsIntent = new Intent(getContext(), GoogleMapsActivity.class);
                        getActivity().startActivityForResult(googleMapsIntent, LAUNCH_GOOGLE_MAPS_PAST_LOC);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public interface SetLocationDialogListener {
        public void onSetLocationDialogPositiveClick(DialogFragment dialog);
//        public void onSetLocationDialogNegativeClick(DialogFragment dialog);
    }

    // Override the Fragment.onAttach() method to instantiate the SetLocationDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SetLocationDialogListener so we can send events to the host
            listener = (SetLocationDialogFragment.SetLocationDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(new HomeActivity().toString()
                    + " must implement SetLocationDialogListener");
        }
    }
}