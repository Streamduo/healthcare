package com.sxy.healthcare.me.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.dialog.CommonDialog;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.SharedPrefsUtil;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.me.activity.ReserveDetailActivity;
import com.sxy.healthcare.me.bean.BookingBean;
import com.sxy.healthcare.me.event.CancelEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class ReserveAdapter extends RecyclerView.Adapter<ReserveAdapter.ViewHolder> {

    private static final String TAG = OrderAdapter.class.getSimpleName();

    private Context mContext;

    private List<BookingBean> bookingBeans;

    private FragmentManager mFragmentManager;

    private SharedPrefsUtil sharedPrefsUtil;

    public void setBookingBeans(List<BookingBean> bookingBeans) {
        this.bookingBeans = bookingBeans;
        notifyDataSetChanged();
    }

    public ReserveAdapter(Context context, FragmentManager fragmentManager){
        this.mContext = context;
        this.mFragmentManager = fragmentManager;
        sharedPrefsUtil = SharedPrefsUtil.getInstance(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_reserve, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final BookingBean bookingBean = bookingBeans.get(position);

        Glide.with(mContext).load(bookingBean.getMainPic()).apply(GlideUtils.getOptions()).into(holder.imageView);

        holder.bookNo.setText("订单编号："+bookingBean.getBookNo());

        holder.price.setText("积分消费："+bookingBean.getRealPrice());
        holder.name.setText(bookingBean.getBookDesc());

        if(bookingBean.getState()==0){
            holder.status.setText("已下单");
            holder.btnCancel.setVisibility(View.VISIBLE);
        }else  if(bookingBean.getState()==1){
            holder.status.setText("已付款");
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnJs.setText("已付款");
        }else  if(bookingBean.getState()==2){
            holder.status.setText("已接单");
            holder.btnCancel.setVisibility(View.GONE);
        }else  if(bookingBean.getState()==3){
            holder.status.setText("超时关闭");
            holder.btnJs.setText("超时关闭");
        }else  if(bookingBean.getState()==4){
            holder.status.setText("订单取消");
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnJs.setText("已取消");
        }else  if(bookingBean.getState()==5){
            holder.status.setText("订单完成");
            holder.btnJs.setText("已完成");
        }

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
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
                        cancelBooking(bookingBean.getBookNo());
                        commonDialog.dismiss();
                    }
                });
            }
        });

       /* holder.btnJs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderSettleDialog orderSettleDialog = OrderSettleDialog.newInstance();
                orderSettleDialog.show(mFragmentManager,TAG);
                orderSettleDialog.setConfirmListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ToastUtils.shortToast(mContext,"结算");
                    }
                });
            }
        });

*/

       holder.btnDel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final CommonDialog commonDialog = CommonDialog.newInstance("你确定要删除订单吗？",
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
                       delOrder(bookingBean);
                   }
               });
           }
       });

       holder.layout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(mContext, ReserveDetailActivity.class);
               intent.putExtra("reserveBean",bookingBean);
               mContext.startActivity(intent);
           }
       });


    }

    @Override
    public int getItemCount() {
        return null!=bookingBeans?bookingBeans.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView btnCancel;
        private TextView btnJs;
        private TextView bookNo;
        private TextView price;
        private TextView status;
        private TextView name;
        private ImageView imageView;

        private TextView btnDel;




        private LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            btnCancel = (TextView)itemView.findViewById(R.id.tv_cancel_order);
            btnJs = (TextView)itemView.findViewById(R.id.tv_order_settlement);
            bookNo = (TextView)itemView.findViewById(R.id.tv_no);
            price = (TextView)itemView.findViewById(R.id.tv_order_price);
            status = (TextView)itemView.findViewById(R.id.tv_order_status);
            name = (TextView)itemView.findViewById(R.id.tv_order_name);
            imageView = (ImageView) itemView.findViewById(R.id.iv_order_img);
            layout = (LinearLayout) itemView.findViewById(R.id.ll_reserve);
            btnDel = (TextView) itemView.findViewById(R.id.tv_del);
        }
    }

    /**
     * 删除订单
     * */
    private void delOrder(final BookingBean bookingBean){

        if(!NetUtils.isNetworkAvailable(mContext.getApplicationContext())){
            ToastUtils.shortToast(mContext.getApplicationContext(),"当前网络不可用～");
            return;
        }

        if(bookingBean==null){
            return;
        }

        int type =0;

        if(bookingBean.getOrderType()==1){
            type = 1;
        }else  if(bookingBean.getOrderType()==2){
            type = 2;
        }else {
            type =3;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type",type);
        jsonObject.addProperty("id",bookingBean.getBookNo());


        String param = null;
        try {
            param = ThreeDesUtils.encryptThreeDESECB(jsonObject.toString(),
                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                .delOder(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

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
                                //  orderAdapter.remove(orderBean);
                                CancelEvent cancelEvent = new CancelEvent();
                                EventBus.getDefault().post(cancelEvent);
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


    /**
     * 取消预定
     * */
    private void cancelBooking(String id){

        if(!NetUtils.isNetworkAvailable(mContext.getApplicationContext())){
            ToastUtils.shortToast(mContext.getApplicationContext(),"当前网络不可用～");
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("bookNo",id);


        String param = null;
        try {
            param = ThreeDesUtils.encryptThreeDESECB(jsonObject.toString(),
                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                .cancelBooking(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String stringResponse) {
                        LogUtils.d(TAG,"stringResponse="+stringResponse.toString());
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            LogUtils.d(TAG,"result="+result);

                            Gson gson = new Gson();
                            Response response = gson.fromJson(result,Response.class);

                            if(response.isSuccess()){
                                ToastUtils.shortToast(mContext.getApplicationContext(),"取消成功～");
                                CancelEvent cancelEvent = new CancelEvent();
                                EventBus.getDefault().post(cancelEvent);
                            }else {
                                ToastUtils.shortToast(mContext.getApplicationContext(),"取消失败～");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.shortToast(mContext.getApplicationContext(),"取消失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
