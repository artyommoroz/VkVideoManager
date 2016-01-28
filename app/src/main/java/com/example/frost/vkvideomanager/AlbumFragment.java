package com.example.frost.vkvideomanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frost.vkvideomanager.adapters.AlbumAdapter;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AlbumFragment extends Fragment implements AlbumAdapter.ItemClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    List<Album> albums = new ArrayList<>();

    // JSON Node names
    private static final String TAG_ITEMS = "items";
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_EMAIL = "email";

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final VKRequest albumRequest = VKApi.video().getAlbums();
        albumRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.d("ApiResponse", String.valueOf(response.json));
                try {
                    JSONObject resp = response.json.getJSONObject("response");
                    JSONArray albumsJSON = resp.getJSONArray("items");
                    Log.d("ApiResponse", String.valueOf(albumsJSON));
                    for (int i = 0; i < albumsJSON.length(); i++) {
                        JSONObject album = albumsJSON.getJSONObject(i);
                        albums.add(new Album(album));
                    }
                    AlbumAdapter recyclerAdapter = new AlbumAdapter(getActivity(), albums, AlbumFragment.this);
                    recyclerView.setAdapter(recyclerAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void itemClicked(View v, int position) {
//        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoList.get(position).player)));
    }

}
