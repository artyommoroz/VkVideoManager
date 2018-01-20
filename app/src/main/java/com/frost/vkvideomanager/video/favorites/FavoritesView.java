package com.frost.vkvideomanager.video.favorites;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;


public interface FavoritesView extends MvpLceView<VKList<VKApiVideo>> {

    void moreVideosLoaded(int newSize);
}
