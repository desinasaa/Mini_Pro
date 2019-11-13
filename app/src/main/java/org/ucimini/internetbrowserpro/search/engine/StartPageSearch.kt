package org.ucimini.internetbrowserpro.search.engine

import org.ucimini.internetbrowserpro.R
import org.ucimini.internetbrowserpro.constant_minipro.Constants_mini

/**
 * The StartPage_mini search engine.
 */
class StartPageSearch : BaseSearchEngine(
        "file:///android_asset/startpage.png",
        Constants_mini.STARTPAGE_SEARCH,
        R.string.search_engine_startpage
)
