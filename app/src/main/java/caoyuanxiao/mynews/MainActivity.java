package caoyuanxiao.mynews;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import caoyuanxiao.mynews.annotation.ActivityFragmentInject;
import caoyuanxiao.mynews.base.BaseActivity;
import caoyuanxiao.mynews.bean.User;
import caoyuanxiao.mynews.utils.RxBus;

import static caoyuanxiao.mynews.R.id.tv_back;

@ActivityFragmentInject(
        contentViewId = R.layout.activity_main
)
public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        findViewById(tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User("yuanxiao", "520");
                List<User> users = new ArrayList<User>();
                users.add(user);
                users.add(user);
                RxBus.get().post("back", users);
                finish();
            }
        });
    }


}
