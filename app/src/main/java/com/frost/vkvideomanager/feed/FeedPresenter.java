package com.frost.vkvideomanager.feed;

import com.frost.vkvideomanager.network.Parser;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class FeedPresenter extends MvpBasePresenter<FeedView> {

    private static final String NEWSFEED_REQUEST = "newsfeed.get";

    private String startFrom;
    private List<FeedSectionModel> feedSections = new ArrayList<>();

    public void loadFeedSections(final boolean pullToRefresh) {
        getView().showLoading(pullToRefresh);

        VKRequest request = new VKRequest(NEWSFEED_REQUEST, VKParameters.from(
                VKApiConst.FILTERS, "video",
                VKApiConst.COUNT, 20));
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
                    feedSections.clear();
                    try {
                        startFrom = response.json.optJSONObject("response").getString("next_from");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    feedSections = Parser.parseNewsFeed(response);
                    getView().setData(feedSections);
                    getView().showContent();
                }
            }
        });
    }

    public void loadMoreFeedSections() {
        if (!startFrom.isEmpty()) {
            VKRequest feedRequest = new VKRequest(NEWSFEED_REQUEST, VKParameters.from(
                    VKApiConst.FILTERS, "video",
                    VKApiConst.FEED_START_FROM, startFrom,
                    VKApiConst.COUNT, 20));
            feedRequest.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    if (isViewAttached()) {
                        try {
                            startFrom = response.json.optJSONObject("response").getString("next_from");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        List<FeedSectionModel> loadedFeedSectionModelList = Parser.parseNewsFeed(response);
                        getView().moreVideosLoaded(loadedFeedSectionModelList, feedSections.size());
                        feedSections.addAll(loadedFeedSectionModelList);
                    }
                }
            });
        }
    }

    public List<FeedSectionModel> getFeedSections() {
        return feedSections;
    }
}
