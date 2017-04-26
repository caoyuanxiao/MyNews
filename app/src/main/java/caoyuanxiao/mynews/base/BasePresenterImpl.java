package caoyuanxiao.mynews.base;

import caoyuanxiao.mynews.callback.RequestCallback;
import rx.Subscription;

/**
 * Created by Smile on 2017/4/26.
 */

public class BasePresenterImpl<T extends BaseView, V> implements BasePresenter, RequestCallback<V> {
    protected T mView;
    protected Subscription mSubscription;

    public BasePresenterImpl(T view) {
        mView = view;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        if (mSubscription != null && mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
        mView = null;
    }

    @Override
    public void beforeRequest() {

    }

    @Override
    public void requestError(String msg) {
    }

    @Override
    public void requestComplete() {

    }

    @Override
    public void requestSuccess(V data) {

    }
}
