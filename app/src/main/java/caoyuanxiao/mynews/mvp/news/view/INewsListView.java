package caoyuanxiao.mynews.mvp.news.view;

import android.support.annotation.NonNull;

import java.util.List;

import caoyuanxiao.mynews.base.BaseView;
import caoyuanxiao.mynews.bean.NeteastNewsSummary;
import caoyuanxiao.mynews.constant.DataLoadType;

/**
 * Created by Smile on 2017/4/27.
 */

public interface INewsListView extends BaseView {
  void updateNewsList(List<NeteastNewsSummary> data, @NonNull String errorMsg, @DataLoadType.DataLoadTypeChecker int type);
}
