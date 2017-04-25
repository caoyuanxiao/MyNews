package caoyuanxiao.mynews.app;

import android.app.Application;
import android.content.Context;

import com.oubowu.slideback.ActivityHelper;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by Smile on 2017/4/24.
 */

public class App extends Application {
    private RefWatcher mRefWatcher;
    private static Context sApplicationContext;

    ActivityHelper mActivityHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        //如果检测到某个 activity 有内存泄露，LeakCanary 就是自动地显示一个通知
        mRefWatcher = LeakCanary.install(this);
        sApplicationContext = this;
        mActivityHelper = new ActivityHelper();
        registerActivityLifecycleCallbacks(mActivityHelper);
    }


    // 获取ApplicationContext
    public static Context getContext() {
        return sApplicationContext;
    }

    public static ActivityHelper getActivityHelper() {
        return ((App) sApplicationContext).mActivityHelper;
    }

    /**
     * 获取内存监控
     *
     * @param context
     * @return
     */
    public static RefWatcher getRefWatcher(Context context) {
        App application = (App) context.getApplicationContext();
        return application.mRefWatcher;
    }

}
