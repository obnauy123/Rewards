package csc492.Bo_Y.rewards;

import androidx.appcompat.app.ActionBar;

public class Utilities {
    static void setupHomeIndicator(ActionBar actionBar) {
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
