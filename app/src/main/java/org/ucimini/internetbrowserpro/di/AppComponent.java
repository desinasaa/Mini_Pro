package org.ucimini.internetbrowserpro.di;

import javax.inject.Singleton;

import org.ucimini.internetbrowserpro.BrowserApp;
import org.ucimini.internetbrowserpro.browser_minipro.activity.BrowserActivity;
import org.ucimini.internetbrowserpro.browser_minipro.fragment.BookmarksFragment;
import org.ucimini.internetbrowserpro.browser_minipro.fragment.TabsFragment;
import org.ucimini.internetbrowserpro.browser_minipro.BrowserPresenter_mini;
import org.ucimini.internetbrowserpro.browser_minipro.SearchBoxModel_mini;
import org.ucimini.internetbrowserpro.constant_minipro.DownloadsPage_mini;
import org.ucimini.internetbrowserpro.constant_minipro.HistoryPage_mini;
import org.ucimini.internetbrowserpro.download_minipro.DownloadHandler_mini;
import org.ucimini.internetbrowserpro.reading_minipro.activity.ReadingActivity;
import org.ucimini.internetbrowserpro.browser_minipro.TabsManager_mini;
import org.ucimini.internetbrowserpro.browser_minipro.activity.ThemableBrowserActivity;
import org.ucimini.internetbrowserpro.settings_minipro.activity.ThemableSettingsActivityMinipro;
import org.ucimini.internetbrowserpro.constant_minipro.BookmarkPage_mini;
import org.ucimini.internetbrowserpro.constant_minipro.StartPage_mini;
import org.ucimini.internetbrowserpro.dialog.LightningDialogBuilder;
import org.ucimini.internetbrowserpro.download_minipro.LightningDownloadListener_mini;
import org.ucimini.internetbrowserpro.settings_minipro.fragment.BookmarkSettingsFragment;
import org.ucimini.internetbrowserpro.settings_minipro.fragment.DebugSettingsFragment;
import org.ucimini.internetbrowserpro.settings_minipro.fragment.GeneralSettingsFragment;
import org.ucimini.internetbrowserpro.settings_minipro.fragment.LightningPreferenceFragment;
import org.ucimini.internetbrowserpro.settings_minipro.fragment.PrivacySettingsFragment;
import org.ucimini.internetbrowserpro.search.SearchEngineProvider;
import org.ucimini.internetbrowserpro.search.SuggestionsAdapter;
import org.ucimini.internetbrowserpro.utils.ProxyUtils;
import org.ucimini.internetbrowserpro.view_minipro.LightningChromeClient;
import org.ucimini.internetbrowserpro.view_minipro.LightningView;
import org.ucimini.internetbrowserpro.view_minipro.LightningWebClient;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(BrowserActivity activity);

    void inject(BookmarksFragment fragment);

    void inject(BookmarkSettingsFragment fragment);

    void inject(LightningDialogBuilder builder);

    void inject(TabsFragment fragment);

    void inject(LightningView lightningView);

    void inject(ThemableBrowserActivity activity);

    void inject(LightningPreferenceFragment fragment);

    void inject(BrowserApp app);

    void inject(ProxyUtils proxyUtils);

    void inject(ReadingActivity activity);

    void inject(LightningWebClient webClient);

    void inject(ThemableSettingsActivityMinipro activity);

    void inject(LightningDownloadListener_mini listener);

    void inject(PrivacySettingsFragment fragment);

    void inject(StartPage_mini StartPage_mini);

    void inject(HistoryPage_mini HistoryPage_mini);

    void inject(BookmarkPage_mini BookmarkPage_mini);

    void inject(DownloadsPage_mini DownloadsPage_mini);

    void inject(BrowserPresenter_mini presenter);

    void inject(TabsManager_mini manager);

    void inject(DebugSettingsFragment fragment);

    void inject(SuggestionsAdapter suggestionsAdapter);

    void inject(LightningChromeClient chromeClient);

    void inject(DownloadHandler_mini DownloadHandler_mini);

    void inject(SearchBoxModel_mini SearchBoxModel_mini);

    void inject(SearchEngineProvider searchEngineProvider);

    void inject(GeneralSettingsFragment generalSettingsFragment);

}
