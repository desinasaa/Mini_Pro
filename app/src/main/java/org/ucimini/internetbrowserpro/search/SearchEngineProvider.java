package org.ucimini.internetbrowserpro.search;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.ucimini.internetbrowserpro.BrowserApp;
import org.ucimini.internetbrowserpro.preference_minipro.PreferenceManager_mini;
import org.ucimini.internetbrowserpro.search.engine.AskSearch;
import org.ucimini.internetbrowserpro.search.engine.BaiduSearch;
import org.ucimini.internetbrowserpro.search.engine.BaseSearchEngine;
import org.ucimini.internetbrowserpro.search.engine.BingSearch;
import org.ucimini.internetbrowserpro.search.engine.CustomSearch;
import org.ucimini.internetbrowserpro.search.engine.DuckLiteSearch;
import org.ucimini.internetbrowserpro.search.engine.DuckSearch;
import org.ucimini.internetbrowserpro.search.engine.GoogleSearch;
import org.ucimini.internetbrowserpro.search.engine.StartPageMobileSearch;
import org.ucimini.internetbrowserpro.search.engine.StartPageSearch;
import org.ucimini.internetbrowserpro.search.engine.YahooSearch;
import org.ucimini.internetbrowserpro.search.engine.YandexSearch;

/**
 * The model that provides the search engine based
 * on the user's preference.
 */
public class SearchEngineProvider {

    @Inject
    PreferenceManager_mini mPreferenceManagerMini;

    @Inject
    public SearchEngineProvider() {
        BrowserApp.getAppComponent().inject(this);
    }

    @NonNull
    public BaseSearchEngine getCurrentSearchEngine() {
        switch (mPreferenceManagerMini.getSearchChoice()) {
            case 0:
                return new CustomSearch(mPreferenceManagerMini.getSearchUrl());
            case 1:
            default:
                return new GoogleSearch();
            case 2:
                return new AskSearch();
            case 3:
                return new BingSearch();
            case 4:
                return new YahooSearch();
            case 5:
                return new StartPageSearch();
            case 6:
                return new StartPageMobileSearch();
            case 7:
                return new DuckSearch();
            case 8:
                return new DuckLiteSearch();
            case 9:
                return new BaiduSearch();
            case 10:
                return new YandexSearch();
        }
    }

    public int mapSearchEngineToPreferenceIndex(@NonNull BaseSearchEngine searchEngine) {
        if (searchEngine instanceof CustomSearch) {
            return 0;
        } else if (searchEngine instanceof GoogleSearch) {
            return 1;
        } else if (searchEngine instanceof AskSearch) {
            return 2;
        } else if (searchEngine instanceof BingSearch) {
            return 3;
        } else if (searchEngine instanceof YahooSearch) {
            return 4;
        } else if (searchEngine instanceof StartPageSearch) {
            return 5;
        } else if (searchEngine instanceof StartPageMobileSearch) {
            return 6;
        } else if (searchEngine instanceof DuckSearch) {
            return 7;
        } else if (searchEngine instanceof DuckLiteSearch) {
            return 8;
        } else if (searchEngine instanceof BaiduSearch) {
            return 9;
        } else if (searchEngine instanceof YandexSearch) {
            return 10;
        } else {
            throw new UnsupportedOperationException("Unknown search engine provided: " + searchEngine.getClass());
        }
    }

    @NonNull
    public List<BaseSearchEngine> getAllSearchEngines() {
        return new ArrayList<BaseSearchEngine>(11) {{
            add(new CustomSearch(mPreferenceManagerMini.getSearchUrl()));
            add(new GoogleSearch());
            add(new AskSearch());
            add(new BingSearch());
            add(new YahooSearch());
            add(new StartPageSearch());
            add(new StartPageMobileSearch());
            add(new DuckSearch());
            add(new DuckLiteSearch());
            add(new BaiduSearch());
            add(new YandexSearch());
        }};
    }

}
