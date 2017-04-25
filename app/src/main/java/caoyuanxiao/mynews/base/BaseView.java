package caoyuanxiao.mynews.base;

/**
 * Created by Smile on 2017/4/24.
 * 视图的基类  后面的MVP模式会使用到
 */

public interface BaseView {
    void toast(String msg);

    void showProgress();

    void hideProgress();
}
