package com.example.frost.vkvideomanager.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.adapters.AlbumAdapter;
import com.example.frost.vkvideomanager.model.Album;
import com.example.frost.vkvideomanager.network.Parser;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AlbumsFragment extends BaseFragment implements AlbumAdapter.ItemClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private List<Album> albumList;
    private OnAlbumSelectedListener albumSelectedListener;

    public AlbumsFragment() {
        // Required empty public constructor
    }

    public static AlbumsFragment newInstance() {
        return new AlbumsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
        VKRequest albumRequest = VKApi.video().getAlbums(VKParameters.from(VKApiConst.EXTENDED, 1));
        albumRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                progressBar.setVisibility(View.INVISIBLE);
                albumList = Parser.parseAlbums(response);
                AlbumAdapter albumAdapter = new AlbumAdapter(getActivity(), albumList, AlbumsFragment.this);
                recyclerView.setAdapter(albumAdapter);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnAlbumSelectedListener) {
            albumSelectedListener = (OnAlbumSelectedListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        albumSelectedListener = null;
    }

    @Override
    public void itemClicked(View v, int position) {
        if (albumSelectedListener != null) {
            int ownerId = albumList.get(position).getOwnerId();
            int albumId = albumList.get(position).getId();
            String albumTitle = albumList.get(position).getTitle();
            albumSelectedListener.onAlbumSelected(ownerId, albumId, albumTitle);
        }
    }

    public String getName() {
        return "ALBUMS";
    }

    public interface OnAlbumSelectedListener {
        void onAlbumSelected(int ownerId, int albumId, String albumTitle);
    }
}
