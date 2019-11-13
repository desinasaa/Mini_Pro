package org.ucimini.internetbrowserpro.di;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

// import net.i2p.android.ui.I2PAndroidHelper;

import javax.inject.Singleton;

import org.ucimini.internetbrowserpro.BrowserApp;
import org.ucimini.internetbrowserpro.database_minipro.bookmark.BookmarkDatabase;
import org.ucimini.internetbrowserpro.database_minipro.bookmark.BookmarkModel;
import org.ucimini.internetbrowserpro.database_minipro.downloads.DownloadsDatabase;
import org.ucimini.internetbrowserpro.database_minipro.downloads.DownloadsModel;
import org.ucimini.internetbrowserpro.database_minipro.history.HistoryDatabase;
import org.ucimini.internetbrowserpro.database_minipro.history.HistoryModel;
import org.ucimini.internetbrowserpro.download_minipro.DownloadHandler_mini;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final BrowserApp mApp;

    public AppModule(BrowserApp app) {
        this.mApp = app;
    }

    @Provides
    public Application provideApplication() {
        return mApp;
    }

    @Provides
    public Context provideContext() {
        return mApp.getApplicationContext();
    }

    @NonNull
    @Provides
    @Singleton
    public BookmarkModel provideBookmarkModel() {
        return new BookmarkDatabase(mApp);
    }

    @NonNull
    @Provides
    @Singleton
    public DownloadsModel provideDownloadsModel() {
        return new DownloadsDatabase(mApp);
    }

    @NonNull
    @Provides
    @Singleton
    public HistoryModel providesHistoryModel() {
        return new HistoryDatabase(mApp);
    }

    @NonNull
    @Provides
    @Singleton
    public DownloadHandler_mini provideDownloadHandler() {
        return new DownloadHandler_mini();
    }

    // @NonNull
    // @Provides
    // @Singleton
    // public I2PAndroidHelper provideI2PAndroidHelper() {
    //    return new I2PAndroidHelper(mApp.getApplicationContext());
    // }

}
