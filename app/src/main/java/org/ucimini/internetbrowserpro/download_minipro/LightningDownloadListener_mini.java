/*
 * Copyright 2014 A.C.R. Development
 */
package org.ucimini.internetbrowserpro.download_minipro;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;

import org.ucimini.internetbrowserpro.R;
import org.ucimini.internetbrowserpro.BrowserApp;
import org.ucimini.internetbrowserpro.database_minipro.downloads.DownloadsModel;
import org.ucimini.internetbrowserpro.dialog.BrowserDialog;
import org.ucimini.internetbrowserpro.preference_minipro.PreferenceManager_mini;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;

import javax.inject.Inject;

public class LightningDownloadListener_mini implements DownloadListener {

    private static final String TAG = "LightningDownloader";

    private final Activity mActivity;

    @Inject
    PreferenceManager_mini mPreferenceManagerMini;
    @Inject
    DownloadHandler_mini mDownloadHandlerMini;
    @Inject DownloadsModel downloadsModel;

    public LightningDownloadListener_mini(Activity context) {
        BrowserApp.getAppComponent().inject(this);
        mActivity = context;
    }

    @Override
    public void onDownloadStart(final String url, final String userAgent,
                                final String contentDisposition, final String mimetype, final long contentLength) {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity,
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
            new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    final String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
                    final String downloadSize;

                    if (contentLength > 0) {
                        downloadSize = Formatter.formatFileSize(mActivity, contentLength);
                    } else {
                        downloadSize = mActivity.getString(R.string.unknown_size);
                    }

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    mDownloadHandlerMini.onDownloadStart(mActivity, mPreferenceManagerMini, url, userAgent, contentDisposition, mimetype, downloadSize);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity); // dialog
                    String message = mActivity.getString(R.string.dialog_download, downloadSize);
                    Dialog dialog = builder.setTitle(fileName)
                        .setMessage(message)
                        .setPositiveButton(mActivity.getResources().getString(R.string.action_download),
                            dialogClickListener)
                        .setNegativeButton(mActivity.getResources().getString(R.string.action_cancel),
                            dialogClickListener).show();
                    BrowserDialog.setDialogSize(mActivity, dialog);
                    Log.i(TAG, "Downloading: " + fileName);
                }

                @Override
                public void onDenied(String permission) {
                    //TODO show message
                }
            });
    }
}
