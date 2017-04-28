package caoyuanxiao.mynews.mvp.news.model;

import java.util.List;
import java.util.Map;

import caoyuanxiao.mynews.base.BaseSubscriber;
import caoyuanxiao.mynews.bean.NeteastNewsSummary;
import caoyuanxiao.mynews.callback.RequestCallback;
import caoyuanxiao.mynews.http.Api;
import caoyuanxiao.mynews.http.HostType;
import caoyuanxiao.mynews.http.manager.RetrofitManager;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by Smile on 2017/4/27.
 * 获取NewsList的数据
 */

public class INewsListInteractorImp implements INewsListInteractor<List<NeteastNewsSummary>> {

    @Override
    public Subscription getNewsListDate(RequestCallback<List<NeteastNewsSummary>> callback, String type, final String id, int startPage) {
        return RetrofitManager.getInstance(HostType.NETEASE_NEWS_VIDEO)
                .getNewsListObservable(type, id, startPage)
                .flatMap(
                        new Func1<Map<String, List<NeteastNewsSummary>>, Observable<NeteastNewsSummary>>() {
                            // map得到list转换成item
                            @Override
                            public Observable<NeteastNewsSummary> call(Map<String, List<NeteastNewsSummary>> map) {
                                if (id.equals(Api.HOUSE_ID)) {
                                    // 房产实际上针对地区的它的id与返回key不同
                                    return Observable.from(map.get("北京"));
                                }
                                return Observable.from(map.get(id));
                            }
                        })
                .toSortedList(new Func2<NeteastNewsSummary, NeteastNewsSummary, Integer>() {
                    // 按时间先后排序
                    @Override
                    public Integer call(NeteastNewsSummary neteastNewsSummary, NeteastNewsSummary neteastNewsSummary2) {
                        return neteastNewsSummary2.ptime.compareTo(neteastNewsSummary.ptime);
                    }
                }).subscribe(new BaseSubscriber<>(callback));
    }
}
