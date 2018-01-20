package com.frost.vkvideomanager.mosby.view;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.vk.sdk.api.model.VKApiVideo;
import com.vk.sdk.api.model.VKList;

/**
 * Created by frost on 25.10.16.
 */

public interface SearchViewMosby extends MvpLceView<VKList<VKApiVideo>> {

    void moreVideosLoaded(int newSize);
}
