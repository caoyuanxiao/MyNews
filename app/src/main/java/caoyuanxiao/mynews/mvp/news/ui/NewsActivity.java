package caoyuanxiao.mynews.mvp.news.ui;

import android.view.MenuItem;

import caoyuanxiao.mynews.R;
import caoyuanxiao.mynews.annotation.ActivityFragmentInject;
import caoyuanxiao.mynews.base.BaseActivity;
import caoyuanxiao.mynews.mvp.news.presenter.INewsPresenter;
import caoyuanxiao.mynews.mvp.news.view.INewsView;

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
public class NewsActivity extends BaseActivity<INewsPresenter> implements INewsView {



    @Override
    protected void initView() {


    }

    @Override
    public void initRxBusEvent() {

    }

    @Override
    public void initViewpager() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_channel_manage:
                toast("选择频道");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
