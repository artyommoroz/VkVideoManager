package com.frost.vkvideomanager.community;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKList;


public class CommunitiesPresenter extends MvpBasePresenter<CommunitiesView> {

    private VKList<VKApiCommunity> communities;

    public void loadCommunities(final boolean pullToRefresh) {
        getView().showLoading(pullToRefresh);

        VKRequest request = VKApi.groups().get(VKParameters.from(
                VKApiConst.COUNT, 1000,
                VKApiConst.EXTENDED, 1
        ));
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
                    communities = new VKList<>();
                    int friendsCount = ((VKList<VKApiCommunity>) response.parsedModel).getCount();
                    for (int i = 0; i < friendsCount; i++) {
                        VKApiCommunity community = ((VKList<VKApiCommunity>) response.parsedModel).get(i);
                        communities.add(community);
                    }
                    getView().setData(communities);
                    getView().showContent();
                }
            }
        });
    }

    public VKApiCommunity getSelectedFriend(int position) {
        return communities.get(position);
    }


}
