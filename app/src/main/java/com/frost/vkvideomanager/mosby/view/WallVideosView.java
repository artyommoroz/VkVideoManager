package com.frost.vkvideomanager.mosby.view;

import com.frost.vkvideomanager.wall.WallVideo;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import java.util.List;

/**
 * Created by frost on 16.10.16.
 */

public interface WallVideosView extends MvpLceView<List<WallVideo>> {

    void moreWallVideosLoaded(int newSize);

    void playWallVideo(String wallVideoUrl);
}
