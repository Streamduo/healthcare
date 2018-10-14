package com.sxy.healthcare.cart.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.dialog.CommonDialog;
import com.sxy.healthcare.cart.bean.DelShopEvent;
import com.sxy.healthcare.cart.bean.GoodsBean;
import com.sxy.healthcare.common.event.ShopEvent;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.SharedPrefsUtil;
import com.sxy.healthcare.common.utils.StringUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.common.view.SnappingStepper;
import com.sxy.healthcare.common.view.SnappingStepperValueChangeListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {

    private static final String TAG = ShoppingCartAdapter.class.getSimpleName();

    private Context mContext;

    private List<GoodsBean> goodsBeans;

    private Disposable delDis;

    private SharedPrefsUtil sharedPrefsUtil;

    private RequestOptions options;

    private FragmentManager mFragmentManager;

    public ShoppingCartAdapter(Context context,FragmentManager fragmentManager){
        this.mContext = context;
        sharedPrefsUtil = SharedPrefsUtil.getInstance(mContext);
        this.mFragmentManager = fragmentManager;

        options = new RequestOptions()
                .placeholder(R.color.gray_979797)
                .error(R.color.gray_979797);
    }

    public void setGoodsBeans(List<GoodsBean> goodsBeans) {
        this.goodsBeans = goodsBeans;
        notifyDataSetChanged();
    }

    public List<GoodsBean> getGoodsBeans() {
        return goodsBeans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_shopping_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(goodsBeans.get(position).isSelect()){
            holder.mCheckBox.setChecked(true);
        }else {
            holder.mCheckBox.setChecked(false);
        }

        Glide.with(mContext).load(goodsBeans.get(position).getPic()).apply(GlideUtils.getOptions()).into(holder.goodImg);

        holder.name.setText(goodsBeans.get(position).getGoodsName());

        if(StringUtils.isEmpty(goodsBeans.get(position).getPrice())){
            holder.price.setText("积分消费："+0+"/");
        }else {
            holder.price.setText("积分消费："+goodsBeans.get(position).getPrice()+"/"+goodsBeans.get(position).getUnit());
        }

        holder.num.setValue(goodsBeans.get(position).getQuantity());

        holder.num.setOnValueChangeListener(new SnappingStepperValueChangeListener() {
            @Override
            public void onValueChange(View view, int value) {

                holder.tvNum.setText("数量："+holder.num.getValue()+"");
                ShopEvent shopEvent = new ShopEvent();
                goodsBeans.get(position).setQuantity(holder.num.getValue());
                shopEvent.setGoodsBeans(getGoodsBeans());
                //  RxBus.get().post(birthEvent);
                EventBus.getDefault().post(shopEvent);
                try {
                    changeNum(goodsBeans.get(position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.tvNum.setText("数量："+holder.num.getValue()+"");

        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CommonDialog commonDialog = CommonDialog.newInstance("你确定要取消订单吗？",
                        "取消","确定");
                commonDialog.show(mFragmentManager,TAG);
                commonDialog.setCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        commonDialog.dismiss();
                    }
                });
                commonDialog.setConfirmListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        commonDialog.dismiss();
                        delGoods(goodsBeans.get(position));
                    }
                });
            }
        });

        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(goodsBeans.get(position).isSelect()){
                    goodsBeans.get(position).setSelect(false);
                }else {
                    goodsBeans.get(position).setSelect(true);
                }
                ShopEvent shopEvent = new ShopEvent();
                shopEvent.setGoodsBeans(getGoodsBeans());
                //  RxBus.get().post(birthEvent);
                EventBus.getDefault().post(shopEvent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null!=goodsBeans?goodsBeans.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CheckBox mCheckBox;
        private ImageView goodImg;

        private TextView name;
        private TextView price;
        private TextView tvNum;

        private TextView delBtn;

        private SnappingStepper num;


        public ViewHolder(View itemView) {
            super(itemView);
            mCheckBox = (CheckBox)itemView.findViewById(R.id.item_cb_sc);
            name = (TextView)itemView.findViewById(R.id.item_tv_name);
            goodImg = (ImageView)itemView.findViewById(R.id.item_iv_goods);
            price = (TextView)itemView.findViewById(R.id.item_tv_price);
            num = (SnappingStepper)itemView.findViewById(R.id.num);
            tvNum = (TextView) itemView.findViewById(R.id.item_tv_num);
            delBtn = (TextView) itemView.findViewById(R.id.item_tv_del);
        }
    }

    /**
     * 改变商品数量
     * */
    private void changeNum(final GoodsBean goodsBean) throws JSONException {

        if(!NetUtils.isNetworkAvailable(mContext.getApplicationContext())){
            ToastUtils.shortToast(mContext.getApplicationContext(),"当前网络不可用～");
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id",goodsBean.getId());
        jsonObject.addProperty("quantity",goodsBean.getQuantity());

        LogUtils.d(TAG,"jsonObject="+jsonObject.toString());

        String param = null;
        try {
            param = ThreeDesUtils.encryptThreeDESECB(jsonObject.toString(),
                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("token",sharedPrefsUtil.getString(Constants.USER_TOKEN,""));
        jsonObject1.put("param",param);

        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),jsonObject1.toString());
        destroyDis();

        ApiServiceFactory.getStringApiService()
                .changeNum(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        delDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            Gson gson = new Gson();
                            Response<String> response = gson.fromJson(result,Response.class);

                            if(response.isSuccess()){
                                //ToastUtils.shortToast(mContext.getApplicationContext(),"改变成功");
                            }else {
                              //  ToastUtils.shortToast(mContext.getApplicationContext(),response.getMsg());
                            }
                            LogUtils.d(TAG,"result="+response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                       // ToastUtils.shortToast(mContext.getApplicationContext(),"改变失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * 删除购物车商品
     * */
    private void delGoods(final GoodsBean goodsBean){

        if(!NetUtils.isNetworkAvailable(mContext.getApplicationContext())){
            ToastUtils.shortToast(mContext.getApplicationContext(),"当前网络不可用～");
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id",goodsBean.getId());

        String param = null;
        try {
            param = ThreeDesUtils.encryptThreeDESECB(jsonObject.toString(),
                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        destroyDis();

        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("token",sharedPrefsUtil.getString(Constants.USER_TOKEN,""));
            jsonObject1.put("param",param);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonObject1.toString());

        ApiServiceFactory.getStringApiService()
                .delGoods(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        delDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            Gson gson = new Gson();
                            Response<String> response = gson.fromJson(result,Response.class);

                            if(response.isSuccess()){
                                ToastUtils.shortToast(mContext.getApplicationContext(),"删除成功");
                                goodsBeans.remove(goodsBean);
                                notifyDataSetChanged();
                                EventBus.getDefault().post(new DelShopEvent());
                            }else {
                                ToastUtils.shortToast(mContext.getApplicationContext(),response.getMsg());
                            }
                            LogUtils.d(TAG,"result="+response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.shortToast(mContext.getApplicationContext(),"删除失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }



    private void destroyDis(){
        if (null!=delDis&&!delDis.isDisposed()){
            delDis.dispose();
        }
    }

}

