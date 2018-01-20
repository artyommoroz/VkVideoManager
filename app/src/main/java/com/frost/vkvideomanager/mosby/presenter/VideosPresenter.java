package com.frost.vkvideomanager.mosby.presenter;

import com.frost.vkvideomanager.mosby.view.VideosView;
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


public class VideosPresenter extends MvpBasePresenter<VideosView> {

    private int offset;
    private VKList<VKApiVideo> videos = new VKList<>();

    public void loadVideos(final boolean pullToRefresh, int ownerId, int albumId) {
        getView().showLoading(pullToRefresh);
        offset = 50;
        VKRequest request = VKApi.video().get(VKParameters.from(
                VKApiConst.COUNT, 50,
                VKApiConst.OWNER_ID, ownerId,
                VKApiConst.ALBUM_ID, albumId));
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

    public void loadMoreVideos(int ownerId, int albumId) {
        VKRequest request = VKApi.video().get(VKParameters.from(
                VKApiConst.COUNT, 50,
                VKApiConst.OWNER_ID, ownerId,
                VKApiConst.ALBUM_ID, albumId,
                VKApiConst.OFFSET, offset));
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

    public void deleteVideo(int position, int albumId) {
        String ids = albumId == 0 ? "-1, -2" : String.valueOf(albumId);
        VKRequest request = VKApi.video().removeFromAlbum(VKParameters.from(
                VKApiConst.VIDEO_ID, videos.get(position).id,
                VKApiConst.OWNER_ID, videos.get(position).owner_id,
                VKApiConst.ALBUM_IDS, ids));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                getView().videoDeleted(position, videos.get(position).title);
                videos.remove(position);
            }
        });
    }
}
