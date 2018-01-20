package com.frost.vkvideomanager.mosby.view;

import com.frost.vkvideomanager.catalog.CatalogSection;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import java.util.List;


public interface CatalogView extends MvpLceView<List<CatalogSection>> {

    void moreCatalogSectionsLoaded(List<CatalogSection> catalogSections, int oldSize);
}
