package com.victormugo.nsign_media.utils;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.victormugo.nsign_media.R;

public class Dialogs {

    public static void showMaterialDialog(String message, String title, MaterialDialog.SingleButtonCallback onclickListener, boolean showCancelButton, Context context) {
        try {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
            builder.title(title);
            builder.content(message);
            builder.canceledOnTouchOutside(false);
            builder.positiveText(android.R.string.yes);

            if (showCancelButton) {
                builder.negativeText(android.R.string.no);
                builder.onNegative((dialog, which) -> dialog.dismiss());
            }

            if (onclickListener != null) {
                builder.onPositive(onclickListener);
            }

            builder.icon(ContextCompat.getDrawable(context, R.drawable.ic_dialog));

            builder.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static MaterialDialog.Builder showDialogProgress(String message, Context context) {
        try {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
            builder.cancelable(false);
            builder.content(message);
            builder.progress(true, 0);
            builder.progressIndeterminateStyle(true);
            builder.autoDismiss(true);

            return builder;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
