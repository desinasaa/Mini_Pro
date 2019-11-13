package org.ucimini.internetbrowserpro.search.engine

import org.ucimini.internetbrowserpro.R
import org.ucimini.internetbrowserpro.constant_minipro.Constants_mini

/**
 * The Yandex search engine.
 *
 * See http://upload.wikimedia.org/wikipedia/commons/thumb/9/91/Yandex.svg/600px-Yandex.svg.png
 * for the icon.
 */
class YandexSearch : BaseSearchEngine(
        "file:///android_asset/yandex.png",
        Constants_mini.YANDEX_SEARCH,
        R.string.search_engine_yandex
)
