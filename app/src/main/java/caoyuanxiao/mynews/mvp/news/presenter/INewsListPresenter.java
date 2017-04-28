package caoyuanxiao.mynews.mvp.news.presenter;

import caoyuanxiao.mynews.base.BasePresenter;

/**
 * Created by Smile on 2017/4/27.
 */

public interface INewsListPresenter extends BasePresenter{
    //这里需要处理界面以及数据的展示

    void refreshDate();

    void loadMoreDate();
}
