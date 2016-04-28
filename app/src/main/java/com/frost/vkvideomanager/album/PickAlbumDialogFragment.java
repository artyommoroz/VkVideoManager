package com.frost.vkvideomanager.album;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.frost.vkvideomanager.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKBatchRequest;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;

import java.util.ArrayList;
import java.util.List;


public class PickAlbumDialogFragment extends DialogFragment {

    private static final String VIDEO_ID = "videoId";
    private static final String OWNER_ID = "ownerId";
    private static final String ALBUM_LIST = "albumList";

    private List<Album> albumList;
    private int videoId;
    private int ownerId;

    public static PickAlbumDialogFragment newInstance(int videoId, int ownerId, ArrayList<Album> albums) {
        PickAlbumDialogFragment editAlbumDialogFragment = new PickAlbumDialogFragment();
        Bundle args = new Bundle();
        args.putInt(VIDEO_ID, videoId);
        args.putInt(OWNER_ID, ownerId);
        args.putParcelableArrayList(ALBUM_LIST, albums);
        editAlbumDialogFragment.setArguments(args);
        return editAlbumDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoId = getArguments().getInt(VIDEO_ID);
            ownerId = getArguments().getInt(OWNER_ID);
            albumList = getArguments().getParcelableArrayList(ALBUM_LIST);
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_pick_album, null);

        getDialog().setTitle(getString(R.string.album_dialog_add_title));

        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(new SimpleAlbumAdapter(getActivity()));

        Button buttonPositive = (Button) rootView.findViewById(R.id.buttonPositive);
        buttonPositive.setOnClickListener(v -> {
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
            getDialog().dismiss();
        });

        Button buttonNegative = (Button) rootView.findViewById(R.id.buttonNegative);
        buttonNegative.setOnClickListener(v -> getDialog().dismiss());

        return rootView;
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

            rowView.setOnClickListener(v -> {
                if (albumList.get(position).isSelected() && checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    albumList.get(position).setSelected(false);
                } else if (!albumList.get(position).isSelected() && !checkBox.isChecked()){
                    checkBox.setChecked(true);
                    albumList.get(position).setSelected(true);
                }
            });

            return rowView;
        }
    }
}
