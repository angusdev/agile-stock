package hk.reality.utils;

import android.app.Activity;
import android.util.Log;

public class ActivityHelper {
    public static final String TAG = "ActivityHelper";

    /**
     * Show a dialog from a activity quitely, ignore any exceptions thrown
     */
    public static void showDialogQuitely(Activity activity, int dialogId) {
        try {
            if (activity == null) {
                return;
            }

            activity.showDialog(dialogId);
        } catch (RuntimeException re) {
            Log.w(TAG, 
                    String.format("unexpected error occurred while attempt show dialog %d at activity", 
                    dialogId));
        }
    }
}
