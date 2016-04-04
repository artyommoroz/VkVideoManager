package com.example.frost.vkvideomanager.album;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.frost.vkvideomanager.R;
import com.example.frost.vkvideomanager.network.Parser;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AlbumsFragment extends Fragment implements AlbumAdapter.ItemClickListener,
        EditAlbumDialogFragment.EditDialogListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefresh;

    private List<Album> albumList = new ArrayList<>();
    private AlbumAdapter albumAdapter;
    private boolean isCreated;

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

        int orientation = getActivity().getResources().getConfiguration().orientation;
        RecyclerView.LayoutManager layoutManager = null;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new LinearLayoutManager(getActivity());
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        }
        recyclerView.setLayoutManager(layoutManager);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateAlbumList();
            }
        });
    }

    private void updateAlbumList() {
        VKRequest albumRequest = VKApi.video().getAlbums(VKParameters.from(VKApiConst.EXTENDED, 1));
        albumRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                progressBar.setVisibility(View.INVISIBLE);
                swipeRefresh.setRefreshing(false);
                albumList.clear();
                albumList = Parser.parseAlbums(response);
                albumAdapter = new AlbumAdapter(getActivity(), albumList, AlbumsFragment.this);
                recyclerView.setAdapter(albumAdapter);
            }
        });
    }

    @Override
    public void itemClicked(View v, int position) {
        if (v instanceof LinearLayout) {
            Intent albumIntent = new Intent(getActivity(), AlbumActivity.class);
            albumIntent.putExtra("ownerId", albumList.get(position).getOwnerId());
            albumIntent.putExtra("albumId", albumList.get(position).getId());
            albumIntent.putExtra("albumTitle", albumList.get(position).getTitle());
            albumIntent.putExtra("isMy", true);
            startActivity(albumIntent);
        } else if (v instanceof ImageButton) {
            showPopupMenu(v, position);
        }
    }

    private void showPopupMenu(View v, final int position) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        popupMenu.inflate(R.menu.popup_menu_album);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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
            }
        });
        popupMenu.show();
    }

    private void editAlbum(int position) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        EditAlbumDialogFragment editAlbumDialogFragment = EditAlbumDialogFragment.newInstance(
                albumList.get(position).getTitle(), albumList.get(position).getPrivacy(), position);
        editAlbumDialogFragment.setListener(this);
        editAlbumDialogFragment.show(ft, "ss");
    }

    private void deleteAlbum(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Вы уверены, что хотите удалить этот альбом?")
                .setPositiveButton("ДА", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        VKRequest deleteRequest = VKApi.video().deleteAlbum(VKParameters.from(
                                VKApiConst.ALBUM_ID, albumList.get(position).getId()));
                        deleteRequest.executeWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                if (response.json.optInt("response") == 1) {
                                    Toast.makeText(getActivity(), "Альбом " + albumList.get(position).getTitle()
                                            + " был успешно удален", Toast.LENGTH_SHORT).show();
                                    albumList.remove(position);
                                    albumAdapter.notifyItemRemoved(position);
                                    albumAdapter.notifyItemRangeChanged(position, albumAdapter.getItemCount());
                                }
                            }
                        });
                        }
                })
                .setNegativeButton("НЕТ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    public String getName() {
        return "AЛЬБОМЫ";
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
                        Toast.makeText(getActivity(), "Альбом " + albumList.get(position).getTitle()
                                + " был успешно отредактирован", Toast.LENGTH_SHORT).show();
                        albumList.get(position).setTitle(title);
                        albumList.get(position).setPrivacy(privacy);
                        albumAdapter.notifyItemChanged(position);
                    }
                }
            });
        }
    }
}
