package caoyuanxiao.mynews.mvp.news.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import caoyuanxiao.mynews.R;
import caoyuanxiao.mynews.annotation.ActivityFragmentInject;
import caoyuanxiao.mynews.base.BaseActivity;
import caoyuanxiao.mynews.base.BaseFragment;
import caoyuanxiao.mynews.base.BaseFragmentAdapter;
import caoyuanxiao.mynews.greendao.NewsChannelTable;
import caoyuanxiao.mynews.mvp.news.presenter.INewsPresenter;
import caoyuanxiao.mynews.mvp.news.presenter.INewsPresenterImp;
import caoyuanxiao.mynews.mvp.news.view.INewsView;
import caoyuanxiao.mynews.utils.RxBus;
import caoyuanxiao.mynews.utils.ViewUtil;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Smile on 2017/4/24.
 */

@ActivityFragmentInject(
        contentViewId = R.layout.activity_news,
        menuId = R.menu.menu_news,
        hasNavigationView = true,
        toolbarTitle = R.string.news,
        toolbarIndicator = R.drawable.ic_list_white,
        menuDefaultCheckedItem = R.id.action_news
)
public class NewsActivity extends BaseActivity<INewsPresenter> implements INewsView, View.OnClickListener {
    INewsPresenterImp mINewsPresenterImp;
    Observable<Boolean> mChannelObservable;

    View view1, view2, view3, view4, view5, view6, view7, view8;
    List<View> views;
    List<String> titles;


    @Override
    protected void initView() {
        mINewsPresenterImp = new INewsPresenterImp(this);

    }

    @Override
    public void initRxBusEvent() {
        //这里需要注册 频道列表变化的数据
        mChannelObservable = RxBus.get().register("channelChange", Boolean.class);
        mChannelObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean channelChanged) {
                toast("频道数据变化了");

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChannelObservable != null) {
            RxBus.get().unregister("channelChange", mChannelObservable);
        }
    }

    List<String> title;

    /**
     * 初始化Viewpager
     */
    @Override
    public void initViewpager(List<NewsChannelTable> newsChannels) {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        List<BaseFragment> fragments = new ArrayList<>();
        title = new ArrayList<>();


        if (newsChannels != null) {
            // 有除了固定的其他频道被选中，添加
            for (NewsChannelTable news : newsChannels) {
//                final NewsListFragment fragment = NewsListFragment
//                        .newInstance(news.getNewsChannelId(), news.getNewsChannelType(),
//                                news.getNewsChannelIndex());
                MyFragment myFragment = new MyFragment();
                fragments.add(myFragment);
                title.add(news.getNewsChannelName());
            }

            if (viewPager.getAdapter() == null) {
                // 初始化ViewPager
                /*BaseFragmentAdapter adapter = new BaseFragmentAdapter(getSupportFragmentManager(),
                        fragments, title);*/
                // MyPagerAdapter adapter = new MyPagerAdapter(fragments);
                BaseFragmentAdapter adapter = new BaseFragmentAdapter(getSupportFragmentManager(), fragments, title);
                viewPager.setAdapter(adapter);
            }
//            else {
//                final BaseFragmentAdapter adapter = (BaseFragmentAdapter) viewPager.getAdapter();
//                adapter.updateFragments(fragments, title);
//            }
            viewPager.setCurrentItem(0, false);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setScrollPosition(0, 0, true);
            // 根据Tab的长度动态设置TabLayout的模式
            ViewUtil.dynamicSetTabLayoutMode(tabLayout);

            //setOnTabSelectEvent(viewPager, tabLayout);

        } else {
            toast("数据异常");
        }
    }


    @Override
    public void showSuccess() {

        toast("登陆成功");
    }

    @Override
    public void showFailed() {
        toast("登陆失败");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_channel_manage:
                toast("选择频道");
                //mPresenter.operateChannelDb();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
