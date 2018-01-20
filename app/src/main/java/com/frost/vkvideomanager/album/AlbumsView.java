package com.frost.vkvideomanager.album;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import java.util.List;

public interface AlbumsView extends MvpLceView<List<Album>> {

    void albumDeleted(int position, String title);

    void albumEdited(int position, String title);
}
