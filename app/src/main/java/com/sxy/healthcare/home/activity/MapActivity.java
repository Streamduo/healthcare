package com.sxy.healthcare.home.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.lbsapi.MKGeneralListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.StringUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.home.bean.BusinessBean;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class MapActivity extends BaseActivity implements
        OnGetGeoCoderResultListener {

    private static final String TAG = MapActivity.class.getSimpleName();

    @BindView(R.id.mapview)
    MapView mMapView;

    // 定位相关
    private LocationClient mLocClient;

    public MyLocationListenner myListener;

    BitmapDescriptor mCurrentMarker;

    private BaiduMap mBaiduMap;

    /**
     * 当前定位的模式
     */
    private MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
    /***
     * 是否是第一次定位
     */
    private volatile boolean isFristLocation = true;

    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    boolean isFirstLoc = true;// 是否首次定位

    private String address;

    private BusinessBean businessBean;

    private LatLng latLng;

    private static final String BAI_DU="com.baidu.BaiduMap";


    private static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what==1){

            }
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setCurrentTitle("地图导航");
        doReturn();

        businessBean = (BusinessBean)getIntent().getSerializableExtra(Constants.EXTRA_BUSINESS_DETAIL);
        LogUtils.d(TAG,"businessBean="+businessBean.toString());
        address = getIntent().getStringExtra(Constants.EXTRA_ADDRESS);
        getLngAndLat(address);

      //  LogUtils.d(TAG,"address="+address);

        // 地图初始化
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.anjuke_icon_itis_position);

        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.zoomTo(15.0f);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

      //   初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
       mSearch.setOnGetGeoCodeResultListener(this);

        if(businessBean!=null){
            String strLatLng = businessBean.getTraderDetail().getAddressXy();
            if(null!=strLatLng){
                String[] strings = strLatLng.split(",");
                latLng = new LatLng(Double.parseDouble(strings[0]),Double.parseDouble(strings[1]));

                LogUtils.d(TAG,"latLng="+latLng.toString());
                //mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));

            }
        }


        if(!StringUtils.isEmpty(address)&&address.length()>6){
            LogUtils.d(TAG,"city="+address.substring(0,3)+",address="+address.substring(3,address.length()-1));
            // Geo搜索
            mSearch.geocode(new GeoCodeOption().city(address.substring(0,3)).address(address.substring(3,address.length()-1)));
        }


        //  定位初始化
//        mLocClient = new LocationClient(this);
//        mLocClient.registerLocationListener(new MyLocationListenner());
//        LocationClientOption option = new LocationClientOption();
//        option.setOpenGps(true);// 打开gps
//        option.setCoorType("bd09ll"); // 设置坐标类型
//        option.setScanSpan(1000);
//        mLocClient.setLocOption(option);
//        mLocClient.start();

    }


    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        LogUtils.d(TAG,"[onGetReverseGeoCodeResult] status="
                +reverseGeoCodeResult.status+",error="+reverseGeoCodeResult.error
        +",address="+reverseGeoCodeResult.getAddress()+",city="
                +reverseGeoCodeResult.getAddressDetail().city);

        LatLng ll = new LatLng(116.66626606301684,
                39.889199928563467);
        mBaiduMap.addOverlay(new MarkerOptions().title(address).position(ll)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.anjuke_icon_itis_position)));
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(ll));
    }


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;

            LogUtils.d(TAG,"location="+location.getAddress().city);


            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            if (isFirstLoc) {
                isFirstLoc = false;
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }

            mBaiduMap.addOverlay(new MarkerOptions().title(address).position(ll)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.anjuke_icon_itis_position)));
            mBaiduMap.setMyLocationEnabled(false);
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }


    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
      //  mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    public void onGetGeoCodeResult(final GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MapActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        LogUtils.d(TAG,"result="+result.getAddress().toString());
        View view = LayoutInflater.from(MapActivity.this).inflate(R.layout.layout_marker, null);
        TextView textView = (TextView)view.findViewById(R.id.tv_address);
        textView.setText(result.getAddress());
        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().title(address).position(result.getLocation())
                .icon(BitmapDescriptorFactory
                        .fromView(view)));
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(isPackageInstalled(getApplicationContext(),BAI_DU)){
                    Intent naviIntent = new Intent("android.intent.action.VIEW",
                            android.net.Uri.parse("baidumap://map/geocoder?location="
                            +result.getLocation().latitude + "," + result.getLocation().longitude));
                    startActivity(naviIntent);
                }else{
                    ToastUtils.LongToast(getApplicationContext(),"请先安装百度地图～");
                }
                return false;
            }
        });

        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);
       // Toast.makeText(MapActivity.this, strInfo, Toast.LENGTH_LONG).show();
    }


    public static Map<String,Double> getLngAndLat(String address){
        Map<String,Double> map=new HashMap<String, Double>();
        final String url = "http://api.map.baidu.com/geocoder/v2/?address="+address+"&output=json&ak=F454f8a5efe5e577997931cc01de3974";

        new Thread(new Runnable() {
            @Override
            public void run() {
                String json = loadJSON(url);
                Message message = Message.obtain();
                message.what=1;
                message.obj= json;
                LogUtils.d(TAG,"json="+json);
            }
        }).start();


       /* JSONObject obj = JSONObject.fromObject(json);
        if(obj.get("status").toString().equals("0")){
            double lng=obj.getJSONObject("result").getJSONObject("location").getDouble("lng");
            double lat=obj.getJSONObject("result").getJSONObject("location").getDouble("lat");
            map.put("lng", lng);
            map.put("lat", lat);
            //System.out.println("经度："+lng+"---纬度："+lat);
        }else{
            //System.out.println("未找到相匹配的经纬度！");
        }*/
        return map;
    }

    public static String loadJSON (String url) {
        StringBuilder json = new StringBuilder();
        try {
            URL oracle = new URL(url);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String inputLine = null;
            while ( (inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        return json.toString();
    }


    public static boolean isPackageInstalled(Context mContext, String packagename) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = mContext.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        } finally {
            return packageInfo == null ? false : true;
        }
    }

}
