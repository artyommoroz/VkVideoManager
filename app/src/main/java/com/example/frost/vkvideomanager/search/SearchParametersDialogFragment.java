package com.example.frost.vkvideomanager.search;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.example.frost.vkvideomanager.R;


public class SearchParametersDialogFragment extends DialogFragment {

    private static final String HD = "hd";
    private static final String ADULT = "adult";
    private static final String SORT = "sort";
    private static final String DURATION = "duration";

    private SearchParametersListener listener;
    private int hd;
    private int adult;
    private int sort;
    private String duration;

    public static SearchParametersDialogFragment newInstance(int hd, int adult, int sort, String duration) {
        SearchParametersDialogFragment fragment = new SearchParametersDialogFragment();
        Bundle args = new Bundle();
        args.putInt(HD, hd);
        args.putInt(ADULT, adult);
        args.putInt(SORT, sort);
        args.putString(DURATION, duration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hd = getArguments().getInt(HD);
            adult = getArguments().getInt(ADULT);
            sort = getArguments().getInt(SORT);
            duration = getArguments().getString(DURATION);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_search, null);

        final CheckBox hdCheckBox = (CheckBox) layout.findViewById(R.id.hdCheckBox);
        if (hd == 0) {
            hdCheckBox.setChecked(false);
        } else if (hd == 1) {
            hdCheckBox.setChecked(true);
        }

        final CheckBox adultCheckBox = (CheckBox) layout.findViewById(R.id.adultCheckBox);
        if (adult == 0) {
            adultCheckBox.setChecked(true);
        } else if (adult == 1) {
            adultCheckBox.setChecked(false);
        }

        final Spinner spinnerSort = (Spinner) layout.findViewById(R.id.spinnerSort);
        switch (sort) {
            case 0: spinnerSort.setSelection(getIndex(spinnerSort, "По дате")); break;
            case 1: spinnerSort.setSelection(getIndex(spinnerSort, "По длительности")); break;
            case 2: spinnerSort.setSelection(getIndex(spinnerSort, "По релевантности")); break;
        }

        final Spinner spinnerDuration = (Spinner) layout.findViewById(R.id.spinnerDuration);
        switch (duration) {
            case "": spinnerDuration.setSelection(getIndex(spinnerDuration, "Любая")); break;
            case "short": spinnerDuration.setSelection(getIndex(spinnerDuration, "Короткие")); break;
            case "long": spinnerDuration.setSelection(getIndex(spinnerDuration, "Длинные")); break;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Параметры поиска")
                .setView(layout)
                .setPositiveButton("ПРИМЕНИТЬ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        hd = hdCheckBox.isChecked() ? 1 : 0;
                        adult = adultCheckBox.isChecked() ? 0 : 1;
                        switch (spinnerSort.getSelectedItem().toString()) {
                            case "По дате": sort = 0; break;
                            case "По длительности": sort = 1; break;
                            case "По релевантности": sort = 2; break;
                        }
                        switch (spinnerDuration.getSelectedItem().toString()) {
                            case "Любая": duration = ""; break;
                            case "Короткие": duration = "short"; break;
                            case "Длинные": duration = "long"; break;
                        }
                        listener.onDialogPositiveClick(hd, adult, sort, duration);
                    }
                })
                .setNegativeButton("ОТМЕНА", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    private int getIndex(Spinner spinner, String myString){
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    public void setListener(SearchParametersListener listener) {
        this.listener = listener;
    }

    public interface SearchParametersListener {
        void onDialogPositiveClick(final int hd, final int adult, final int sort, final String duration);
    }
}
