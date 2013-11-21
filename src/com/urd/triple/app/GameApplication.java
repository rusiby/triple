package com.urd.triple.app;

import android.app.Application;
import android.os.Process;

import com.urd.triple.core.GameCore;

public class GameApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        GameCore.init();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        GameCore.fini();

        Process.killProcess(android.os.Process.myPid());
    }
}
