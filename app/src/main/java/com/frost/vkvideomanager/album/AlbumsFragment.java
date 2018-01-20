package com.frost.vkvideomanager.album;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.frost.vkvideomanager.R;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumsFragment extends MvpLceViewStateFragment<SwipeRefreshLayout, List<Album>, AlbumsView, AlbumsPresenter>
        implements AlbumsView, SwipeRefreshLayout.OnRefreshListener, AlbumAdapter.ItemClickListener,
        EditAlbumDialogFragment.EditDialogListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.noneItemsView)
    TextView noneItemsView;

    private AlbumAdapter adapter;

    public static AlbumsFragment newInstance() {
        return new AlbumsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        errorView.setCompoundDrawablesWithIntrinsicBounds(null,
                getResources().getDrawable(R.drawable.ic_cloud_off_black_96dp), null, null);
        adapter = new AlbumAdapter(getActivity(), this);
        recyclerView.setAdapter(adapter);
        int orientation = getActivity().getResources().getConfiguration().orientation;
        RecyclerView.LayoutManager layoutManager = orientation == Configuration.ORIENTATION_PORTRAIT ?
                new LinearLayoutManager(getActivity()) : new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);

        contentView.setOnRefreshListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;

    }

    @Override
    public AlbumsPresenter createPresenter() {
        return new AlbumsPresenter();
    }

    @Override
    public void setData(List<Album> data) {
        adapter.setAlbums(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public List<Album> getData() {
        return adapter == null ? null : adapter.getAlbums();
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadAlbums(pullToRefresh);
    }

    @Override
    public LceViewState<List<Album>, AlbumsView> createViewState() {
        setRetainInstance(true);
        return new RetainingLceViewState<>();
    }

    @Override
    public void showContent() {
        super.showContent();
        contentView.setRefreshing(false);
        if (adapter.getAlbums().isEmpty()) {
            noneItemsView.setVisibility(View.VISIBLE);
            noneItemsView.setText(getString(R.string.no_albums));
        }
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        super.showLoading(pullToRefresh);
        contentView.setRefreshing(pullToRefresh);
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return getString(R.string.no_connection_message);
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override
    public void itemClicked(View v, int position) {
        if (v instanceof RelativeLayout) {
            Intent albumIntent = new Intent(getActivity(), AlbumActivity.class);
            albumIntent.putExtra(AlbumActivity.ALBUM_ID, presenter.getSelectedAlbum(position).getId());
            albumIntent.putExtra(AlbumActivity.ALBUM_TITLE, presenter.getSelectedAlbum(position).getTitle());
            albumIntent.putExtra(AlbumActivity.OWNER_ID, presenter.getSelectedAlbum(position).getOwnerId());
            albumIntent.putExtra(AlbumActivity.IS_MY, true);
            startActivity(albumIntent);
        } else if (v instanceof ImageButton) {
            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
            popupMenu.inflate(R.menu.popup_menu_album);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.edit:
                        showEditAlbumDialog(position);
                        return true;
                    case R.id.delete:
                        showDeleteAlbumDialog(position);
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        }
    }

    private void showEditAlbumDialog(int position) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        EditAlbumDialogFragment editAlbumDialogFragment = EditAlbumDialogFragment.newInstance(
                presenter.getSelectedAlbum(position).getTitle(),
                presenter.getSelectedAlbum(position).getPrivacy(), position);
        editAlbumDialogFragment.setListener(this);
        editAlbumDialogFragment.show(ft, "hey");
    }

    @Override
    public void onPositiveClick(int position, String title, String privacy) {
        if (!presenter.getSelectedAlbum(position).getTitle().equals(title)
                || !presenter.getSelectedAlbum(position).getPrivacy().equals(privacy)) {
            int privacyNumber = 0;
            switch (privacy) {
                case "all": privacyNumber = 0; break;
                case "friends": privacyNumber = 1; break;
                case "friends_of_friends":
                case "friends_of_friends_only": privacyNumber = 2; break;
                case "nobody":
                case "only_me": privacyNumber = 3; break;
            }
            presenter.editAlbum(position, title, privacy, privacyNumber);
        }
    }

    @Override
    public void albumEdited(int position, String title) {
        Toast.makeText(getActivity(), getString(R.string.album_dialog_edit_success_toast, title),
                Toast.LENGTH_SHORT).show();
        adapter.notifyItemChanged(position);
    }

    private void showDeleteAlbumDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.album_dialog_delete_title))
                .setPositiveButton(getString(R.string.album_dialog_delete_positive_button),
                        (dialog, id) -> { presenter.deleteAlbum(position); })
                .setNegativeButton(getString(R.string.album_dialog_delete_negative_button),
                        (dialog, id) -> { dialog.cancel(); });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void albumDeleted(int position, String title) {
        Toast.makeText(getActivity(), getString(R.string.album_dialog_delete_success_toast,
                title), Toast.LENGTH_SHORT).show();
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, adapter.getItemCount());
    }
}
