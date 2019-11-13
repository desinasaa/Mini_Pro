package org.ucimini.internetbrowserpro.search.engine

import org.ucimini.internetbrowserpro.R
import org.ucimini.internetbrowserpro.constant_minipro.Constants_mini

/**
 * The Yahoo search engine.
 *
 * See http://upload.wikimedia.org/wikipedia/commons/thumb/2/24/Yahoo%21_logo.svg/799px-Yahoo%21_logo.svg.png
 * for the icon.
 */
class YahooSearch : BaseSearchEngine(
        "file:///android_asset/yahoo.png",
        Constants_mini.YAHOO_SEARCH,
        R.string.search_engine_yahoo
)
