package caoyuanxiao.mynews.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.oubowu.slideback.SlideBackHelper;
import com.oubowu.slideback.SlideConfig;
import com.oubowu.slideback.widget.SlideBackLayout;

import caoyuanxiao.mynews.R;
import caoyuanxiao.mynews.annotation.ActivityFragmentInject;
import caoyuanxiao.mynews.app.App;
import caoyuanxiao.mynews.app.AppManager;
import caoyuanxiao.mynews.mvp.news.ui.NewsActivity;
import caoyuanxiao.mynews.utils.GlideCircleTransform;
import caoyuanxiao.mynews.utils.GlideUtils;
import caoyuanxiao.mynews.utils.MeasureUtil;
import caoyuanxiao.mynews.utils.RxBus;
import caoyuanxiao.mynews.utils.SpUtil;
import caoyuanxiao.mynews.utils.ThemeUtil;
import caoyuanxiao.mynews.utils.ViewUtil;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Smile on 2017/4/24.
 */

public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements BaseView {

    //代理的基类
    T mPresenter;

    /**
     * 标示该activity是否可滑动退出,默认false
     */
    protected boolean mEnableSlidr;
    /**
     * 布局的id
     */
    protected int mContentViewId;

    /**
     * 是否存在NavigationView
     */
    protected boolean mHasNavigationView;

    /**
     * 滑动布局
     */
    protected DrawerLayout mDrawerLayout;

    /**
     * 侧滑导航布局
     */
    protected NavigationView mNavigationView;

    /**
     * 菜单的id
     */
    private int mMenuId;

    /**
     * Toolbar标题
     */
    private int mToolbarTitle;

    /**
     * 默认选中的菜单项
     */
    private int mMenuDefaultCheckedItem;

    /**
     * Toolbar左侧按钮的样式
     */
    private int mToolbarIndicator;

    /**
     * 控制滑动与否的接口
     */
    //    protected SlidrInterface mSlidrInterface;
    protected SlideBackLayout mSlideBackLayout;

    /**
     * 跳转的类
     */
    private Class mClass;

    /**
     * 结束Activity的可观测对象
     */
    private Observable<Boolean> mFinishObservable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //这里使用注解中的参数 赋值到继承activity的值
        if (getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
            ActivityFragmentInject annotation = getClass().getAnnotation(ActivityFragmentInject.class);
            mContentViewId = annotation.contentViewId();
            mEnableSlidr = annotation.enableSlidr();
            mHasNavigationView = annotation.hasNavigationView();
            mMenuId = annotation.menuId();
            mToolbarTitle = annotation.toolbarTitle();
            mToolbarIndicator = annotation.toolbarIndicator();
            mMenuDefaultCheckedItem = annotation.menuDefaultCheckedItem();
        } else {
            new RuntimeException("Class must add annotations of ActivityFragmentInit.class");
        }

        initTheme();
        setContentView(mContentViewId);

        if (mEnableSlidr && SpUtil.readBoolean("disableSlide")) {
            // 默认开启侧滑，默认是整个页码侧滑
            mSlideBackLayout = SlideBackHelper.attach(this, App.getActivityHelper(), new SlideConfig.Builder()
                    // 是否侧滑
                    .edgeOnly(SpUtil.readBoolean("enableSlideEdge"))
                    // 是否会屏幕旋转
                    .rotateScreen(false)
                    // 是否禁止侧滑
                    .lock(false)
                    // 侧滑的响应阈值，0~1，对应屏幕宽度*percent
                    .edgePercent(0.1f)
                    // 关闭页面的阈值，0~1，对应屏幕宽度*percent
                    .slideOutPercent(0.35f).create(), null);

        }

        if (mHasNavigationView) {
            initNavigationView();
            initFinishRxBus();
        }

        initToolbar();

        initView();

    }

    protected abstract void initView();
    private void initToolbar() {

        // 针对父布局非DrawerLayout的状态栏处理方式
        // 设置toolbar上面的View实现类状态栏效果，这里是因为状态栏设置为透明的了，而默认背景是白色的，不设的话状态栏处就是白色
//        final View statusView = findViewById(R.id.status_view);
//        if (statusView != null) {
//            statusView.getLayoutParams().height = MeasureUtil.getStatusBarHeight(this);
//        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            // 24.0.0版本后导航图标会有默认的与标题的距离，这里设置去掉
            toolbar.setContentInsetStartWithNavigation(0);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            if (mToolbarTitle != -1) {
                setToolbarTitle(mToolbarTitle);
            }
            //设置左侧按钮的图片
            if (mToolbarIndicator != -1) {
                setToolbarIndicator(mToolbarIndicator);
            } else {
                setToolbarIndicator(R.drawable.ic_menu_back);
            }
        }
    }

    protected void setToolbarIndicator(int resId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(resId);
        }
    }

    protected void setToolbarTitle(String str) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(str);
        }
    }

    protected void setToolbarTitle(int strId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(strId);
        }
    }

    /**
     * 订阅结束自己的事件，这里使用来结束导航的Activity
     */
    private void initFinishRxBus() {
        mFinishObservable = RxBus.get().register("finish", Boolean.class);
        mFinishObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean themeChange) {
                try {
                    if (themeChange && !AppManager.getAppManager().getCurrentNavActivity().getName().equals(BaseActivity.this.getClass().getName())) {
                        //  切换皮肤的做法是设置页面通过鸿洋大大的不重启换肤，其他后台导航页面的统统干掉，跳转回去的时候，
                        //  因为用了FLAG_ACTIVITY_REORDER_TO_FRONT，发现栈中无之前的activity存在了，就重启设置了主题，
                        // 这样一来就不会所有Activity都做无重启刷新控件更该主题造成的卡顿现象
                        finish();
                    } else if (!themeChange) {
                        // 这个是入口新闻页面退出时发起的通知所有导航页面退出的事件
                        finish();
                        AppManager.getAppManager().clear();

                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();

                }
            }
        });
    }

    /**
     * 初始化NavigationView
     */
    private void initNavigationView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        handleStatusView();

        handleAppBarLayoutOffset();

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        if (mMenuDefaultCheckedItem != -1 && mNavigationView != null) {
            mNavigationView.setCheckedItem(mMenuDefaultCheckedItem);
        }
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.isChecked()) {
                    return true;
                }
                switch (item.getItemId()) {
                    case R.id.action_news:
                        mClass = NewsActivity.class;
                        break;
                    case R.id.action_video:
                        // mClass = VideoActivity.class;
                        toast("电影");
                        break;
                    case R.id.action_photo:
                        //mClass = PhotoActivity.class;
                        toast("图片");
                        break;
                    case R.id.action_settings:
                        //mClass = SettingsActivity.class;
                        toast("设置");
                        break;
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        });
        mNavigationView.post(new Runnable() {
            @Override
            public void run() {
                final ImageView imageView = (ImageView) BaseActivity.this.findViewById(R.id.avatar);
                if (imageView != null) {
                    GlideUtils.loadDefaultTransformation(R.drawable.ic_header, imageView, false, null, new GlideCircleTransform(imageView.getContext()), null);
                    //                    Glide.with(mNavigationView.getContext()).load(R.drawable.ic_header).animate(R.anim.image_load).transform(new GlideCircleTransform(mNavigationView.getContext()))
                    //                            .into(imageView);
                }
            }
        });
        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerClosed(View drawerView) {
                if (mClass != null) {
                    showActivityReorderToFront(BaseActivity.this, mClass, false);
                    mClass = null;
                }
            }
        });
    }

    /**
     * 从NagativeView跳转到选择的界面中
     *
     * @param aty
     * @param cls
     * @param backPress
     */
    public void showActivityReorderToFront(Activity aty, Class<?> cls, boolean backPress) {

        App.getActivityHelper().addActivity(cls);

        AppManager.getAppManager().orderNavActivity(cls.getName(), backPress);

        Intent intent = new Intent();
        intent.setClass(aty, cls);
        // 此标志用于启动一个Activity的时候，若栈中存在此Activity实例，则把它调到栈顶。不创建多一个
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        aty.startActivity(intent);
        overridePendingTransition(0, 0);
    }


    /**
     * 处理布局延伸到状态栏，对4.4以上系统有效
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void handleStatusView() {
        // 针对4.4和SettingsActivity(因为要做换肤，而状态栏在5.0是设置为透明的，若不这样处理换肤时状态栏颜色不会变化)
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {

            // 生成一个状态栏大小的矩形
            View statusBarView = ViewUtil.createStatusView(this, ThemeUtil.getColor(this, R.attr.colorPrimary));
            // 添加 statusBarView 到布局中
            ViewGroup contentLayout = (ViewGroup) mDrawerLayout.getChildAt(0);
            contentLayout.addView(statusBarView, 0);
            // 内容布局不是 LinearLayout 时,设置margin或者padding top
            final View view = contentLayout.getChildAt(1);
            final int statusBarHeight = MeasureUtil.getStatusBarHeight(this);
            if (!(contentLayout instanceof LinearLayout) && view != null) {
                view.setPadding(0, statusBarHeight, 0, 0);
            }
            // 设置属性
            ViewGroup drawer = (ViewGroup) mDrawerLayout.getChildAt(1);
            mDrawerLayout.setFitsSystemWindows(false);
            contentLayout.setFitsSystemWindows(false);
            contentLayout.setClipToPadding(true);
            drawer.setFitsSystemWindows(false);

//            if (this instanceof SettingsActivity) {
//                // 因为要SettingsActivity做换肤，所以statusBarView也要设置
//                statusBarView.setTag("skin:primary:background");
//                view.setTag("skin:primary:background");
//            }

        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            // 5.0以上跟4.4统一，状态栏颜色和Toolbar的一致
            mDrawerLayout.setStatusBarBackgroundColor(ThemeUtil.getColor(this, R.attr.colorPrimary));
        }
    }

    /**
     * 处理与AppBarLayout偏移量相关事件处理
     */
    private void handleAppBarLayoutOffset() {
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (appBarLayout != null && toolbar != null) {
            final int toolbarHeight = MeasureUtil.getToolbarHeight(this);
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                        // appBarLayout偏移量为Toolbar高度，这里为了4.4往上滑的时候由于透明状态栏Toolbar遮不住看起来难看，
                        // 根据偏移量设置透明度制造出往上滑动逐渐消失的效果
                        toolbar.setAlpha((toolbarHeight + verticalOffset) * 1.0f / toolbarHeight);
                    }
                    // AppBarLayout没有偏移量时，告诉Fragment刷新控件可用
                    RxBus.get().post("enableRefreshLayoutOrScrollRecyclerView", verticalOffset == 0);
                }
            });
        }
    }

    private void initTheme() {
//        if (this instanceof NewsActivity) {
//            setTheme(SpUtil.readBoolean("enableNightMode") ? R.style.BaseAppThemeNight_LauncherAppTheme : R.style.BaseAppTheme_LauncherAppTheme);
//        } else if (!mEnableSlidr && mHasNavigationView) {
//            setTheme(SpUtil.readBoolean("enableNightMode") ? R.style.BaseAppThemeNight_AppTheme : R.style.BaseAppTheme_AppTheme);
//        } else {
//            setTheme(SpUtil.readBoolean("enableNightMode") ? R.style.BaseAppThemeNight_SlidrTheme : R.style.BaseAppTheme_SlidrTheme);
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(mMenuId, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerLayout != null && item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else if (item.getItemId() == android.R.id.home && mToolbarIndicator == -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            } else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void toast(String msg) {
        showSnackbar(msg);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    protected void showSnackbar(String msg) {
        Snackbar.make(getWindow().getDecorView(), msg, Snackbar.LENGTH_SHORT).show();
    }
}
