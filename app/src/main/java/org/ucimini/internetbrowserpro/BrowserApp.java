package org.ucimini.internetbrowserpro;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.webkit.WebView;

import com.anthonycr.bonsai.Schedulers;
import com.squareup.leakcanary.LeakCanary;

import java.util.List;

import javax.inject.Inject;

import org.ucimini.internetbrowserpro.database_minipro.HistoryItem_mini;
import org.ucimini.internetbrowserpro.database_minipro.bookmark.BookmarkExporter;
import org.ucimini.internetbrowserpro.database_minipro.bookmark.BookmarkModel;
import org.ucimini.internetbrowserpro.database_minipro.bookmark.legacy.LegacyBookmarkManager;
import org.ucimini.internetbrowserpro.di.AppComponent;
import org.ucimini.internetbrowserpro.di.AppModule;
import org.ucimini.internetbrowserpro.di.DaggerAppComponent;
import org.ucimini.internetbrowserpro.preference_minipro.PreferenceManager_mini;
import org.ucimini.internetbrowserpro.utils.FileUtils;
import org.ucimini.internetbrowserpro.utils.MemoryLeakUtils;
import org.ucimini.internetbrowserpro.utils.Preconditions;

public class BrowserApp extends Application {

    private static final String TAG = "BrowserApp";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT);
    }

    @Nullable private static AppComponent sAppComponent;

    @Inject
    PreferenceManager_mini mPreferenceManagerMini;
    @Inject BookmarkModel mBookmarkModel;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        }

        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, @NonNull Throwable ex) {

                if (BuildConfig.DEBUG) {
                    FileUtils.writeCrashToStorage(ex);
                }

                if (defaultHandler != null) {
                    defaultHandler.uncaughtException(thread, ex);
                } else {
                    System.exit(2);
                }
            }
        });

        sAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        sAppComponent.inject(this);

        Schedulers.worker().execute(new Runnable() {
            @Override
            public void run() {
                List<HistoryItem_mini> oldBookmarks = LegacyBookmarkManager.destructiveGetBookmarks(BrowserApp.this);

                if (!oldBookmarks.isEmpty()) {
                    // If there are old bookmarks, import them
                    mBookmarkModel.addBookmarkList(oldBookmarks).subscribeOn(Schedulers.io()).subscribe();
                } else if (mBookmarkModel.count() == 0) {
                    // If the database is empty, fill it from the assets list
                    List<HistoryItem_mini> assetsBookmarks = BookmarkExporter.importBookmarksFromAssets(BrowserApp.this);
                    mBookmarkModel.addBookmarkList(assetsBookmarks).subscribeOn(Schedulers.io()).subscribe();
                }
            }
        });

        if (mPreferenceManagerMini.getUseLeakCanary() && !isRelease()) {
            LeakCanary.install(this);
        }
        if (!isRelease() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        registerActivityLifecycleCallbacks(new MemoryLeakUtils.LifecycleAdapter() {
            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d(TAG, "Cleaning up after the Android framework");
                MemoryLeakUtils.clearNextServedView(activity, BrowserApp.this);
            }
        });
    }

    @NonNull
    public static AppComponent getAppComponent() {
        Preconditions.checkNonNull(sAppComponent);
        return sAppComponent;
    }

    /**
     * Determines whether this is a release build.
     *
     * @return true if this is a release build, false otherwise.
     */
    public static boolean isRelease() {
        return !BuildConfig.DEBUG || BuildConfig.BUILD_TYPE.toLowerCase().equals("release");
    }

    public static void copyToClipboard(@NonNull Context context, @NonNull String string) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("URL", string);
        clipboard.setPrimaryClip(clip);
    }

}
