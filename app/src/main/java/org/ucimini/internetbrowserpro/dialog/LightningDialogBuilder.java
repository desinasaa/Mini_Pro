package org.ucimini.internetbrowserpro.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.anthonycr.bonsai.CompletableOnSubscribe;
import com.anthonycr.bonsai.Schedulers;
import com.anthonycr.bonsai.SingleOnSubscribe;

import java.util.List;

import javax.inject.Inject;

import org.ucimini.internetbrowserpro.BrowserApp;
import org.ucimini.internetbrowserpro.R;
import org.ucimini.internetbrowserpro.database_minipro.HistoryItem_mini;
import org.ucimini.internetbrowserpro.download_minipro.DownloadHandler_mini;
import org.ucimini.internetbrowserpro.MainActivity;
import org.ucimini.internetbrowserpro.constant_minipro.BookmarkPage_mini;
import org.ucimini.internetbrowserpro.constant_minipro.Constants_mini;
import org.ucimini.internetbrowserpro.controller_minipro.UIController_mini;
import org.ucimini.internetbrowserpro.database_minipro.bookmark.BookmarkModel;
import org.ucimini.internetbrowserpro.database_minipro.downloads.DownloadsModel;
import org.ucimini.internetbrowserpro.database_minipro.history.HistoryModel;
import org.ucimini.internetbrowserpro.preference_minipro.PreferenceManager_mini;
import org.ucimini.internetbrowserpro.utils.IntentUtils;
import org.ucimini.internetbrowserpro.utils.Preconditions;
import org.ucimini.internetbrowserpro.utils.UrlUtils;

/**
 * TODO Rename this class it doesn't build dialogs only for bookmarks
 * <p/>
 * Created by Stefano Pacifici on 02/09/15, based on Anthony C. Restaino's code.
 */
public class LightningDialogBuilder {
    private static final String TAG = "LightningDialogBuilder";

    public enum NewTab {
        FOREGROUND,
        BACKGROUND,
        INCOGNITO
    }

    @Inject BookmarkModel mBookmarkManager;
    @Inject DownloadsModel mDownloadsModel;
    @Inject HistoryModel mHistoryModel;
    @Inject
    PreferenceManager_mini mPreferenceManagerMini;
    @Inject
    DownloadHandler_mini mDownloadHandlerMini;

    @Inject
    public LightningDialogBuilder() {
        BrowserApp.getAppComponent().inject(this);
    }

    /**
     * Show the appropriated dialog for the long pressed link. It means that we try to understand
     * if the link is relative to a bookmark or is just a folder.
     *
     * @param activity used to show the dialog
     * @param url      the long pressed url
     */
    public void showLongPressedDialogForBookmarkUrl(@NonNull final Activity activity,
                                                    @NonNull final UIController_mini uiControllerMini,
                                                    @NonNull final String url) {
        final HistoryItem_mini item;
        if (UrlUtils.isBookmarkUrl(url)) {
            // TODO hacky, make a better bookmark mechanism in the future
            final Uri uri = Uri.parse(url);
            final String filename = uri.getLastPathSegment();
            final String folderTitle = filename.substring(0, filename.length() - BookmarkPage_mini.FILENAME.length() - 1);
            item = new HistoryItem_mini();
            item.setIsFolder(true);
            item.setTitle(folderTitle);
            item.setImageId(R.drawable.ic_folder);
            item.setUrl(Constants_mini.FOLDER + folderTitle);
            showBookmarkFolderLongPressedDialog(activity, uiControllerMini, item);
        } else {
            mBookmarkManager.findBookmarkForUrl(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.main())
                .subscribe(new SingleOnSubscribe<HistoryItem_mini>() {
                    @Override
                    public void onItem(@Nullable HistoryItem_mini HistoryItem_mini) {
                        // TODO: 6/14/17 figure out solution to case where slashes get appended to root urls causing the item to be null
                        if (HistoryItem_mini != null) {
                            showLongPressedDialogForBookmarkUrl(activity, uiControllerMini, HistoryItem_mini);
                        }
                    }
                });
        }
    }

