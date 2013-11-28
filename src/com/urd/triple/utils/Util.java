package com.urd.triple.utils;

import android.content.Context;
import android.widget.Toast;

public class Util {
    private static Toast sToast = null;

    public static void showToast(Context context, CharSequence text, int duration) {
        if (sToast == null) {
            sToast = Toast.makeText(context, text, duration);
        } else {
            sToast.setText(text);
        }

        sToast.show();
    }
}
