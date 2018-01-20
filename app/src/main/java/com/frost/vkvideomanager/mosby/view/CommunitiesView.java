package com.frost.vkvideomanager.mosby.view;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

/**
 * Created by frost on 13.10.16.
 */

public interface CommunitiesView extends MvpLceView<VKList<VKApiCommunity>> {
}
