package caoyuanxiao.mynews.mvp.news.view;

import java.util.List;

import caoyuanxiao.mynews.base.BaseView;
import caoyuanxiao.mynews.greendao.NewsChannelTable;

/**
 * Created by Smile on 2017/4/24.
 */

public interface INewsView extends BaseView {
    void initRxBusEvent();
    void initViewpager(List<NewsChannelTable> newsChannels);
    void showSuccess();
    void showFailed();
}
