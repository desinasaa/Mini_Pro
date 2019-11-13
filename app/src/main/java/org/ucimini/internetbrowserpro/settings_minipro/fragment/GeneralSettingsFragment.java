/*
 * Copyright 2014 A.C.R. Development
 */
package org.ucimini.internetbrowserpro.settings_minipro.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;

import java.util.List;

import javax.inject.Inject;

import org.ucimini.internetbrowserpro.BuildConfig;
import org.ucimini.internetbrowserpro.R;
import org.ucimini.internetbrowserpro.BrowserApp;
import org.ucimini.internetbrowserpro.constant_minipro.Constants_mini;
import org.ucimini.internetbrowserpro.dialog.BrowserDialog;
import org.ucimini.internetbrowserpro.search.SearchEngineProvider;
import org.ucimini.internetbrowserpro.search.engine.BaseSearchEngine;
import org.ucimini.internetbrowserpro.search.engine.CustomSearch;
import org.ucimini.internetbrowserpro.utils.FileUtils;
import org.ucimini.internetbrowserpro.utils.ProxyUtils;
import org.ucimini.internetbrowserpro.utils.ThemeUtils;
import org.ucimini.internetbrowserpro.utils.Utils;

import static org.ucimini.internetbrowserpro.preference_minipro.PreferenceManager_mini.Suggestion;

