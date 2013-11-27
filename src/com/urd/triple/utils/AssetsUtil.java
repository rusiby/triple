package com.urd.triple.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class AssetsUtil {

    public static Drawable ceateDrawableFromAssets(Context context, String filename) {
        Drawable drawable = null;

        AssetManager am = context.getAssets();
        InputStream is = null;
        try {
            is = am.open("picture/" + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (is != null) {
            // 获得Drawable
            drawable = Drawable.createFromStream(is, null);
            try {
                is.close();
            } catch (IOException e) {
            }
        }

        return drawable;
    }

    public static Bitmap createBitmapFromAssets(Context context, String filename) {
        Bitmap bitmap = null;

        AssetManager am = context.getAssets();
        InputStream is = null;
        try {
            is = am.open("picture/" + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (is != null) {
            // 获得Drawable
            bitmap = BitmapFactory.decodeStream(is);
            try {
                is.close();
            } catch (IOException e) {
            }
        }

        return bitmap;
    }
}
