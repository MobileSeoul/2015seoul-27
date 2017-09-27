package com.seoul.publicbooksearcher.presentation.view.component;


import android.view.Menu;
import android.view.MenuItem;

import com.seoul.publicbooksearcher.R;

public class ActionBarProgressBarView {

    private Menu optionsMenu;

    public ActionBarProgressBarView(Menu optionsMenu) {
        this.optionsMenu = optionsMenu;
    }

    public void setSortActionButtonState(final boolean refreshing) {
        if (optionsMenu != null) {
            final MenuItem sortItem = optionsMenu.findItem(R.id.action_sort);
            if (sortItem != null) {
                if (refreshing) {
                    sortItem.setActionView(R.layout.actionbar_indeterminate_progress);
                } else {
                    sortItem.setActionView(null);
                }
            }
        }
    }
}
