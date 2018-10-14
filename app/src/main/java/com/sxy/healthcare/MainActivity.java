package com.sxy.healthcare;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.cart.ShoppingCartFragment;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.home.HomeFragment;
import com.sxy.healthcare.me.ProfileFragment;
import com.sxy.healthcare.me.activity.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.id_content)
    FrameLayout frameLayout;

    @BindView(R.id.tab_home)
    LinearLayout tabHome;

    @BindView(R.id.tab_shopping)
    LinearLayout tabCart;

    @BindView(R.id.tab_me)
    LinearLayout tabMe;

    private int currentIndex = 0;

    private HomeFragment homeFragment;
    private ShoppingCartFragment shoppingCartFragment;
    private ProfileFragment profileFragment;

    private long firstPressedTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentIndex = getIntent().getIntExtra("index",0);

       if(null!=savedInstanceState){
            currentIndex = savedInstanceState.getInt("index",0);
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        selectTab(0);
        tabHome.setOnClickListener(this);
        tabCart.setOnClickListener(this);
        tabMe.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("index",currentIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!"login".equals(sharedPrefsUtil.getString(Constants.LOGIN_SUCCESS,""))){
            selectTab(0);
        }else {
            selectTab(currentIndex);
        }

    }

    //处理Tab的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_home:
                selectTab(0);
                break;
            case R.id.tab_shopping:
                selectTab(1);

                break;
            case R.id.tab_me:
                selectTab(2);
                break;
        }

    }

    //进行选中Tab的处理
    private void selectTab(int i) {

        currentIndex = i;

        try {
            //获取FragmentManager对象
            FragmentManager manager = getSupportFragmentManager();
            //获取FragmentTransaction对象
            FragmentTransaction transaction = manager.beginTransaction();
            //先隐藏所有的Fragment
            hideFragments(transaction);

            Intent intent;

            switch (i) {
                case 0:
                    if (homeFragment == null) {
                        homeFragment = new HomeFragment();
                        transaction.add(R.id.id_content, homeFragment);
                    } else {
                        transaction.show(homeFragment);
                    }
                    tabHome.setSelected(true);
                    tabCart.setSelected(false);
                    tabMe.setSelected(false);
                    break;
                case 1:
                    if(!"login".equals(sharedPrefsUtil.getString(Constants.LOGIN_SUCCESS,""))){
                        intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.putExtra(Constants.EXTRA_ENTER_TYPE,"cart");
                        startActivityForResult(intent,Constants.CART_REQUEST);
                    }else {
                        if (shoppingCartFragment == null) {
                            shoppingCartFragment = new ShoppingCartFragment();
                            transaction.add(R.id.id_content, shoppingCartFragment);
                        } else {
                            transaction.show(shoppingCartFragment);
                        }
                        tabHome.setSelected(false);
                        tabCart.setSelected(true);
                        tabMe.setSelected(false);
                    }

                    break;
                case 2:
                    if(!"login".equals(sharedPrefsUtil.getString(Constants.LOGIN_SUCCESS,""))){
                        intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.putExtra(Constants.EXTRA_ENTER_TYPE,"me");
                        startActivityForResult(intent,Constants.ME_REQUEST);
                    }else {
                        if (profileFragment == null) {
                            profileFragment = new ProfileFragment();
                            transaction.add(R.id.id_content, profileFragment);
                        } else {
                            transaction.show(profileFragment);
                        }
                        tabHome.setSelected(false);
                        tabCart.setSelected(false);
                        tabMe.setSelected(true);
                        break;
                    }
            }
            //不要忘记提交事务
            transaction.commit();
        }catch (Exception e){

        }
    }

    //将四个的Fragment隐藏
    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (shoppingCartFragment != null) {
            transaction.hide(shoppingCartFragment);
        }
        if (profileFragment != null) {
            transaction.hide(profileFragment);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==Constants.CART_REQUEST){
                selectTab(1);
            }else if(requestCode==Constants.ME_REQUEST){
                selectTab(2);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            super.onBackPressed();
            finish();
        } else {
            ToastUtils.shortToast(getApplicationContext(),"再按一次退出！");
            firstPressedTime = System.currentTimeMillis();
        }
    }
}