public class GeneralSettingsFragment extends LightningPreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    private static final String SETTINGS_PROXY = "proxy";
    private static final String SETTINGS_FLASH = "cb_flash";
    private static final String SETTINGS_ADS = "cb_ads";
    private static final String SETTINGS_IMAGES = "cb_images";
    private static final String SETTINGS_JAVASCRIPT = "cb_javascript";
    private static final String SETTINGS_COLORMODE = "cb_colormode";
    private static final String SETTINGS_USERAGENT = "agent";
    private static final String SETTINGS_DOWNLOAD = "download";
    private static final String SETTINGS_HOME = "home";
    private static final String SETTINGS_SEARCHENGINE = "search";
    private static final String SETTINGS_SUGGESTIONS = "suggestions_choice";
    private Activity mActivity;
    private static final int API = android.os.Build.VERSION.SDK_INT;
    private CharSequence[] mProxyChoices;
    private Preference proxy, useragent, downloadloc, home, searchengine, searchsSuggestions;
    private String mDownloadLocation;
    private int mAgentChoice;
    private String mHomepage;

    @Inject SearchEngineProvider mSearchEngineProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_general);

        BrowserApp.getAppComponent().inject(this);

        mActivity = getActivity();

        initPrefs();
    }

    private void initPrefs() {
        proxy = findPreference(SETTINGS_PROXY);
        useragent = findPreference(SETTINGS_USERAGENT);
        downloadloc = findPreference(SETTINGS_DOWNLOAD);
        home = findPreference(SETTINGS_HOME);
        searchengine = findPreference(SETTINGS_SEARCHENGINE);
        searchsSuggestions = findPreference(SETTINGS_SUGGESTIONS);

        CheckBoxPreference cbFlash = (CheckBoxPreference) findPreference(SETTINGS_FLASH);
        CheckBoxPreference cbAds = (CheckBoxPreference) findPreference(SETTINGS_ADS);
        CheckBoxPreference cbImages = (CheckBoxPreference) findPreference(SETTINGS_IMAGES);
        CheckBoxPreference cbJsScript = (CheckBoxPreference) findPreference(SETTINGS_JAVASCRIPT);
        CheckBoxPreference cbColorMode = (CheckBoxPreference) findPreference(SETTINGS_COLORMODE);

        proxy.setOnPreferenceClickListener(this);
        useragent.setOnPreferenceClickListener(this);
        downloadloc.setOnPreferenceClickListener(this);
        home.setOnPreferenceClickListener(this);
        searchsSuggestions.setOnPreferenceClickListener(this);
        searchengine.setOnPreferenceClickListener(this);
        cbFlash.setOnPreferenceChangeListener(this);
        cbAds.setOnPreferenceChangeListener(this);
        cbImages.setOnPreferenceChangeListener(this);
        cbJsScript.setOnPreferenceChangeListener(this);
        cbColorMode.setOnPreferenceChangeListener(this);

        mAgentChoice = mPreferenceManagerMini.getUserAgentChoice();
        mHomepage = mPreferenceManagerMini.getHomepage();
        mDownloadLocation = mPreferenceManagerMini.getDownloadDirectory();
        mProxyChoices = getResources().getStringArray(R.array.proxy_choices_array);

        int choice = mPreferenceManagerMini.getProxyChoice();
        if (choice == Constants_mini.PROXY_MANUAL) {
            proxy.setSummary(mPreferenceManagerMini.getProxyHost() + ':' + mPreferenceManagerMini.getProxyPort());
        } else {
            proxy.setSummary(mProxyChoices[choice]);
        }

        if (API >= Build.VERSION_CODES.KITKAT) {
            mPreferenceManagerMini.setFlashSupport(0);
        }

        BaseSearchEngine currentSearchEngine = mSearchEngineProvider.getCurrentSearchEngine();
        setSearchEngineSummary(currentSearchEngine);

        downloadloc.setSummary(mDownloadLocation);

        switch (mPreferenceManagerMini.getSearchSuggestionChoice()) {
            case SUGGESTION_GOOGLE:
                searchsSuggestions.setSummary(R.string.powered_by_google);
                break;
            case SUGGESTION_DUCK:
                searchsSuggestions.setSummary(R.string.powered_by_duck);
                break;
            case SUGGESTION_BAIDU:
                searchsSuggestions.setSummary(R.string.powered_by_baidu);
                break;
            case SUGGESTION_NONE:
                searchsSuggestions.setSummary(R.string.search_suggestions_off);
                break;
        }


        if (mHomepage.contains(Constants_mini.SCHEME_HOMEPAGE)) {
            home.setSummary(getResources().getString(R.string.action_homepage));
        } else if (mHomepage.contains(Constants_mini.SCHEME_BLANK)) {
            home.setSummary(getResources().getString(R.string.action_blank));
        } else if (mHomepage.contains(Constants_mini.SCHEME_BOOKMARKS)) {
            home.setSummary(getResources().getString(R.string.action_bookmarks));
        } else {
            home.setSummary(mHomepage);
        }

        switch (mAgentChoice) {
            case 1:
                useragent.setSummary(getResources().getString(R.string.agent_default));
                break;
            case 2:
                useragent.setSummary(getResources().getString(R.string.agent_desktop));
                break;
            case 3:
                useragent.setSummary(getResources().getString(R.string.agent_mobile));
                break;
            case 4:
                useragent.setSummary(getResources().getString(R.string.agent_custom));
        }

        int flashNum = mPreferenceManagerMini.getFlashSupport();
        boolean imagesBool = mPreferenceManagerMini.getBlockImagesEnabled();
        boolean enableJSBool = mPreferenceManagerMini.getJavaScriptEnabled();

        cbAds.setEnabled(BuildConfig.FULL_VERSION);

        if (!BuildConfig.FULL_VERSION) {
            cbAds.setSummary(R.string.upsell_plus_version);
        }

        if (API < Build.VERSION_CODES.KITKAT) {
            cbFlash.setEnabled(true);
        } else {
            cbFlash.setEnabled(false);
            cbFlash.setSummary(R.string.flash_not_supported);
        }

        cbImages.setChecked(imagesBool);
        cbJsScript.setChecked(enableJSBool);
        cbFlash.setChecked(flashNum > 0);
        cbAds.setChecked(BuildConfig.FULL_VERSION && mPreferenceManagerMini.getAdBlockEnabled());
        cbColorMode.setChecked(mPreferenceManagerMini.getColorModeEnabled());
    }

    private void showUrlPicker(@NonNull final CustomSearch customSearch) {

        BrowserDialog.showEditText(mActivity,
            R.string.search_engine_custom,
            R.string.search_engine_custom,
            mPreferenceManagerMini.getSearchUrl(),
            R.string.action_ok,
            new BrowserDialog.EditorListener() {
                @Override
                public void onClick(String text) {
                    mPreferenceManagerMini.setSearchUrl(text);
                    setSearchEngineSummary(customSearch);
                }
            });

    }

    private void getFlashChoice() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(mActivity.getResources().getString(R.string.title_flash));
        builder.setMessage(getResources().getString(R.string.flash))
            .setCancelable(true)
            .setPositiveButton(getResources().getString(R.string.action_manual),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mPreferenceManagerMini.setFlashSupport(1);
                    }
                })
            .setNegativeButton(getResources().getString(R.string.action_auto),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPreferenceManagerMini.setFlashSupport(2);
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                mPreferenceManagerMini.setFlashSupport(0);
            }

        });
        AlertDialog alert = builder.create();
        alert.show();
        BrowserDialog.setDialogSize(mActivity, alert);
    }

    private void proxyChoicePicker() {
        AlertDialog.Builder picker = new AlertDialog.Builder(mActivity);
        picker.setTitle(R.string.http_proxy);
        picker.setSingleChoiceItems(mProxyChoices, mPreferenceManagerMini.getProxyChoice(),
            new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setProxyChoice(which);
                }
            });
        picker.setPositiveButton(R.string.action_ok, null);
        Dialog dialog = picker.show();
        BrowserDialog.setDialogSize(mActivity, dialog);
    }

    private void setProxyChoice(@Constants_mini.Proxy int choice) {
        switch (choice) {
            case Constants_mini.PROXY_ORBOT:
                choice = ProxyUtils.setProxyChoice(choice, mActivity);
                break;
            case Constants_mini.PROXY_I2P:
                choice = ProxyUtils.setProxyChoice(choice, mActivity);
                break;
            case Constants_mini.PROXY_MANUAL:
                manualProxyPicker();
                break;
            case Constants_mini.NO_PROXY:
                break;
        }

        mPreferenceManagerMini.setProxyChoice(choice);
        if (choice < mProxyChoices.length)
            proxy.setSummary(mProxyChoices[choice]);
    }

    private void manualProxyPicker() {
        View v = mActivity.getLayoutInflater().inflate(R.layout.dialog_manual_proxy, null);
        final EditText eProxyHost = v.findViewById(R.id.proxyHost);
        final EditText eProxyPort = v.findViewById(R.id.proxyPort);

        // Limit the number of characters since the port needs to be of type int
        // Use input filters to limite the EditText length and determine the max
        // length by using length of integer MAX_VALUE
        int maxCharacters = Integer.toString(Integer.MAX_VALUE).length();
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(maxCharacters - 1);
        eProxyPort.setFilters(filterArray);

        eProxyHost.setText(mPreferenceManagerMini.getProxyHost());
        eProxyPort.setText(Integer.toString(mPreferenceManagerMini.getProxyPort()));

        Dialog dialog = new AlertDialog.Builder(mActivity)
            .setTitle(R.string.manual_proxy)
            .setView(v)
            .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String proxyHost = eProxyHost.getText().toString();
                    int proxyPort;
                    try {
                        // Try/Catch in case the user types an empty string or a number
                        // larger than max integer
                        proxyPort = Integer.parseInt(eProxyPort.getText().toString());
                    } catch (NumberFormatException ignored) {
                        proxyPort = mPreferenceManagerMini.getProxyPort();
                    }
                    mPreferenceManagerMini.setProxyHost(proxyHost);
                    mPreferenceManagerMini.setProxyPort(proxyPort);
                    proxy.setSummary(proxyHost + ':' + proxyPort);
                }
            }).show();
        BrowserDialog.setDialogSize(mActivity, dialog);
    }

    @NonNull
    private CharSequence[] convertSearchEngineToString(@NonNull List<BaseSearchEngine> searchEngines) {
        CharSequence[] titles = new CharSequence[searchEngines.size()];

        for (int n = 0; n < searchEngines.size(); n++) {
            titles[n] = getString(searchEngines.get(n).getTitleRes());
        }

        return titles;
    }

    private void searchDialog() {
        AlertDialog.Builder picker = new AlertDialog.Builder(mActivity);
        picker.setTitle(getResources().getString(R.string.title_search_engine));

        final List<BaseSearchEngine> searchEngineList = mSearchEngineProvider.getAllSearchEngines();

        CharSequence[] chars = convertSearchEngineToString(searchEngineList);

        int n = mPreferenceManagerMini.getSearchChoice();

        picker.setSingleChoiceItems(chars, n, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                BaseSearchEngine searchEngine = searchEngineList.get(which);

                // Store the search engine preference
                int preferencesIndex = mSearchEngineProvider.mapSearchEngineToPreferenceIndex(searchEngine);
                mPreferenceManagerMini.setSearchChoice(preferencesIndex);

                if (searchEngine instanceof CustomSearch) {
                    // Show the URL picker
                    showUrlPicker((CustomSearch) searchEngine);
                } else {
                    // Set the new search engine summary
                    setSearchEngineSummary(searchEngine);
                }
            }
        });
        picker.setPositiveButton(R.string.action_ok, null);
        Dialog dialog = picker.show();
        BrowserDialog.setDialogSize(mActivity, dialog);
    }

    private void homepageDialog() {
        AlertDialog.Builder picker = new AlertDialog.Builder(mActivity);
        picker.setTitle(R.string.home);
        mHomepage = mPreferenceManagerMini.getHomepage();
        int n;
        switch (mHomepage) {
            case Constants_mini.SCHEME_HOMEPAGE:
                n = 0;
                break;
            case Constants_mini.SCHEME_BLANK:
                n = 1;
                break;
            case Constants_mini.SCHEME_BOOKMARKS:
                n = 2;
                break;
            default:
                n = 3;
                break;
        }

        picker.setSingleChoiceItems(R.array.homepage, n,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            mPreferenceManagerMini.setHomepage(Constants_mini.SCHEME_HOMEPAGE);
                            home.setSummary(getResources().getString(R.string.action_homepage));
                            break;
                        case 1:
                            mPreferenceManagerMini.setHomepage(Constants_mini.SCHEME_BLANK);
                            home.setSummary(getResources().getString(R.string.action_blank));
                            break;
                        case 2:
                            mPreferenceManagerMini.setHomepage(Constants_mini.SCHEME_BOOKMARKS);
                            home.setSummary(getResources().getString(R.string.action_bookmarks));
                            break;
                        case 3:
                            homePicker();
                            break;
                    }
                }
            });
        picker.setPositiveButton(getResources().getString(R.string.action_ok), null);
        Dialog dialog = picker.show();
        BrowserDialog.setDialogSize(mActivity, dialog);
    }

    private void suggestionsDialog() {
        AlertDialog.Builder picker = new AlertDialog.Builder(mActivity);
        picker.setTitle(getResources().getString(R.string.search_suggestions));

        int currentChoice = 3;

        switch (mPreferenceManagerMini.getSearchSuggestionChoice()) {
            case SUGGESTION_GOOGLE:
                currentChoice = 0;
                break;
            case SUGGESTION_DUCK:
                currentChoice = 1;
                break;
            case SUGGESTION_BAIDU:
                currentChoice = 2;
                break;
            case SUGGESTION_NONE:
                currentChoice = 3;
                break;
        }

        picker.setSingleChoiceItems(R.array.suggestions, currentChoice,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            mPreferenceManagerMini.setSearchSuggestionChoice(Suggestion.SUGGESTION_GOOGLE);
                            searchsSuggestions.setSummary(R.string.powered_by_google);
                            break;
                        case 1:
                            mPreferenceManagerMini.setSearchSuggestionChoice(Suggestion.SUGGESTION_DUCK);
                            searchsSuggestions.setSummary(R.string.powered_by_duck);
                            break;
                        case 2:
                            mPreferenceManagerMini.setSearchSuggestionChoice(Suggestion.SUGGESTION_BAIDU);
                            searchsSuggestions.setSummary(R.string.powered_by_baidu);
                            break;
                        case 3:
                            mPreferenceManagerMini.setSearchSuggestionChoice(Suggestion.SUGGESTION_NONE);
                            searchsSuggestions.setSummary(R.string.search_suggestions_off);
                            break;
                    }
                }
            });
        picker.setPositiveButton(getResources().getString(R.string.action_ok), null);
        Dialog dialog = picker.show();
        BrowserDialog.setDialogSize(mActivity, dialog);
    }

    private void homePicker() {
        String currentHomepage;
        mHomepage = mPreferenceManagerMini.getHomepage();
        if (!URLUtil.isAboutUrl(mHomepage)) {
            currentHomepage = mHomepage;
        } else {
            currentHomepage = "https://www.google.com";
        }

        BrowserDialog.showEditText(mActivity,
            R.string.title_custom_homepage,
            R.string.title_custom_homepage,
            currentHomepage,
            R.string.action_ok,
            new BrowserDialog.EditorListener() {
                @Override
                public void onClick(String text) {
                    mPreferenceManagerMini.setHomepage(text);
                    home.setSummary(text);
                }
            });
    }

    private void downloadLocDialog() {
        AlertDialog.Builder picker = new AlertDialog.Builder(mActivity);
        picker.setTitle(getResources().getString(R.string.title_download_location));
        mDownloadLocation = mPreferenceManagerMini.getDownloadDirectory();
        int n;
        if (mDownloadLocation.contains(Environment.DIRECTORY_DOWNLOADS)) {
            n = 0;
        } else {
            n = 1;
        }

        picker.setSingleChoiceItems(R.array.download_folder, n,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            mPreferenceManagerMini.setDownloadDirectory(FileUtils.DEFAULT_DOWNLOAD_PATH);
                            downloadloc.setSummary(FileUtils.DEFAULT_DOWNLOAD_PATH);
                            break;
                        case 1:
                            downPicker();
                            break;
                    }
                }
            });
        picker.setPositiveButton(getResources().getString(R.string.action_ok), null);
        Dialog dialog = picker.show();
        BrowserDialog.setDialogSize(mActivity, dialog);
    }

    private void agentDialog() {
        AlertDialog.Builder agentPicker = new AlertDialog.Builder(mActivity);
        agentPicker.setTitle(getResources().getString(R.string.title_user_agent));
        mAgentChoice = mPreferenceManagerMini.getUserAgentChoice();
        agentPicker.setSingleChoiceItems(R.array.user_agent, mAgentChoice - 1,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mPreferenceManagerMini.setUserAgentChoice(which + 1);
                    switch (which) {
                        case 0:
                            useragent.setSummary(getResources().getString(R.string.agent_default));
                            break;
                        case 1:
                            useragent.setSummary(getResources().getString(R.string.agent_desktop));
                            break;
                        case 2:
                            useragent.setSummary(getResources().getString(R.string.agent_mobile));
                            break;
                        case 3:
                            useragent.setSummary(getResources().getString(R.string.agent_custom));
                            agentPicker();
                            break;
                    }
                }
            });
        agentPicker.setPositiveButton(getResources().getString(R.string.action_ok), null);
        Dialog dialog = agentPicker.show();
        BrowserDialog.setDialogSize(mActivity, dialog);
    }

    private void agentPicker() {

        BrowserDialog.showEditText(mActivity,
            R.string.title_user_agent,
            R.string.title_user_agent,
            mPreferenceManagerMini.getUserAgentString(""),
            R.string.action_ok,
            new BrowserDialog.EditorListener() {
                @Override
                public void onClick(String text) {
                    mPreferenceManagerMini.setUserAgentString(text);
                    useragent.setSummary(mActivity.getString(R.string.agent_custom));
                }
            });
    }

    private void downPicker() {

        View dialogView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_edit_text, null);
        final EditText getDownload = dialogView.findViewById(R.id.dialog_edit_text);

        final int errorColor = ContextCompat.getColor(mActivity, R.color.error_red);
        final int regularColor = ThemeUtils.getTextColor(mActivity);
        getDownload.setTextColor(regularColor);
        getDownload.addTextChangedListener(new DownloadLocationTextWatcher(getDownload, errorColor, regularColor));
        getDownload.setText(mPreferenceManagerMini.getDownloadDirectory());

        AlertDialog.Builder downLocationPicker = new AlertDialog.Builder(mActivity)
            .setTitle(R.string.title_download_location)
            .setView(dialogView)
            .setPositiveButton(R.string.action_ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = getDownload.getText().toString();
                        text = FileUtils.addNecessarySlashes(text);
                        mPreferenceManagerMini.setDownloadDirectory(text);
                        downloadloc.setSummary(text);
                    }
                });
        Dialog dialog = downLocationPicker.show();
        BrowserDialog.setDialogSize(mActivity, dialog);
    }

    private void setSearchEngineSummary(BaseSearchEngine baseSearchEngine) {
        if (baseSearchEngine instanceof CustomSearch) {
            searchengine.setSummary(mPreferenceManagerMini.getSearchUrl());
        } else {
            searchengine.setSummary(getString(baseSearchEngine.getTitleRes()));
        }
    }

    @Override
    public boolean onPreferenceClick(@NonNull Preference preference) {
        switch (preference.getKey()) {
            case SETTINGS_PROXY:
                proxyChoicePicker();
                return true;
            case SETTINGS_USERAGENT:
                agentDialog();
                return true;
            case SETTINGS_DOWNLOAD:
                downloadLocDialog();
                return true;
            case SETTINGS_HOME:
                homepageDialog();
                return true;
            case SETTINGS_SEARCHENGINE:
                searchDialog();
                return true;
            case SETTINGS_SUGGESTIONS:
                suggestionsDialog();
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        boolean checked = false;
        if (newValue instanceof Boolean) {
            checked = Boolean.TRUE.equals(newValue);
        }
        switch (preference.getKey()) {
            case SETTINGS_FLASH:
                if (!Utils.isFlashInstalled(mActivity) && checked) {
                    Utils.createInformativeDialog(mActivity, R.string.title_warning, R.string.dialog_adobe_not_installed);
                    mPreferenceManagerMini.setFlashSupport(0);
                    return false;
                } else {
                    if (checked) {
                        getFlashChoice();
                    } else {
                        mPreferenceManagerMini.setFlashSupport(0);
                    }
                }
                return true;
            case SETTINGS_ADS:
                mPreferenceManagerMini.setAdBlockEnabled(checked);
                return true;
            case SETTINGS_IMAGES:
                mPreferenceManagerMini.setBlockImagesEnabled(checked);
                return true;
            case SETTINGS_JAVASCRIPT:
                mPreferenceManagerMini.setJavaScriptEnabled(checked);
                return true;
            case SETTINGS_COLORMODE:
                mPreferenceManagerMini.setColorModeEnabled(checked);
                return true;
            default:
                return false;
        }
    }

    private static class DownloadLocationTextWatcher implements TextWatcher {
        @NonNull private final EditText getDownload;
        private final int errorColor;
        private final int regularColor;

        public DownloadLocationTextWatcher(@NonNull EditText getDownload, int errorColor, int regularColor) {
            this.getDownload = getDownload;
            this.errorColor = errorColor;
            this.regularColor = regularColor;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(@NonNull Editable s) {
            if (!FileUtils.isWriteAccessAvailable(s.toString())) {
                this.getDownload.setTextColor(this.errorColor);
            } else {
                this.getDownload.setTextColor(this.regularColor);
            }
        }
    }
}
