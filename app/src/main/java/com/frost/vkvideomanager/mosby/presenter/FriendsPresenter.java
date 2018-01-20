package com.frost.vkvideomanager.mosby.presenter;

import com.frost.vkvideomanager.mosby.view.FriendsView;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;


public class FriendsPresenter extends MvpBasePresenter<FriendsView> {

    private VKList<VKApiUser> friends;

    public void loadFriends(final boolean pullToRefresh) {
        getView().showLoading(pullToRefresh);
        VKRequest request = VKApi.friends().get(VKParameters.from(
                VKApiConst.COUNT, 1000,
                VKApiConst.ORDER, "hints",
                VKApiConst.FIELDS, "photo_100"
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
                    friends = new VKList<>();
                    int friendsCount = ((VKList<VKApiUser>) response.parsedModel).getCount();
                    for (int i = 0; i < friendsCount; i++) {
                        VKApiUser friend = ((VKList<VKApiUser>) response.parsedModel).get(i);
                        friends.add(friend);
                    }
                    getView().setData(friends);
                    getView().showContent();
                }
            }
        });
    }

    public VKApiUser getSelectedFriend(int position) {
        return friends.get(position);
    }
}
