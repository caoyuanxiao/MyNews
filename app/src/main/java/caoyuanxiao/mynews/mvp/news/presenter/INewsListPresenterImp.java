package caoyuanxiao.mynews.mvp.news.presenter;

import java.util.List;

import caoyuanxiao.mynews.base.BasePresenterImpl;
import caoyuanxiao.mynews.bean.NeteastNewsSummary;
import caoyuanxiao.mynews.constant.DataLoadType;
import caoyuanxiao.mynews.mvp.news.model.INewsListInteractor;
import caoyuanxiao.mynews.mvp.news.model.INewsListInteractorImp;
import caoyuanxiao.mynews.mvp.news.view.INewsListView;

/**
 * Created by Smile on 2017/4/27.
 */

public class INewsListPresenterImp extends BasePresenterImpl<INewsListView, List<NeteastNewsSummary>> implements INewsListPresenter {
    private int mStartPage;
    private String newsid;
    private String newtype;
    INewsListInteractor<List<NeteastNewsSummary>> mListINewsListInteractor;

    Boolean mIsRefresh = true;

    public INewsListPresenterImp(INewsListView view, String newsId, String newType) {
        super(view);
        this.newsid = newsId;
        this.newtype = newType;
        mListINewsListInteractor = new INewsListInteractorImp();
        mSubscription = mListINewsListInteractor.getNewsListDate(this, newType, newsId, mStartPage);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void refreshDate() {
        mStartPage = 0;
        mIsRefresh = true;
        mSubscription = mListINewsListInteractor.getNewsListDate(this, newtype, newsid, mStartPage);
    }

    @Override
    public void loadMoreDate() {
        mStartPage = +20;
        mIsRefresh = false;
        mSubscription = mListINewsListInteractor.getNewsListDate(this, newtype, newsid, mStartPage);
    }

    @Override
    public void requestSuccess(List<NeteastNewsSummary> data) {
        super.requestSuccess(data);

        if (data != null) {
            mStartPage = +20;
        }
        mView.updateNewsList(data, "", mIsRefresh ? DataLoadType.TYPE_REFRESH_SUCCESS : DataLoadType.TYPE_LOAD_MORE_SUCCESS);
    }

    @Override
    public void requestError(String msg) {
        super.requestError(msg);
        mView.updateNewsList(null, msg, mIsRefresh ? DataLoadType.TYPE_REFRESH_SUCCESS : DataLoadType.TYPE_LOAD_MORE_SUCCESS);
    }

    @Override
    public void beforeRequest() {
        super.beforeRequest();
        mView.showProgress();

    }
}
