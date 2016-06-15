package de.hs_mannheim.planb.mobilegrowthmonitor.misc;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import de.hs_mannheim.planb.mobilegrowthmonitor.R;

/**
 * Created by Laura on 03.06.2016.
 */
public class PermissionDialogFragment extends DialogFragment {

    public interface NoticeDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final int permission = getArguments().getInt("permission");
        String permissionString;
        switch (permission) {
            case 1:
                permissionString = getString(R.string.request_permission_cam);
                break;
            case 2:
                permissionString = getString(R.string.request_permission_read_storage);
                break;
            default:
                permissionString = getString(R.string.app_needs_permission);
        }

        builder.setMessage(permissionString)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        PermissionDialogFragment pdf = PermissionDialogFragment.this;
                        Bundle args = new Bundle();
                        args.putInt("permission", permission);
                        mListener.onDialogPositiveClick(PermissionDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(PermissionDialogFragment.this);
                    }
                });
        return builder.create();
    }

}
