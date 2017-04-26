package caoyuanxiao.mynews.mvp.news.presenter;

import java.util.List;

import caoyuanxiao.mynews.base.BasePresenterImpl;
import caoyuanxiao.mynews.greendao.NewsChannelTable;
import caoyuanxiao.mynews.mvp.news.model.INewsInteractorImp;
import caoyuanxiao.mynews.mvp.news.view.INewsView;

/**
 * Created by Smile on 2017/4/24.
 */

public class INewsPresenterImp extends BasePresenterImpl<INewsView, List<NewsChannelTable>> implements INewsPresenter {

    INewsInteractorImp mNewsInteractorImp;

    public INewsPresenterImp(INewsView view) {
        super(view);
        mNewsInteractorImp = new INewsInteractorImp();
        mSubscription = mNewsInteractorImp.operationlogindate(this);
        mView.initRxBusEvent();

    }

    @Override
    public void requestSuccess(List<NewsChannelTable> data) {
        mView.initViewpager(data);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void operateChannelDb() {
        //这里是更新频道列表
        mSubscription = mNewsInteractorImp.operationlogindate(this);
    }
}
