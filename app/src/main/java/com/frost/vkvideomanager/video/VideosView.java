package com.frost.vkvideomanager.video;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;


public interface VideosView extends MvpLceView<VKList<VKApiVideo>> {

    void moreVideosLoaded(int newSize);

    void videoDeleted(int position, String title);
}
