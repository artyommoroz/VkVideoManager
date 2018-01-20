package com.frost.vkvideomanager.video.favorites;

import com.frost.vkvideomanager.network.Parser;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;


public class FavoritesPresenter extends MvpBasePresenter<FavoritesView> {

    private static final String FAVORITES_REQUEST = "fave.getVideos";

    private int offset;
    private VKList<VKApiVideo> videos = new VKList<>();

    public void loadVideos(final boolean pullToRefresh) {
        getView().showLoading(pullToRefresh);
        offset = 50;
        VKRequest request = new VKRequest(FAVORITES_REQUEST, VKParameters.from(VKApiConst.COUNT, 50));
        request.executeWithListener(new VKRequest.VKRequestListener() {
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

    public void loadMoreVideos() {
        VKRequest request = new VKRequest(FAVORITES_REQUEST, VKParameters.from(
                VKApiConst.OFFSET, offset,
                VKApiConst.COUNT, 50),
                VKApiVideo.class
        );
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
