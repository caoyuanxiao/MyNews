package caoyuanxiao.mynews.mvp.news.model;

import caoyuanxiao.mynews.callback.RequestCallback;
import rx.Subscription;

/**
 * Created by Smile on 2017/4/24.
 */

public interface INewsInteractor<T> {
    Subscription operationlogindate(RequestCallback<T> callback);
}
