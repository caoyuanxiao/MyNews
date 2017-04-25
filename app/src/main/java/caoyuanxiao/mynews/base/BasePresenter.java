package caoyuanxiao.mynews.base;

/**
 * Created by Smile on 2017/4/24.
 * 这里是代理的基类 用于联通view和model之间的桥梁
 * 创建基类是为了使用在同一个activity中
 */

public interface BasePresenter {

    void onResume();

    void onDestroy();

}
