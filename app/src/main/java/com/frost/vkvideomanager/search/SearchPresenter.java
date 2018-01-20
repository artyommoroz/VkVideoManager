package com.frost.vkvideomanager.search;

import com.frost.vkvideomanager.network.Parser;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;


public class SearchPresenter extends MvpBasePresenter<SearchViewMosby> {

    private int offset;
    private VKList<VKApiVideo> videos = new VKList<>();

    public void loadVideos(final boolean pullToRefresh, final String query, int hd, int adult, int sort, String duration) {
        getView().showLoading(pullToRefresh);
        offset = 50;
        VKRequest searchRequest = VKApi.video().search(VKParameters.from(
                VKApiConst.Q, query,
                VKApiConst.HD, hd,
                VKApiConst.ADULT, adult,
                VKApiConst.SORT, sort,
                VKApiConst.FILTERS, duration,
                VKApiConst.COUNT, 50
        ));
        searchRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onError(VKError error) {
                super.onError(error);
                getView().showError(null, pullToRefresh);
            }

            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                if (isViewAttached()) {
                    videos.clear();
                    videos = Parser.parseVideos(response);
                    getView().setData(videos);
                    getView().showContent();
                }
            }
        });
    }

    public void loadMoreVideos(final String query, int hd, int adult, int sort, String duration) {
        VKRequest request = VKApi.video().search(VKParameters.from(
                VKApiConst.Q, query,
                VKApiConst.HD, hd,
                VKApiConst.ADULT, adult,
                VKApiConst.SORT, sort,
                VKApiConst.FILTERS, duration,
                VKApiConst.COUNT, 50,
                VKApiConst.OFFSET, offset
        ));
        offset += offset;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                if (isViewAttached()) {
                    videos.addAll(Parser.parseVideos(response));
                    getView().moreVideosLoaded(videos.size());
                }
            }
        });
    }

    public VKApiVideo getSelectedVideo(int position) {
        return videos.get(position);
    }
}
