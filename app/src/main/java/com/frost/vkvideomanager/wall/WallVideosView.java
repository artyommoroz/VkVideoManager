package com.frost.vkvideomanager.wall;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import java.util.List;


public interface WallVideosView extends MvpLceView<List<WallVideo>> {

    void moreWallVideosLoaded(int newSize);

    void playWallVideo(String wallVideoUrl);
}
