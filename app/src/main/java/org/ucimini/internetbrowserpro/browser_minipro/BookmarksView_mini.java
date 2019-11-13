package org.ucimini.internetbrowserpro.browser_minipro;

import android.support.annotation.NonNull;

import org.ucimini.internetbrowserpro.database_minipro.HistoryItem_mini;

public interface BookmarksView_mini {

    void navigateBack();

    void handleUpdatedUrl(@NonNull String url);

    void handleBookmarkDeleted(@NonNull HistoryItem_mini item);

}
