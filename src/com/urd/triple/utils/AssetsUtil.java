package com.urd.triple.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;

public class AssetsUtil {
	
	public static Drawable ceateDrawableFromAssets(Context context, String fileName) {
		if (context == null || fileName == null) {
			return null;
		}
    	Drawable da = null;
    	AssetManager asm = context.getAssets();
    	InputStream is = null;
		try {
			is = asm.open("picture/" + fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (is != null) {
			//获得Drawable 
	        da = Drawable.createFromStream(is, null); 
		}
		return da;
    }
}
