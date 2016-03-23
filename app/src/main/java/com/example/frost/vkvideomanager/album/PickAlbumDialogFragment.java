package com.example.frost.vkvideomanager.album;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.frost.vkvideomanager.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKBatchRequest;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;

import java.util.ArrayList;
import java.util.List;


public class PickAlbumDialogFragment extends DialogFragment {

    List<Album> albumList;
    int videoId;
    int ownerId;

    public static PickAlbumDialogFragment newInstance(int videoId, int ownerId, ArrayList<Album> albums) {
        PickAlbumDialogFragment editAlbumDialogFragment = new PickAlbumDialogFragment();
        Bundle args = new Bundle();
        args.putInt("videoId", videoId);
        args.putInt("ownerId", ownerId);
        args.putParcelableArrayList("albums", albums);
        editAlbumDialogFragment.setArguments(args);
        return editAlbumDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoId = getArguments().getInt("videoId");
            ownerId = getArguments().getInt("ownerId");
            albumList = getArguments().getParcelableArrayList("albums");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View layout = inflater.inflate(R.layout.dialog_edit, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Добавить в альбом")
                .setAdapter(new SimpleAlbumAdapter(getActivity()),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                albumList.get(which).toggleSelection();
                            }
                        })
                .setPositiveButton("СОХРАНИТЬ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        List<Album> selectedAlbumList = new ArrayList<>();
                        List<Album> unselectedAlbumList = new ArrayList<>();
                        for (int i = 0; i < albumList.size(); i++) {
                            if (albumList.get(i).isSelected()) {
                                selectedAlbumList.add(albumList.get(i));
                            } else {
                                unselectedAlbumList.add(albumList.get(i));
                            }
                        }
                        String selectedIds = selectedAlbumList.toString().substring(1, selectedAlbumList.toString().length() - 1);
                        String unselectedIds = unselectedAlbumList.toString().substring(1, unselectedAlbumList.toString().length() - 1);
                        VKRequest addVideoRequest = VKApi.video().addToAlbum(VKParameters.from(
                                VKApiConst.VIDEO_ID, videoId,
                                VKApiConst.OWNER_ID, ownerId,
                                VKApiConst.ALBUM_IDS, selectedIds));
                        VKRequest removeVideoRequest = VKApi.video().removeFromAlbum(VKParameters.from(
                                VKApiConst.VIDEO_ID, videoId,
                                VKApiConst.OWNER_ID, ownerId,
                                VKApiConst.ALBUM_IDS, unselectedIds));
                        VKBatchRequest batchRequest = new VKBatchRequest(addVideoRequest, removeVideoRequest);
                        batchRequest.executeWithListener(new VKBatchRequest.VKBatchRequestListener() {});
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

    private class SimpleAlbumAdapter extends ArrayAdapter<Album> {

        Context context;

        public SimpleAlbumAdapter(Context context) {
            super(context, R.layout.item_album_simple);
            this.context = context;
        }

        @Override
        public int getCount() {
            return albumList.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.item_album_simple, parent, false);

            TextView title = (TextView) rowView.findViewById(R.id.title);
            title.setText(albumList.get(position).getTitle());

            final CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.check_box);
            checkBox.setChecked(albumList.get(position).isSelected());
            checkBox.setClickable(false);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (albumList.get(position).isSelected() && checkBox.isChecked()) {
                        checkBox.setChecked(false);
                        albumList.get(position).setSelected(false);
                    } else if (!albumList.get(position).isSelected() && !checkBox.isChecked()){
                        checkBox.setChecked(true);
                        albumList.get(position).setSelected(true);
                    }
                }
            });

            return rowView;
        }

    }

}
