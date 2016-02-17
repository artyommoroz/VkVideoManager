package com.example.frost.vkvideomanager.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frost.vkvideomanager.pojo.Album;
import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.adapters.AlbumAdapter;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AlbumListFragment extends Fragment implements AlbumAdapter.ItemClickListener {

    @Bind(R.id.recyclerViewAlbum)
    RecyclerView recyclerView;
    List<Album> albumList = new ArrayList<>();
    OnAlbumSelectedListener albumSelectedListener;

    public AlbumListFragment() {
        // Required empty public constructor
    }

    public static AlbumListFragment newInstance() {
        AlbumListFragment fragment = new AlbumListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_album, container, false);
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

        setRetainInstance(true);

        final VKRequest albumRequest = VKApi.video().getAlbums(VKParameters.from(VKApiConst.EXTENDED, "1"));
        albumRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.d("ApiResponse", String.valueOf(response.json));
                try {
                    JSONObject resp = response.json.getJSONObject("response");
                    JSONArray jalbums = resp.getJSONArray("items");
                    for (int i = 0; i < jalbums.length(); i++) {
                        albumList.add(new Album(jalbums.getJSONObject(i)));
                    }
                    AlbumAdapter recyclerAdapter = new AlbumAdapter(getActivity(), albumList, AlbumListFragment.this);
                    recyclerView.setAdapter(recyclerAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
            int albumId = albumList.get(position).getId();
            String albumTitle = albumList.get(position).getTitle();
            albumSelectedListener.onAlbumSelected(albumId, albumTitle);
        }
    }

    public interface OnAlbumSelectedListener {
        void onAlbumSelected(int albumId, String albumTitle);
    }
}
