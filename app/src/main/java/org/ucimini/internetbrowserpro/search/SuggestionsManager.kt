package org.ucimini.internetbrowserpro.search

import org.ucimini.internetbrowserpro.database_minipro.HistoryItem_mini
import org.ucimini.internetbrowserpro.search.suggestions.BaiduSuggestionsModel
import org.ucimini.internetbrowserpro.search.suggestions.DuckSuggestionsModel
import org.ucimini.internetbrowserpro.search.suggestions.GoogleSuggestionsModel
import android.app.Application
import com.anthonycr.bonsai.Single
import com.anthonycr.bonsai.SingleAction

internal object SuggestionsManager {

    @JvmStatic
    @Volatile var isRequestInProgress: Boolean = false

    @JvmStatic
    fun createGoogleQueryObservable(query: String, application: Application) =
            Single.create(SingleAction<List<HistoryItem_mini>> { subscriber ->
                isRequestInProgress = true
                val results = GoogleSuggestionsModel(application).fetchResults(query)
                subscriber.onItem(results)
                subscriber.onComplete()
                isRequestInProgress = false
            })

    @JvmStatic
    fun createBaiduQueryObservable(query: String, application: Application) =
            Single.create(SingleAction<List<HistoryItem_mini>> { subscriber ->
                isRequestInProgress = true
                val results = BaiduSuggestionsModel(application).fetchResults(query)
                subscriber.onItem(results)
                subscriber.onComplete()
                isRequestInProgress = false
            })

    @JvmStatic
    fun createDuckQueryObservable(query: String, application: Application) =
            Single.create(SingleAction<List<HistoryItem_mini>> { subscriber ->
                isRequestInProgress = true
                val results = DuckSuggestionsModel(application).fetchResults(query)
                subscriber.onItem(results)
                subscriber.onComplete()
                isRequestInProgress = false
            })

}
