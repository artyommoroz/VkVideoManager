package com.frost.vkvideomanager.wall;

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

import java.util.ArrayList;
import java.util.List;


public class WallVideosPresenter extends MvpBasePresenter<WallVideosView> {

    private int offset;
    private List<WallVideo> wallVideos = new ArrayList<>();

    public void loadWallVideos(final boolean pullToRefresh, int ownerId) {
        getView().showLoading(pullToRefresh);
        offset = 100;
        VKRequest request = VKApi.wall().get(VKParameters.from(
                VKApiConst.OWNER_ID, ownerId,
                VKApiConst.COUNT, 100,
                VKApiConst.EXTENDED, 1));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onError(VKError error) {
                super.onError(error);
                if (isViewAttached()) {
                    getView().showError(null, pullToRefresh);
                }
            }

            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                if (isViewAttached()) {
                    wallVideos.clear();
                    wallVideos = Parser.parseWall(response);
                    getView().setData(wallVideos);
                    getView().showContent();
                }
            }
        });
    }

    public void loadMoreWallVideos(int ownerId) {
        VKRequest request = VKApi.wall().get(VKParameters.from(
                VKApiConst.OWNER_ID, ownerId,
                VKApiConst.OFFSET, offset,
                VKApiConst.COUNT, 100,
                VKApiConst.EXTENDED, 1));
        offset += offset;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                if (isViewAttached()) {
                    wallVideos.addAll(Parser.parseWall(response));
                    getView().moreWallVideosLoaded(wallVideos.size());
                }
            }
        });
    }

    public void getWallVideoUrl(int position) {
        VKRequest videoRequest = VKApi.video().get(VKParameters.from(
                VKApiConst.VIDEOS, wallVideos.get(position).getVideo().owner_id
                        + "_" + wallVideos.get(position).getVideo().id
//                        + "_" + wallVideos.get(position).getVideo().access_key));
                        + "_" + wallVideos.get(position).getVideo().access_key));
        videoRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                if (isViewAttached()) {
                    VKApiVideo vkApiVideo = ((VKList<VKApiVideo>) response.parsedModel).get(0);
                    getView().playWallVideo(vkApiVideo.player);
                }
            }
        });
    }

    public WallVideo getSelectedVideo(int position) {
        return wallVideos.get(position);
    }
}
