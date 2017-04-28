package caoyuanxiao.mynews.mvp.news.model;

import caoyuanxiao.mynews.callback.RequestCallback;
import rx.Subscription;

/**
 * Created by Smile on 2017/4/27.
 */

public interface INewsListInteractor<T> {
    Subscription getNewsListDate(RequestCallback<T> callback,String type,String id,int startPage);
}
