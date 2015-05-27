package com.ohanhi.measure;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class ColorChooserDialog extends DialogFragment {

    private static final String ARG_TITLE = "ARG_TITLE";
    private Listener colourSelectedListener;

    public static ColorChooserDialog newInstance(String dialogTitle) {
        Bundle arguments = new Bundle();
        arguments.putString(ARG_TITLE, dialogTitle);
        ColorChooserDialog dialog = new ColorChooserDialog();
        dialog.setArguments(arguments);
        return dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        colourSelectedListener = (Listener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(ARG_TITLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String[] colours = getResources().getStringArray(R.array.colors_array);
                        colourSelectedListener.onColourSelected(colours[which], getTag());
                    }
                });
        return builder.create();
    }

    interface Listener {
        void onColourSelected(String colour, String tag);
    }
}

