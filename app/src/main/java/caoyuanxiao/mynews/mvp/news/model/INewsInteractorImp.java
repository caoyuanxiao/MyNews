package caoyuanxiao.mynews.mvp.news.model;

import java.util.Arrays;
import java.util.List;

import caoyuanxiao.mynews.R;
import caoyuanxiao.mynews.app.App;
import caoyuanxiao.mynews.callback.RequestCallback;
import caoyuanxiao.mynews.greendao.NewsChannelTable;
import caoyuanxiao.mynews.greendao.NewsChannelTableDao;
import caoyuanxiao.mynews.http.Api;
import caoyuanxiao.mynews.utils.SpUtil;
import de.greenrobot.dao.query.Query;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by Smile on 2017/4/24.
 */

public class INewsInteractorImp implements INewsInteractor<List<NewsChannelTable>> {


    @Override
    public Subscription operationlogindate(final RequestCallback<List<NewsChannelTable>> callback) {
        return Observable.create(new Observable.OnSubscribe<List<NewsChannelTable>>() {
            @Override
            public void call(Subscriber<? super List<NewsChannelTable>> subscriber) {

                final NewsChannelTableDao dao = ((App) App.getContext()).getDaoSession()
                        .getNewsChannelTableDao();

                if (!SpUtil.readBoolean("initDb")) {

                    List<String> channelName = Arrays.asList(App.getContext().getResources()
                            .getStringArray(R.array.news_channel));

                    List<String> channelId = Arrays.asList(App.getContext().getResources()
                            .getStringArray(R.array.news_channel_id));

                    for (int i = 0; i < channelName.size(); i++) {
                        NewsChannelTable table = new NewsChannelTable(channelName.get(i),
                                channelId.get(i), Api.getType(channelId.get(i)), i <= 2,
                                // 前三是固定死的，默认选中状态
                                i, i <= 2);
                        dao.insert(table);
                    }
                    SpUtil.writeBoolean("initDb", true);

                }

                final Query<NewsChannelTable> build = dao.queryBuilder()
                        .where(NewsChannelTableDao.Properties.NewsChannelSelect.eq(true))
                        .orderAsc(NewsChannelTableDao.Properties.NewsChannelIndex).build();
                subscriber.onNext(build.list());
                subscriber.onCompleted();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        callback.beforeRequest();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<NewsChannelTable>>() {
                    @Override
                    public void onCompleted() {
                        callback.requestComplete();
                    }

                    @Override
                    public void onError(Throwable e) {

                        callback.requestError(e.getLocalizedMessage() + "\n" + e);
                    }

                    @Override
                    public void onNext(List<NewsChannelTable> newsChannels) {
                        callback.requestSuccess(newsChannels);
                    }
                });
    }
}
