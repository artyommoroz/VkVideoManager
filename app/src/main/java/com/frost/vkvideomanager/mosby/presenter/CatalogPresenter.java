package com.frost.vkvideomanager.mosby.presenter;

import com.frost.vkvideomanager.catalog.CatalogSection;
import com.frost.vkvideomanager.catalog.CatalogSectionAdapter;
import com.frost.vkvideomanager.feed.FeedSectionModel;
import com.frost.vkvideomanager.mosby.view.CatalogView;
import com.frost.vkvideomanager.network.Parser;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by frost on 24.10.16.
 */

public class CatalogPresenter extends MvpBasePresenter<CatalogView> {

    private static final String CATALOG_REQUEST = "video.getCatalog";

    private String next;
    private List<CatalogSection> catalogSections = new ArrayList<>();

    public void loadFeedSections(final boolean pullToRefresh) {
        getView().showLoading(pullToRefresh);

        VKRequest request = new VKRequest(CATALOG_REQUEST, VKParameters.from(
                VKApiConst.COUNT, 16,
                VKApiConst.EXTENDED, 1,
                VKApiConst.FILTERS, "other"));
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
                    catalogSections.clear();
                    try {
                        next = response.json.optJSONObject("response").getString("next");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    catalogSections = Parser.parseCatalog(response);
                    getView().setData(catalogSections);
                    getView().showContent();
                }
            }
        });
    }

    public void loadMoreCatalogSections() {
        VKRequest request = new VKRequest(CATALOG_REQUEST, VKParameters.from(
                VKApiConst.COUNT, 16,
                VKApiConst.CATALOG_FROM, next,
                VKApiConst.EXTENDED, 1,
                VKApiConst.FILTERS, "other"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                if (isViewAttached()) {
                    try {
                        next = response.json.optJSONObject("response").getString("next");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    List<CatalogSection> loadedCatalogSections = Parser.parseCatalog(response);
                    getView().moreCatalogSectionsLoaded(loadedCatalogSections, catalogSections.size());
                    catalogSections.addAll(loadedCatalogSections);
                }
            }
        });
    }

    public List<CatalogSection> getCatalogSections() {
        return catalogSections;
    }
}
