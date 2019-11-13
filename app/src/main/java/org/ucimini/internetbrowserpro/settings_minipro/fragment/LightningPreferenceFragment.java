package org.ucimini.internetbrowserpro.settings_minipro.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import javax.inject.Inject;

import org.ucimini.internetbrowserpro.BrowserApp;
import org.ucimini.internetbrowserpro.preference_minipro.PreferenceManager_mini;

/**
 * Simplify {@link PreferenceManager_mini} inject in all the PreferenceFragments
 *
 * @author Stefano Pacifici
 * @date 2015/09/16
 */
public class LightningPreferenceFragment extends PreferenceFragment {

    @Inject
    PreferenceManager_mini mPreferenceManagerMini;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BrowserApp.getAppComponent().inject(this);
    }
}
