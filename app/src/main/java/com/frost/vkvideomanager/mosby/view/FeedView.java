package com.frost.vkvideomanager.mosby.view;

import com.frost.vkvideomanager.feed.FeedSectionModel;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import java.util.List;

/**
 * Created by frost on 17.10.16.
 */

public interface FeedView extends MvpLceView<List<FeedSectionModel>> {

    void moreVideosLoaded(List<FeedSectionModel> feedSectionModels, int oldSize);
}
