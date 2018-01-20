package com.frost.vkvideomanager.album;

import com.frost.vkvideomanager.network.Parser;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.util.List;


public class AlbumsPresenter extends MvpBasePresenter<AlbumsView> {

    private List<Album> albums;

    public void loadAlbums(final boolean pullToRefresh) {
        getView().showLoading(pullToRefresh);

        VKRequest request = VKApi.video().getAlbums(VKParameters.from(
                VKApiConst.COUNT, 100,
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
                    albums = Parser.parseAlbums(response);;
                    getView().setData(albums);
                    getView().showContent();
                }
            }
        });
    }

    public void deleteAlbum(int position) {
        VKRequest request = VKApi.video().deleteAlbum(VKParameters.from(
                VKApiConst.ALBUM_ID, albums.get(position).getId()
        ));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                if (response.json.optInt("response") == 1) {
                    getView().albumDeleted(position, albums.get(position).getTitle());
                    albums.remove(position);
                }
            }
        });
    }

    public void editAlbum(int position, String title, String privacy, int privacyNumber) {
        VKRequest request = VKApi.video().editAlbum(VKParameters.from(
                VKApiConst.ALBUM_ID, albums.get(position).getId(),
                VKApiConst.ALBUM_TITLE, title,
                VKApiConst.ALBUM_PRIVACY, privacyNumber));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                if (response.json.optInt("response") == 1) {
                    albums.get(position).setTitle(title);
                    albums.get(position).setPrivacy(privacy);
                    getView().albumEdited(position, albums.get(position).getTitle());
                }
            }
        });
    }

    public Album getSelectedAlbum(int position) {
        return albums.get(position);
    }
}
