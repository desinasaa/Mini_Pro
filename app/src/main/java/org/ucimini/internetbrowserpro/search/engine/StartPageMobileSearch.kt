package org.ucimini.internetbrowserpro.search.engine

import org.ucimini.internetbrowserpro.R
import org.ucimini.internetbrowserpro.constant_minipro.Constants_mini

/**
 * The StartPage_mini mobile search engine.
 */
class StartPageMobileSearch : BaseSearchEngine(
        "file:///android_asset/startpage.png",
        Constants_mini.STARTPAGE_MOBILE_SEARCH,
        R.string.search_engine_startpage_mobile
)
