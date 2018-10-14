package com.sxy.healthcare.me.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.BaseFragment;
import com.sxy.healthcare.me.OrderFragment;

import butterknife.BindView;



public class MyOrderActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = MyOrderActivity.class.getSimpleName();


    @BindView(R.id.tv_all)
    TextView btnall;

    @BindView(R.id.tv_no_finish)
    TextView btnno;

    @BindView(R.id.tv_finish)
    TextView btnfinish;

    @BindView(R.id.content)
    FrameLayout layout;

    private BaseFragment currentFragment;

    private OrderFragment orderFragment1;
    private OrderFragment orderFragment2;
    private OrderFragment orderFragment3;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        setCurrentTitle("我的订单");
    }

    @Override
    protected void initViews() {
        super.initViews();
        doReturn();
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        orderFragment1 = new OrderFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt("orderType",0);
        orderFragment1.setArguments(bundle1);


        orderFragment2 = new OrderFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("orderType",1);
        orderFragment2.setArguments(bundle2);

        orderFragment3 = new OrderFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putInt("orderType",3);
        orderFragment3.setArguments(bundle3);

        btnall.setSelected(true);
        btnno.setSelected(false);
        btnfinish.setSelected(false);

        add(orderFragment1,1,R.id.content,"orderFragment1");

    }

    @Override
    protected void initListener() {
        super.initListener();
        btnall.setOnClickListener(this);
        btnfinish.setOnClickListener(this);
        btnno.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_all:
                btnall.setSelected(true);
                btnno.setSelected(false);
                btnfinish.setSelected(false);
                add(orderFragment1,1,R.id.content,"orderFragment1");
                break;
            case R.id.tv_no_finish:
                btnall.setSelected(false);
                btnno.setSelected(true);
                btnfinish.setSelected(false);
                add(orderFragment2,2,R.id.content,"orderFragment2");

                break;
            case R.id.tv_finish:
                btnall.setSelected(false);
                btnno.setSelected(false);
                btnfinish.setSelected(true);
                add(orderFragment3,3,R.id.content,"orderFragment3");
                break;
            default:
                break;
        }
    }


    public void add(BaseFragment fragment, int type, int id,String tag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        OrderFragment tempFragment1;
        OrderFragment  tempFragment2;
        OrderFragment tempFragment3;
        if(type==1){
            //优先检查，fragment是否存在，避免重叠

            tempFragment1 = (OrderFragment) fragmentManager.findFragmentByTag(tag);

            if(null!=tempFragment1){
                fragment = tempFragment1;
            }

        }else if(type==2){
            tempFragment2 = (OrderFragment) fragmentManager.findFragmentByTag(tag);
            if(null!=tempFragment2){
                fragment = tempFragment2;
            }
        }else if(type==3){
            tempFragment3 = (OrderFragment) fragmentManager.findFragmentByTag(tag);
            if(null!=tempFragment3){
                fragment = tempFragment3;
            }
        }

        if(fragment.isAdded()){
            addOrShowFragment(fragmentTransaction,fragment,id,tag);
        }else{
            if(currentFragment!=null&&currentFragment.isAdded()){
                fragmentTransaction.hide(currentFragment).add(id, fragment,tag).commit();
            }else{
                fragmentTransaction.add(id, fragment,tag).commit();
            }
            currentFragment = fragment;
        }
    }

    /**
     * 添加或者显示 fragment
     *
     * @param fragment
     */
    private void addOrShowFragment(FragmentTransaction transaction, BaseFragment fragment, int id,String tag) {
        if(currentFragment == fragment)
            return;
        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(currentFragment).add(id, fragment,tag).commit();
        } else {
            transaction.hide(currentFragment).show(fragment).commit();
        }
        currentFragment.setUserVisibleHint(false);
        currentFragment =  fragment;
        currentFragment.setUserVisibleHint(true);
    }

}
