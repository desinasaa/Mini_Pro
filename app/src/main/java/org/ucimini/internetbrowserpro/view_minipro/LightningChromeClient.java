package org.ucimini.internetbrowserpro.view_minipro;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.anthonycr.bonsai.Schedulers;
import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;

import javax.inject.Inject;

import org.ucimini.internetbrowserpro.R;
import org.ucimini.internetbrowserpro.favicon_minipro.FaviconModel_mini;
import org.ucimini.internetbrowserpro.BrowserApp;
import org.ucimini.internetbrowserpro.controller_minipro.UIController_mini;
import org.ucimini.internetbrowserpro.dialog.BrowserDialog;
import org.ucimini.internetbrowserpro.utils.Preconditions;

public class LightningChromeClient extends WebChromeClient {

    private static final String[] PERMISSIONS = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

    @NonNull private final Activity mActivity;
    @NonNull private final LightningView mLightningView;
    @NonNull private final UIController_mini mXUIControllerMini;
    @Inject
    FaviconModel_mini mFaviconModelMini;

    LightningChromeClient(@NonNull Activity activity, @NonNull LightningView lightningView) {
        Preconditions.checkNonNull(activity);
        Preconditions.checkNonNull(lightningView);
        BrowserApp.getAppComponent().inject(this);
        mActivity = activity;
        mXUIControllerMini = (UIController_mini) activity;
        mLightningView = lightningView;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (mLightningView.isShown()) {
            mXUIControllerMini.updateProgress(newProgress);
        }
    }

    @Override
    public void onReceivedIcon(@NonNull WebView view, Bitmap icon) {
        mLightningView.getTitleInfo().setFavicon(icon);
        mXUIControllerMini.tabChanged(mLightningView);
        cacheFavicon(view.getUrl(), icon);
    }

    /**
     * Naive caching of the favicon according to the domain name of the URL
     *
     * @param icon the icon to cache
     */
    private void cacheFavicon(@Nullable final String url, @Nullable final Bitmap icon) {
        if (icon == null || url == null) {
            return;
        }

        mFaviconModelMini.cacheFaviconForUrl(icon, url)
            .subscribeOn(Schedulers.io())
            .subscribe();
    }


    @Override
    public void onReceivedTitle(@Nullable WebView view, @Nullable String title) {
        if (title != null && !title.isEmpty()) {
            mLightningView.getTitleInfo().setTitle(title);
        } else {
            mLightningView.getTitleInfo().setTitle(mActivity.getString(R.string.untitled));
        }
        mXUIControllerMini.tabChanged(mLightningView);
        if (view != null && view.getUrl() != null) {
            mXUIControllerMini.updateHistory(title, view.getUrl());
        }
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(@NonNull final String origin,
                                                   @NonNull final GeolocationPermissions.Callback callback) {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, PERMISSIONS, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                final boolean remember = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(mActivity.getString(R.string.location));
                String org;
                if (origin.length() > 50) {
                    org = origin.subSequence(0, 50) + "...";
                } else {
                    org = origin;
                }
                builder.setMessage(org + mActivity.getString(R.string.message_location))
                    .setCancelable(true)
                    .setPositiveButton(mActivity.getString(R.string.action_allow),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                callback.invoke(origin, true, remember);
                            }
                        })
                    .setNegativeButton(mActivity.getString(R.string.action_dont_allow),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                callback.invoke(origin, false, remember);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                BrowserDialog.setDialogSize(mActivity, alert);
            }

            @Override
            public void onDenied(String permission) {
                //TODO show message and/or turn off setting
            }
        });
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture,
                                  Message resultMsg) {
        mXUIControllerMini.onCreateWindow(resultMsg);
        return true;
    }

    @Override
    public void onCloseWindow(WebView window) {
        mXUIControllerMini.onCloseWindow(mLightningView);
    }

    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        mXUIControllerMini.openFileChooser(uploadMsg);
    }

    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        mXUIControllerMini.openFileChooser(uploadMsg);
    }

    @SuppressWarnings("unused")
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        mXUIControllerMini.openFileChooser(uploadMsg);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     WebChromeClient.FileChooserParams fileChooserParams) {
        mXUIControllerMini.showFileChooser(filePathCallback);
        return true;
    }

    /**
     * Obtain an image that is displayed as a placeholder on a video until the video has initialized
     * and can begin loading.
     *
     * @return a Bitmap that can be used as a place holder for videos.
     */
    @Nullable
    @Override
    public Bitmap getDefaultVideoPoster() {
        final Resources resources = mActivity.getResources();
        return BitmapFactory.decodeResource(resources, android.R.drawable.spinner_background);
    }

    /**
     * Inflate a view to send to a LightningView when it needs to display a video and has to
     * show a loading dialog. Inflates a progress view and returns it.
     *
     * @return A view that should be used to display the state
     * of a video's loading progress.
     */
    @Override
    public View getVideoLoadingProgressView() {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        return inflater.inflate(R.layout.video_loading_progress, null);
    }

    @Override
    public void onHideCustomView() {
        mXUIControllerMini.onHideCustomView();
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        mXUIControllerMini.onShowCustomView(view, callback);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onShowCustomView(View view, int requestedOrientation,
                                 CustomViewCallback callback) {
        mXUIControllerMini.onShowCustomView(view, callback, requestedOrientation);
    }
}
