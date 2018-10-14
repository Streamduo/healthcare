package com.sxy.healthcare.common.net;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    @GET("")
    Observable<Response<String>> getLabels(@Query("type")String type);


    @GET("api/common/getBaseInfo?")
    Observable<String> getBaseInfo();


    /**
     *http://sapi.wo946.com/sapi/api/common/init
     * */
    @GET("api/common/init")
    Observable<Response<String>> getInitInfo();


    /**
     * 获取短信验证码
     * */
    @POST("api/sms/sendSms")
    Observable<String> getSmsValidateCode(
            @Body RequestBody info
    );


    /**
     * 注册
     * */
    @POST("api/member/register")
    Observable<String> doRegister(
            @Body RequestBody info
    );

    /**
     * 忘记密码
     * */
    @POST("api/member/findPwd")
    Observable<String> doFindPwd(
            @Body RequestBody info
    );

    /**
     * 修改密码
     * */
    @POST("api/member/changePwd")
    Observable<String> doChangePwd(
            @Body RequestBody info
    );


    /**
     * 修改资料
     * */
    @POST("api/member/updateMember")
    Observable<String> doChangeInfo(
            @Body RequestBody info
    );


    /**
     * 登录
     * */
    @POST("api/member/login")
    Observable<String> doLogin(
            @Query("token") String token,
            @Query("param") String param
    );

    /**
     * 登录
     * */
    @POST("api/member/login")
    Observable<String> doLoginBak(
            @Body RequestBody info
    );

    /**
     * 退出登录
     * */
    @POST("api/member/loginOut")
    Observable<String> doExit(
            @Body RequestBody info
    );

    /**
     * 获取验证码预身份
     * */
    @POST("api/common/getPreValidateCode")
    Observable<String> getPreValidateCode(
            @Body RequestBody info
    );

    /**
     * 获取验证码
     * */
    @POST("api/common/getValidateCode")
    Observable<String> getValidateCode(
            @Query("token") String token,
            @Query("vc") String vc
    );


    /**
     * 首页接口
     * */
    @POST("api/common/index")
    Observable<String> getHomeData(
            @Query("token") String token
    );

    /**
     * 首页接口
     * */
    @POST("api/common/index")
    Observable<String> getHomeDataBak(
            @Body RequestBody info
    );

    /**
     * 商家列表
     * */
    @POST("api/trader/getTraders")
    Observable<String> getTraders(
            @Body RequestBody info
    );

    /**
     * 商家详情
     * */
    @POST("api/trader/getTraderDetail")
    Observable<String> getTraderDetail(
            @Body RequestBody info
    );

    /**
     * 添加商家评论
     * */
    @POST("api/trader/addTraderComment")
    Observable<String> addTraderComment(
            @Query("token") String token,
            @Query("param") String param
    );

    /**
     * 商家评论
     * */
    @POST("api/trader/getTraderComments")
    Observable<String> getTraderComments(
            @Body RequestBody info
    );

    /**
     * 发票列表
     * */
    @POST("api/bill/getBills")
    Observable<String> getBills(
            @Query("token") String token,
            @Query("param") String param
    );

    /**
     * 添加发票
     * */
    @POST("api/bill/addBill")
    Observable<String> addBill(
            @Body RequestBody info
    );

    /**
     * 我邀请的人
     * */
    @POST("api/member/getInviters")
    Observable<String> getInviters(
            @Body RequestBody info
    );

    /**
     * 获取概要信息
     * */
    @POST("api/member/getUserProfile")
    Observable<String> getUserProfile(
            @Body RequestBody info
    );

    /**
     * 获取交易记录
     * */
    @POST("api/recharge/getRecharges")
    Observable<String> getRecharges(
            @Body RequestBody info
    );

    /**
     * 购物车
     * */
    @POST("api/cart/getCarts")
    Observable<String> getCarts(
            @Body RequestBody info
    );

    /**
     * 我的订单
     * */
    @POST("api/order/getOrders")
    Observable<String> getOrders(
            @Body RequestBody info
    );

    /**
     * 订单详情
     * */
    @POST("api/order/getOrdersDetail")
    Observable<String> getOrdersDetail(
            @Body RequestBody info
    );

    /**
     * 取消订单
     * */
    @POST("api/order/cancelOrder")
    Observable<String> cancelOrder(
            @Body RequestBody info
    );

    /**
     * 删除订单
     * */
    @POST("api/order/delOrder")
    Observable<String> delOder(
            @Body RequestBody info
    );

    /**
     * 删除购物车商品
     * */
    @POST("api/cart/delGoods")
    Observable<String> delGoods(
            @Body RequestBody info
    );

    /**
     * 加入购物车
     * */
    @POST("api/cart/addToCart")
    Observable<String> addToCart(
            @Body RequestBody info
    );


    /**
     * 下单
     * */
    @POST("api/order/commit")
    Observable<String> commit(
            @Body RequestBody info
    );

    /**
     * 我的预约信息
     * */
    @POST("api/booking/getBookings")
    Observable<String> getReservations(
            @Body RequestBody info
    );

    /**
     * 获取菜系
     * */
    @POST("api/goods/getGoodsCuisines")
    Observable<String> getGoodsCuisines(
            @Body RequestBody info
    );

    /**
     * 我的交易记录
     * */
    @POST("api/changes/getChanges")
    Observable<String> getChanges(
            @Body RequestBody info
    );

    /**
     *
     * */
    @POST("api/goods/getGoods")
    Observable<String> getGoods(
            @Body RequestBody info
    );

    /**
     *
     * */
    @POST("api/goods/getGoodsDetail")
    Observable<String> getGoodsDetail(
            @Body RequestBody info
    );

    /**
     *餐饮预定
     * */
    @POST("api/booking/bookingRestaurant")
    Observable<String> bookingRestaurant(
            @Body RequestBody info
    );

    /**
     *其他预定
     * */
    @POST("api/booking/bookingOther")
    Observable<String> bookingOther(
            @Body RequestBody info
    );

    /**
     *取消预定
     * */
    @POST("api/booking/cancelBooking")
    Observable<String> cancelBooking(
            @Body RequestBody info
    );


    /**
     *上传头像
     * */
    @Multipart
    @POST("api/member/updateMemberHeadImg")
    Observable<String> updateMemberHeadImg(
            @Query("token") String token,
            @Part MultipartBody.Part headImgFile
    );

    /**
     *支付
     * */
    @POST("api/pay/bookingOrderPay")
    Observable<String> bookingOrderPay(
            @Body RequestBody info
    );

    /**
     *积分充值
     * */
    @POST("api/pay/scoreRecharge")
    Observable<String> scoreRecharge(
            @Body RequestBody info
    );

    /**
     *积分充值
     * */
    @POST("api/pay/customScoreRecharge")
    Observable<String> customScoreRecharge(
            @Body RequestBody info
    );

    /**
     *改变购物车数量
     * */
    @POST("api/cart/changeNum")
    Observable<String> changeNum(
            @Body RequestBody info
    );

    /**
     *获取餐饮预约详情
     * */
    @POST("api/booking/getBookingRestaurantDetail")
    Observable<String> getBookingRestaurantDetail(
            @Body RequestBody info
    );

    /**
     *获取版本升级信息
     * */
    @POST("api/common/getVersion")
    Observable<String> getVersion(
            @Body RequestBody info
    );

    /**
     *创建线下订单
     * */
    @POST("api/order/createOfflineOrder")
    Observable<String> getCreateOrder(
            @Body RequestBody info
    );

    /**
     *支付订单
     * */
    @POST("api/pay/bookingOrderPay")
    Observable<String> getPayOrder(
            @Body RequestBody info
    );

}
