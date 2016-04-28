package com.frost.vkvideomanager.album;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.frost.vkvideomanager.R;
import com.frost.vkvideomanager.BaseFragment;
import com.frost.vkvideomanager.network.Parser;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AlbumsFragment extends BaseFragment implements AlbumAdapter.ItemClickListener,
        EditAlbumDialogFragment.EditDialogListener {

    @Bind(R.id.rootView)
    RelativeLayout rootView;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.noConnectionView)
    RelativeLayout noConnectionView;
    @Bind(R.id.retryButton)
    Button retryButton;
    @Bind(R.id.noVideosView)
    TextView noVideosView;

    private List<Album> albumList = new ArrayList<>();
    private AlbumAdapter albumAdapter;
    private boolean isCreated;
    private boolean noConnection;
    private boolean noAlbums;

    public AlbumsFragment() {}

    public static AlbumsFragment newInstance() {
        return new AlbumsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateAlbumList();
        isCreated = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        albumAdapter = new AlbumAdapter(getActivity(), albumList, AlbumsFragment.this);
        recyclerView.setAdapter(albumAdapter);

        if (isCreated) {
            progressBar.setVisibility(View.VISIBLE);
            isCreated = false;
        } else {
            progressBar.setVisibility(View.GONE);
        }

        if (noConnection  && albumList.isEmpty()) {
            noConnectionView.setVisibility(View.VISIBLE);
        } else if (!noConnection  && albumList.size() > 0) {
            noConnectionView.setVisibility(View.GONE);
        }

        if (noAlbums) {
            noVideosView.setText(R.string.no_albums);
            noVideosView.setVisibility(View.VISIBLE);
        } else {
            noVideosView.setVisibility(View.GONE);
        }

        int orientation = getActivity().getResources().getConfiguration().orientation;
        RecyclerView.LayoutManager layoutManager = null;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new LinearLayoutManager(getActivity());
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        }
        recyclerView.setLayoutManager(layoutManager);

        swipeRefresh.setOnRefreshListener(() -> updateAlbumList());

        retryButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            updateAlbumList();
        });
    }

    private void updateAlbumList() {
        VKRequest albumRequest = VKApi.video().getAlbums(VKParameters.from(
                VKApiConst.COUNT, 100,
                VKApiConst.EXTENDED, 1));
        albumRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onError(VKError error) {
                super.onError(error);
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                noConnection = true;
                if (albumList.isEmpty()) {
                    noConnectionView.setVisibility(View.VISIBLE);
                } else if (albumList.size() > 0){
                    Snackbar.make(rootView, getString(R.string.no_connection_snack_message), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.no_connection_snack_button), view -> {
                                updateAlbumList();
                            }).show();
                }
            }

            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                noConnectionView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                albumList.clear();
                albumList = Parser.parseAlbums(response);
                albumAdapter = new AlbumAdapter(getActivity(), albumList, AlbumsFragment.this);
                recyclerView.setAdapter(albumAdapter);
                if (albumList.isEmpty()) {
                    noVideosView.setText(R.string.no_albums);
                    noVideosView.setVisibility(View.VISIBLE);
                    noAlbums = true;
                }
            }
        });
    }

    @Override
    public void itemClicked(View v, int position) {
        if (v instanceof RelativeLayout) {
            Intent albumIntent = new Intent(getActivity(), AlbumActivity.class);
            albumIntent.putExtra(AlbumActivity.ALBUM_ID, albumList.get(position).getId());
            albumIntent.putExtra(AlbumActivity.ALBUM_TITLE, albumList.get(position).getTitle());
            albumIntent.putExtra(AlbumActivity.OWNER_ID, albumList.get(position).getOwnerId());
            albumIntent.putExtra(AlbumActivity.IS_MY, true);
            startActivity(albumIntent);
        } else if (v instanceof ImageButton) {
            showPopupMenu(v, position);
        }
    }

    private void showPopupMenu(View v, final int position) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        popupMenu.inflate(R.menu.popup_menu_album);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit:
                    editAlbum(position);
                    return true;
                case R.id.delete:
                    deleteAlbum(position);
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    private void editAlbum(int position) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        EditAlbumDialogFragment editAlbumDialogFragment = EditAlbumDialogFragment.newInstance(
                albumList.get(position).getTitle(), albumList.get(position).getPrivacy(), position);
        editAlbumDialogFragment.setListener(this);
        editAlbumDialogFragment.show(ft, "hey");
    }

    private void deleteAlbum(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.album_dialog_delete_title))
                .setPositiveButton(getString(R.string.album_dialog_delete_positive_button),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        VKRequest deleteRequest = VKApi.video().deleteAlbum(VKParameters.from(
                                VKApiConst.ALBUM_ID, albumList.get(position).getId()));
                        deleteRequest.executeWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                if (response.json.optInt("response") == 1) {
                                    Toast.makeText(getActivity(), getString(R.string.album_dialog_delete_success_toast,
                                            albumList.get(position).getTitle()),
                                            Toast.LENGTH_SHORT).show();
                                    albumList.remove(position);
                                    albumAdapter.notifyItemRemoved(position);
                                    albumAdapter.notifyItemRangeChanged(position, albumAdapter.getItemCount());
                                }
                            }
                        });
                        }
                })
                .setNegativeButton(getString(R.string.album_dialog_delete_negative_button), (dialog, id) -> {
                            dialog.cancel();
                        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onDialogPositiveClick(final String title, final String privacy, final int position) {
        if (!albumList.get(position).getTitle().equals(title)
                || !albumList.get(position).getPrivacy().equals(privacy)) {
            int privacyNumber = 0;
            switch (privacy) {
                case "all": privacyNumber = 0; break;
                case "friends": privacyNumber = 1; break;
                case "friends_of_friends":
                case "friends_of_friends_only": privacyNumber = 2; break;
                case "nobody":
                case "only_me": privacyNumber = 3; break;
            }
            VKRequest editRequest = VKApi.video().editAlbum(VKParameters.from(
                    VKApiConst.ALBUM_ID, albumList.get(position).getId(),
                    VKApiConst.ALBUM_TITLE, title,
                    VKApiConst.ALBUM_PRIVACY, privacyNumber));
            editRequest.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    if (response.json.optInt("response") == 1) {
                        Toast.makeText(getActivity(), getString(R.string.album_dialog_edit_success_toast,
                                albumList.get(position).getTitle()), Toast.LENGTH_SHORT).show();
                        albumList.get(position).setTitle(title);
                        albumList.get(position).setPrivacy(privacy);
                        albumAdapter.notifyItemChanged(position);
                    }
                }
            });
        }
    }

    public String getName() {
//        return getString(R.string.albums);
        return "АЛЬБОМЫ";
    }

}
