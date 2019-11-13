package org.ucimini.internetbrowserpro.search.engine

import org.ucimini.internetbrowserpro.R

/**
 * A custom search engine.
 */
class CustomSearch(queryUrl: String) : BaseSearchEngine(
        "file:///android_asset/lightning.png",
        queryUrl,
        R.string.search_engine_custom
)
