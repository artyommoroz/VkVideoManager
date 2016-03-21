package com.example.frost.vkvideomanager.network;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.example.frost.vkvideomanager.album.Album;
import com.example.frost.vkvideomanager.dialog_fragment.PickAlbumDialogFragment;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKBatchRequest;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiVideo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frost on 21.03.16.
 */
public class AdditionRequests {

    public static void addVideo(final Context context, final VKApiVideo vkApiVideo) {
        VKRequest addVideoRequest = VKApi.video().add(VKParameters.from(
                VKApiConst.VIDEO_ID, vkApiVideo.id,
                VKApiConst.OWNER_ID, vkApiVideo.owner_id));
        addVideoRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                if (response.json.optInt("response") == 1) {
                    Toast.makeText(context, "Видеозапись "
                            + vkApiVideo.title + " была успешно добавлена", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void addVideoToAlbum(final FragmentManager fm, final VKApiVideo vkApiVideo) {
        VKRequest albumsRequest = VKApi.video().getAlbums(VKParameters.from(VKApiConst.EXTENDED, 1));
        VKRequest albumsByVideoRequest = VKApi.video().getAlbumsByVideo(VKParameters.from(
                VKApiConst.OWNER_ID, vkApiVideo.owner_id,
                VKApiConst.VIDEO_ID, vkApiVideo.id,
                VKApiConst.EXTENDED, 1));
        VKBatchRequest batchRequest = new VKBatchRequest(albumsRequest, albumsByVideoRequest);
        batchRequest.executeWithListener(new VKBatchRequest.VKBatchRequestListener() {
            @Override
            public void onComplete(VKResponse[] responses) {
                super.onComplete(responses);
                ArrayList<Album> albumList = (ArrayList<Album>) Parser.parseAlbums(responses[0]);
//                                    List<Album> albumByVideoList = Parser.parseAlbums(responses[1]);
                List<Album> albumByVideoList = new ArrayList<Album>();
                JSONObject jResponse = responses[1].json.optJSONObject("response");
                JSONArray jAlbums = jResponse.optJSONArray("items");
                for (int i = 0; i < jAlbums.length(); i++) {
                    Album album = null;
                    try {
                        JSONObject jAlbum = jAlbums.getJSONObject(i);
                        album = new Album(jAlbum, "sss");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    };
                    albumByVideoList.add(album);
                }
                Log.d("AlbumResponse", responses[1].responseString);
                for (int i = 0; i < albumList.size(); i++) {
                    for (int j = 0; j < albumByVideoList.size(); j++) {
                        if (albumList.get(i).getId() == albumByVideoList.get(j).getId()) {
                            albumList.get(i).setSelected(true);
                            break;
                        }
                    }
                }
                FragmentTransaction ft = fm.beginTransaction();
                PickAlbumDialogFragment pickAlbumFragment = PickAlbumDialogFragment.newInstance(
                        vkApiVideo.id, vkApiVideo.owner_id, albumList);
                pickAlbumFragment.show(ft, "ss");
            }
        });
    }
}
