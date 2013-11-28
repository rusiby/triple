package com.urd.triple;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class BaseActivity extends Activity {
    private static Toast sToast = null;
    private static SharedPreferences sPrefs = null;

    protected void startActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    protected void showToast(String format, Object... args) {
        String msg = String.format(Locale.US, format, args);
        if (sToast == null) {
            sToast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        } else {
            sToast.setText(msg);
        }
        sToast.show();
    }

    protected void exit() {
        finish();

        getApplication().onTerminate();
    }

    protected SharedPreferences getDefaultSharedPreferences() {
        if (sPrefs == null) {
            sPrefs = getSharedPreferences("settings", MODE_PRIVATE);
        }

        return sPrefs;
    }
}
