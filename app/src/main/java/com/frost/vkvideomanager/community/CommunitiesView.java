package com.frost.vkvideomanager.community;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;


public interface CommunitiesView extends MvpLceView<VKList<VKApiCommunity>> {
}
