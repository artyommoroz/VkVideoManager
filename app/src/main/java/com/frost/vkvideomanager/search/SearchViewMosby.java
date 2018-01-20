package com.frost.vkvideomanager.search;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;


public interface SearchViewMosby extends MvpLceView<VKList<VKApiVideo>> {

    void moreVideosLoaded(int newSize);
}
