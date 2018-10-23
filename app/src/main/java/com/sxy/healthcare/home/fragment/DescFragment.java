package com.sxy.healthcare.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseFragment;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.home.activity.MapActivity;
import com.sxy.healthcare.home.adapter.PicAdapter;
import com.sxy.healthcare.home.bean.BusinessBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import butterknife.BindView;

public class DescFragment extends BaseFragment {

    @BindView(R.id.tv_address)
    TextView address;

    @BindView(R.id.tv_time)
    TextView time;

    @BindView(R.id.tv_phone)
    TextView phone;

    @BindView(R.id.rc_pics)
    RecyclerView recyclerView;

    @BindView(R.id.rl_address)
    RelativeLayout layout;

    @BindView(R.id.webview)
    WebView webView;

    private BusinessBean businessBean;

    private PicAdapter picAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_desc);
    }

    @Override
    protected void initViews() {
        super.initViews();
        businessBean = (BusinessBean)getArguments().getSerializable(Constants.EXTRA_BUSINESS_DETAIL);
        if(null!=businessBean){
            address.setText(businessBean.getTraderDetail().getAddress());
            time.setText("营业时间："+businessBean.getTraderDetail().getOnlineTime());
            phone.setText(businessBean.getTraderDetail().getContactNo());

           /* WebSettings webSettings = webView.getSettings();
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            //设置显示缩放按钮
            webSettings.setBuiltInZoomControls(true);
            //使页面支持缩放
            webSettings.setSupportZoom(true);
            //不使用缓存
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);*/

             if(businessBean!=null&&businessBean.getTraderDetail()!=null&&businessBean.getTraderDetail().getContext()!=null){
                 webView.loadDataWithBaseURL(null,getNewContent(businessBean.getTraderDetail().getContext()),"text/html","utf-8",null);
             }


       //     recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

         //  picAdapter = new PicAdapter(getContext(),businessBean.getTraderDetail().getPics());
        //    recyclerView.setAdapter(picAdapter);
        }
    }


    private String getNewContent(String htmltext){

        Document doc= Jsoup.parse(htmltext);
        Elements elements=doc.getElementsByTag("img");
        for (Element element : elements) {
            element.attr("width","100%").attr("height","auto");
        }

        return doc.toString();
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }

    @Override
    protected void initListener() {
        super.initListener();
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapActivity.class);
                intent.putExtra(Constants.EXTRA_ADDRESS,address.getText().toString());
                intent.putExtra(Constants.EXTRA_BUSINESS_DETAIL,businessBean);
                getContext().startActivity(intent);
            }
        });
    }
}
