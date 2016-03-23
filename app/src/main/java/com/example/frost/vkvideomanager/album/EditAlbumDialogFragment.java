package com.example.frost.vkvideomanager.album;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.frost.vkvideomanager.R;


public class EditAlbumDialogFragment extends DialogFragment {

    private EditDialogListener listener;
    private String title;
    private String privacy;
    private int position;

    public static EditAlbumDialogFragment newInstance(String title, String privacy, int position) {
        EditAlbumDialogFragment editAlbumDialogFragment = new EditAlbumDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("privacy", privacy);
        args.putInt("position", position);
        editAlbumDialogFragment.setArguments(args);
        return editAlbumDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            privacy = getArguments().getString("privacy");
            Log.d("EditDialogPrivacy", privacy);
            position = getArguments().getInt("position");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_edit, null);

        final EditText albumName = (EditText) layout.findViewById(R.id.albumName);
        albumName.setText(title);

        final Spinner spinner = (Spinner) layout.findViewById(R.id.spinnerDuration);
        switch (privacy) {
            case "all": spinner.setSelection(getIndex(spinner, "Все пользователи")); break;
            case "friends": spinner.setSelection(getIndex(spinner, "Только друзья")); break;
            case "friends_of_friends":
            case "friends_of_friends_only": spinner.setSelection(getIndex(spinner, "Друзья и друзья друзей")); break;
            case "nobody":
            case "only_me": spinner.setSelection(getIndex(spinner, "Tолько я")); break;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Редактирование альбома")
                .setView(layout)
                .setPositiveButton("СОХРАНИТЬ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        title = String.valueOf(albumName.getText());
                        switch (spinner.getSelectedItem().toString()) {
                            case "Все пользователи": privacy = "all"; break;
                            case "Только друзья": privacy = "friends"; break;
                            case "Друзья и друзья друзей": privacy = "friends_of_friends"; break;
                            case "Tолько я": privacy = "nobody"; break;
                        }
                        listener.onDialogPositiveClick(title, privacy, position);
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

    public void setListener(EditDialogListener listener) {
        this.listener = listener;
    }

    public interface EditDialogListener {
        void onDialogPositiveClick(final String title, final String privacy, final int position);
    }
}
