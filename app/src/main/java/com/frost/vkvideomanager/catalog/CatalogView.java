package com.frost.vkvideomanager.catalog;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import java.util.List;


public interface CatalogView extends MvpLceView<List<CatalogSection>> {

    void moreCatalogSectionsLoaded(List<CatalogSection> catalogSections, int oldSize);
}
