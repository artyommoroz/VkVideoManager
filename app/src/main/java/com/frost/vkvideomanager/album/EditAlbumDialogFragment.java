package com.frost.vkvideomanager.album;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.frost.vkvideomanager.R;


public class EditAlbumDialogFragment extends DialogFragment {

    private static final String TITLE = "title" ;
    private static final String PRIVACY = "privacy" ;
    private static final String POSITION = "position" ;

    private EditDialogListener listener;
    private String title;
    private String privacy;
    private int position;

    public static EditAlbumDialogFragment newInstance(String title, String privacy, int position) {
        EditAlbumDialogFragment editAlbumDialogFragment = new EditAlbumDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(PRIVACY, privacy);
        args.putInt(POSITION, position);
        editAlbumDialogFragment.setArguments(args);
        return editAlbumDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(TITLE);
            privacy = getArguments().getString(PRIVACY);
            position = getArguments().getInt(POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_edit, null);

        getDialog().setTitle(getString(R.string.album_dialog_edit_title));

        final String all = getString(R.string.all);
        final String friends = getString(R.string.friends);
        final String friendsOfFriends = getString(R.string.friends_of_friends);
        final String onlyMe = getString(R.string.only_me);

        final EditText albumName = (EditText) rootView.findViewById(R.id.albumName);
        albumName.setText(title);
        albumName.setSelection(albumName.getText().length());
        if(albumName.requestFocus()) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        final Spinner spinner = (Spinner) rootView.findViewById(R.id.spinnerDuration);
        switch (privacy) {
            case "all": spinner.setSelection(getIndex(spinner, all)); break;
            case "friends": spinner.setSelection(getIndex(spinner, friends)); break;
            case "friends_of_friends":
            case "friends_of_friends_only": spinner.setSelection(getIndex(spinner, friendsOfFriends)); break;
            case "nobody":
            case "only_me": spinner.setSelection(getIndex(spinner, onlyMe)); break;
        }

        Button buttonPositive = (Button) rootView.findViewById(R.id.buttonPositive);
        buttonPositive.setOnClickListener(v -> {
            String selectedItem = spinner.getSelectedItem().toString();
            if (selectedItem.equals(all)) {
                privacy = "all";
            } else if (selectedItem.equals(friends)) {
                privacy = "friends";
            } else if (selectedItem.equals(friendsOfFriends)) {
                privacy = "friends_of_friends";
            } else if (selectedItem.equals(onlyMe)) {
                privacy = "nobody";
            }
            title = String.valueOf(albumName.getText());
            listener.onPositiveClick(position, title, privacy);
            getDialog().dismiss();
        });

        Button buttonNegative = (Button) rootView.findViewById(R.id.buttonNegative);
        buttonNegative.setOnClickListener(v -> getDialog().cancel());

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

    public void setListener(EditDialogListener listener) {
        this.listener = listener;
    }

    public interface EditDialogListener {
//        void onPositiveClick(final String title, final String privacy, final int position);
        void onPositiveClick(final int position, final String title, final String privacy);
    }
}
