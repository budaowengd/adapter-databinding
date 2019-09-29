package me.lx.sample;

import android.app.Application;
import android.content.Context;

/**
 * author: luoXiong
 * e-mail: 382060748@qq.com
 * date: 2019/9/25 17:29
 * version: 1.0
 * desc:
 */
public class MyApp extends Application {
    public static Context sContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }
}
