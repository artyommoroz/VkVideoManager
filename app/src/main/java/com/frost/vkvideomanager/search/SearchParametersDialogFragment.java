package com.frost.vkvideomanager.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.frost.vkvideomanager.R;


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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_search, null);

        getDialog().setTitle(getString(R.string.search_dialog_title));

        final String any = getString(R.string.any);
        final String durationShort = getString(R.string.duration_short);
        final String durationLong = getString(R.string.duration_long);

        final String byRelevance = getString(R.string.by_relevance);
        final String byDate = getString(R.string.by_date);
        final String byDuration = getString(R.string.by_duration);

        final CheckBox hdCheckBox = (CheckBox) rootView.findViewById(R.id.hdCheckBox);
        if (hd == 0) {
            hdCheckBox.setChecked(false);
        } else if (hd == 1) {
            hdCheckBox.setChecked(true);
        }

        final CheckBox adultCheckBox = (CheckBox) rootView.findViewById(R.id.adultCheckBox);
        if (adult == 0) {
            adultCheckBox.setChecked(true);
        } else if (adult == 1) {
            adultCheckBox.setChecked(false);
        }

        final Spinner spinnerSort = (Spinner) rootView.findViewById(R.id.spinnerSort);
        switch (sort) {
            case 0: spinnerSort.setSelection(getIndex(spinnerSort, byDate)); break;
            case 1: spinnerSort.setSelection(getIndex(spinnerSort, byDuration)); break;
            case 2: spinnerSort.setSelection(getIndex(spinnerSort, byRelevance)); break;
        }

        final Spinner spinnerDuration = (Spinner) rootView.findViewById(R.id.spinnerDuration);
        switch (duration) {
            case "": spinnerDuration.setSelection(getIndex(spinnerDuration, any)); break;
            case "short": spinnerDuration.setSelection(getIndex(spinnerDuration, durationShort)); break;
            case "long": spinnerDuration.setSelection(getIndex(spinnerDuration, durationLong)); break;
        }

        Button buttonPositive = (Button) rootView.findViewById(R.id.buttonPositive);
        buttonPositive.setOnClickListener(v -> {
            String sortSelectedItem = spinnerSort.getSelectedItem().toString();
            if (sortSelectedItem.equals(byDate)) {
                sort = 0;
            } else if (sortSelectedItem.equals(byDuration)) {
                sort = 1;
            } else if (sortSelectedItem.equals(byRelevance)) {
                sort = 2;
            }
            String durationSelectedItem = spinnerDuration.getSelectedItem().toString();
            if (durationSelectedItem.equals(any)) {
                duration = "";
            } else if (durationSelectedItem.equals(durationShort)) {
                duration = "short";
            } else if (durationSelectedItem.equals(durationLong)) {
                duration = "long";
            }
            hd = hdCheckBox.isChecked() ? 1 : 0;
            adult = adultCheckBox.isChecked() ? 0 : 1;
            listener.onDialogPositiveClick(hd, adult, sort, duration);
            getDialog().dismiss();
        });

        Button buttonNegative = (Button) rootView.findViewById(R.id.buttonNegative);
        buttonNegative.setOnClickListener(v -> getDialog().dismiss());

        return rootView;
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
