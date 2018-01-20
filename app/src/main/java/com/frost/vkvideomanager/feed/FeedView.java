package com.frost.vkvideomanager.feed;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import java.util.List;


public interface FeedView extends MvpLceView<List<FeedSectionModel>> {

    void moreVideosLoaded(List<FeedSectionModel> feedSectionModels, int oldSize);
}