    public void showLongPressedDialogForBookmarkUrl(@NonNull final Activity activity,
                                                    @NonNull final UIController_mini uiControllerMini,
                                                    @NonNull final HistoryItem_mini item) {
        BrowserDialog.show(activity, R.string.action_bookmarks,
            new BrowserDialog.Item(R.string.dialog_open_new_tab) {
                @Override
                public void onClick() {
                    uiControllerMini.handleNewTab(NewTab.FOREGROUND, item.getUrl());
                }
            },
            new BrowserDialog.Item(R.string.dialog_open_background_tab) {
                @Override
                public void onClick() {
                    uiControllerMini.handleNewTab(NewTab.BACKGROUND, item.getUrl());
                }
            },
            new BrowserDialog.Item(R.string.dialog_open_incognito_tab, activity instanceof MainActivity) {
                @Override
                public void onClick() {
                    uiControllerMini.handleNewTab(NewTab.INCOGNITO, item.getUrl());
                }
            },
            new BrowserDialog.Item(R.string.action_share) {
                @Override
                public void onClick() {
                    new IntentUtils(activity).shareUrl(item.getUrl(), item.getTitle());
                }
            },
            new BrowserDialog.Item(R.string.dialog_copy_link) {
                @Override
                public void onClick() {
                    BrowserApp.copyToClipboard(activity, item.getUrl());
                }
            },
            new BrowserDialog.Item(R.string.dialog_remove_bookmark) {
                @Override
                public void onClick() {
                    mBookmarkManager.deleteBookmark(item)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.main())
                        .subscribe(new SingleOnSubscribe<Boolean>() {
                            @Override
                            public void onItem(@Nullable Boolean success) {
                                Preconditions.checkNonNull(success);
                                if (success) {
                                    uiControllerMini.handleBookmarkDeleted(item);
                                }
                            }
                        });
                }
            },
            new BrowserDialog.Item(R.string.dialog_edit_bookmark) {
                @Override
                public void onClick() {
                    showEditBookmarkDialog(activity, uiControllerMini, item);
                }
            });
    }

    /**
     * Show the appropriated dialog for the long pressed link.
     *
     * @param activity used to show the dialog
     * @param url      the long pressed url
     */
    public void showLongPressedDialogForDownloadUrl(@NonNull final Activity activity,
                                                    @NonNull final UIController_mini uiControllerMini,
                                                    @NonNull final String url) {

        BrowserDialog.show(activity, R.string.action_downloads,
            new BrowserDialog.Item(R.string.dialog_delete_all_downloads) {
                @Override
                public void onClick() {
                    mDownloadsModel.deleteAllDownloads()
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.main())
                        .subscribe(new CompletableOnSubscribe() {
                            @Override
                            public void onComplete() {
                                uiControllerMini.handleDownloadDeleted();
                            }
                        });
                }
            });
    }

    private void showEditBookmarkDialog(@NonNull final Activity activity,
                                        @NonNull final UIController_mini uiControllerMini,
                                        @NonNull final HistoryItem_mini item) {
        final AlertDialog.Builder editBookmarkDialog = new AlertDialog.Builder(activity);
        editBookmarkDialog.setTitle(R.string.title_edit_bookmark);
        final View dialogLayout = View.inflate(activity, R.layout.dialog_edit_bookmark, null);
        final EditText getTitle = dialogLayout.findViewById(R.id.bookmark_title);
        getTitle.setText(item.getTitle());
        final EditText getUrl = dialogLayout.findViewById(R.id.bookmark_url);
        getUrl.setText(item.getUrl());
        final AutoCompleteTextView getFolder =
            dialogLayout.findViewById(R.id.bookmark_folder);
        getFolder.setHint(R.string.folder);
        getFolder.setText(item.getFolder());

        mBookmarkManager.getFolderNames()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.main())
            .subscribe(new SingleOnSubscribe<List<String>>() {
                @Override
                public void onItem(@Nullable List<String> folders) {
                    Preconditions.checkNonNull(folders);
                    final ArrayAdapter<String> suggestionsAdapter = new ArrayAdapter<>(activity,
                        android.R.layout.simple_dropdown_item_1line, folders);
                    getFolder.setThreshold(1);
                    getFolder.setAdapter(suggestionsAdapter);
                    editBookmarkDialog.setView(dialogLayout);
                    editBookmarkDialog.setPositiveButton(activity.getString(R.string.action_ok),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HistoryItem_mini editedItem = new HistoryItem_mini();
                                editedItem.setTitle(getTitle.getText().toString());
                                editedItem.setUrl(getUrl.getText().toString());
                                editedItem.setUrl(getUrl.getText().toString());
                                editedItem.setFolder(getFolder.getText().toString());
                                mBookmarkManager.editBookmark(item, editedItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.main())
                                    .subscribe(new CompletableOnSubscribe() {
                                        @Override
                                        public void onComplete() {
                                            uiControllerMini.handleBookmarksChange();
                                        }
                                    });
                            }
                        });
                    Dialog dialog = editBookmarkDialog.show();
                    BrowserDialog.setDialogSize(activity, dialog);
                }
            });
    }

    public void showBookmarkFolderLongPressedDialog(@NonNull final Activity activity,
                                                    @NonNull final UIController_mini uiControllerMini,
                                                    @NonNull final HistoryItem_mini item) {

        BrowserDialog.show(activity, R.string.action_folder,
            new BrowserDialog.Item(R.string.dialog_rename_folder) {
                @Override
                public void onClick() {
                    showRenameFolderDialog(activity, uiControllerMini, item);
                }
            },
            new BrowserDialog.Item(R.string.dialog_remove_folder) {
                @Override
                public void onClick() {
                    mBookmarkManager.deleteFolder(item.getTitle())
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.main())
                        .subscribe(new CompletableOnSubscribe() {
                            @Override
                            public void onComplete() {
                                uiControllerMini.handleBookmarkDeleted(item);
                            }
                        });
                }
            });
    }

    private void showRenameFolderDialog(@NonNull final Activity activity,
                                        @NonNull final UIController_mini uiControllerMini,
                                        @NonNull final HistoryItem_mini item) {
        BrowserDialog.showEditText(activity, R.string.title_rename_folder,
            R.string.hint_title, item.getTitle(),
            R.string.action_ok, new BrowserDialog.EditorListener() {
                @Override
                public void onClick(@NonNull String text) {
                    if (!TextUtils.isEmpty(text)) {
                        final String oldTitle = item.getTitle();
                        final HistoryItem_mini editedItem = new HistoryItem_mini();
                        editedItem.setTitle(text);
                        editedItem.setUrl(Constants_mini.FOLDER + text);
                        editedItem.setFolder(item.getFolder());
                        editedItem.setIsFolder(true);
                        mBookmarkManager.renameFolder(oldTitle, text)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.main())
                            .subscribe(new CompletableOnSubscribe() {
                                @Override
                                public void onComplete() {
                                    uiControllerMini.handleBookmarksChange();
                                }
                            });
                    }
                }
            });
    }

    public void showLongPressedHistoryLinkDialog(@NonNull final Activity activity,
                                                 @NonNull final UIController_mini uiControllerMini,
                                                 @NonNull final String url) {
        BrowserDialog.show(activity, R.string.action_history,
            new BrowserDialog.Item(R.string.dialog_open_new_tab) {
                @Override
                public void onClick() {
                    uiControllerMini.handleNewTab(NewTab.FOREGROUND, url);
                }
            },
            new BrowserDialog.Item(R.string.dialog_open_background_tab) {
                @Override
                public void onClick() {
                    uiControllerMini.handleNewTab(NewTab.BACKGROUND, url);
                }
            },
            new BrowserDialog.Item(R.string.dialog_open_incognito_tab, activity instanceof MainActivity) {
                @Override
                public void onClick() {
                    uiControllerMini.handleNewTab(NewTab.INCOGNITO, url);
                }
            },
            new BrowserDialog.Item(R.string.action_share) {
                @Override
                public void onClick() {
                    new IntentUtils(activity).shareUrl(url, null);
                }
            },
            new BrowserDialog.Item(R.string.dialog_copy_link) {
                @Override
                public void onClick() {
                    BrowserApp.copyToClipboard(activity, url);
                }
            },
            new BrowserDialog.Item(R.string.dialog_remove_from_history) {
                @Override
                public void onClick() {
                    mHistoryModel.deleteHistoryItem(url)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.main())
                        .subscribe(new CompletableOnSubscribe() {
                            @Override
                            public void onComplete() {
                                uiControllerMini.handleHistoryChange();
                            }
                        });
                }
            });
    }

    // TODO There should be a way in which we do not need an activity reference to dowload a file
    public void showLongPressImageDialog(@NonNull final Activity activity,
                                         @NonNull final UIController_mini uiControllerMini,
                                         @NonNull final String url,
                                         @NonNull final String userAgent) {
        BrowserDialog.show(activity, url.replace(Constants_mini.HTTP, ""),
            new BrowserDialog.Item(R.string.dialog_open_new_tab) {
                @Override
                public void onClick() {
                    uiControllerMini.handleNewTab(NewTab.FOREGROUND, url);
                }
            },
            new BrowserDialog.Item(R.string.dialog_open_background_tab) {
                @Override
                public void onClick() {
                    uiControllerMini.handleNewTab(NewTab.BACKGROUND, url);
                }
            },
            new BrowserDialog.Item(R.string.dialog_open_incognito_tab, activity instanceof MainActivity) {
                @Override
                public void onClick() {
                    uiControllerMini.handleNewTab(NewTab.INCOGNITO, url);
                }
            },
            new BrowserDialog.Item(R.string.action_share) {
                @Override
                public void onClick() {
                    new IntentUtils(activity).shareUrl(url, null);
                }
            },
            new BrowserDialog.Item(R.string.dialog_copy_link) {
                @Override
                public void onClick() {
                    BrowserApp.copyToClipboard(activity, url);
                }
            },
            new BrowserDialog.Item(R.string.dialog_download_image) {
                @Override
                public void onClick() {
                    mDownloadHandlerMini.onDownloadStart(activity, mPreferenceManagerMini, url, userAgent, "attachment", null, "");
                }
            });
    }

    public void showLongPressLinkDialog(@NonNull final Activity activity,
                                        @NonNull final UIController_mini uiControllerMini,
                                        @NonNull final String url) {
        BrowserDialog.show(activity, url,
            new BrowserDialog.Item(R.string.dialog_open_new_tab) {
                @Override
                public void onClick() {
                    uiControllerMini.handleNewTab(NewTab.FOREGROUND, url);
                }
            },
            new BrowserDialog.Item(R.string.dialog_open_background_tab) {
                @Override
                public void onClick() {
                    uiControllerMini.handleNewTab(NewTab.BACKGROUND, url);
                }
            },
            new BrowserDialog.Item(R.string.dialog_open_incognito_tab, activity instanceof MainActivity) {
                @Override
                public void onClick() {
                    uiControllerMini.handleNewTab(NewTab.INCOGNITO, url);
                }
            },
            new BrowserDialog.Item(R.string.action_share) {
                @Override
                public void onClick() {
                    new IntentUtils(activity).shareUrl(url, null);
                }
            },
            new BrowserDialog.Item(R.string.dialog_copy_link) {
                @Override
                public void onClick() {
                    BrowserApp.copyToClipboard(activity, url);
                }
            });
    }

}
