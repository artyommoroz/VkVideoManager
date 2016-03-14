package com.example.frost.vkvideomanager.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frost.vkvideomanager.EndlessScrollListener;
import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.adapters.VideoAdapter;
import com.example.frost.vkvideomanager.network.Parser;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

import org.json.JSONException;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CatalogSectionFragment extends Fragment implements VideoAdapter.ItemClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    private VideoAdapter videoAdapter;
    private VKList<VKApiVideo> videoList = new VKList<>();
    private OnVideoSelectedListener videoSelectedListener;
    private String sectionId;
    private String from;

    public CatalogSectionFragment() {}

    public static CatalogSectionFragment newInstance(String sectionId, String from) {
        CatalogSectionFragment fragment = new CatalogSectionFragment();
        Bundle args = new Bundle();
        args.putString("sectionId", sectionId);
        args.putString("from", from);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                VKRequest videoRequest = new VKRequest("video.getCatalogSection", VKParameters.from(
                        "section_id", sectionId,
                        "from", from,
                        VKApiConst.COUNT, 16
                ));
                videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        try {
                            from = response.json.optJSONObject("response").getString("next");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        videoList.addAll(Parser.parseCatalogSection(response));
                        int curSize = videoAdapter.getItemCount();
                        videoAdapter.notifyItemRangeInserted(curSize, videoList.size() - 1);
                    }
                });
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            sectionId = getArguments().getString("sectionId");
            from = getArguments().getString("from");
        }

        VKRequest videoRequest = new VKRequest("video.getCatalogSection", VKParameters.from(
                "section_id", sectionId,
                "from", from,
                VKApiConst.COUNT, 16
        ));
        videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    from = response.json.optJSONObject("response").getString("next");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                videoList = Parser.parseCatalogSection(response);
                videoAdapter = new VideoAdapter(getActivity(), videoList, CatalogSectionFragment.this);
                recyclerView.setAdapter(videoAdapter);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnVideoSelectedListener) {
            videoSelectedListener = (OnVideoSelectedListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        videoSelectedListener = null;
    }

    @Override
    public void itemClicked(View v, int position) {
        if (videoSelectedListener != null) {
            Uri videoUri = Uri.parse(videoList.get(position).player);
            videoSelectedListener.onVideoSelected(videoUri);
        }
    }

    public interface OnVideoSelectedListener {
        void onVideoSelected(Uri videoUri);
    }
}
