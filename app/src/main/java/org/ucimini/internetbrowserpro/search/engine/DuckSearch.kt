package org.ucimini.internetbrowserpro.search.engine

import org.ucimini.internetbrowserpro.R
import org.ucimini.internetbrowserpro.constant_minipro.Constants_mini

/**
 * The DuckDuckGo search engine.
 *
 * See https://duckduckgo.com/assets/logo_homepage.normal.v101.png for the icon.
 */
class DuckSearch : BaseSearchEngine(
        "file:///android_asset/duckduckgo.png",
        Constants_mini.DUCK_SEARCH,
        R.string.search_engine_duckduckgo
)
