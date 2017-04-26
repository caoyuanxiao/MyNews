package caoyuanxiao.mynews.mvp.news.presenter;

import caoyuanxiao.mynews.base.BasePresenter;

/**
 * Created by Smile on 2017/4/24.
 */

public interface INewsPresenter extends BasePresenter {
    /**
     * 频道排序或增删变化后调用此方法更新数据库
     */
    void operateChannelDb();
}
