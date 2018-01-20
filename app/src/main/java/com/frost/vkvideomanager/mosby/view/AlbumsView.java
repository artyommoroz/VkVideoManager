package com.frost.vkvideomanager.mosby.view;

import com.frost.vkvideomanager.album.Album;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import java.util.List;
import java.util.StringJoiner;

/**
 * Created by frost on 15.10.16.
 */

public interface AlbumsView extends MvpLceView<List<Album>> {

    void albumDeleted(int position, String title);

    void albumEdited(int position, String title);
}
