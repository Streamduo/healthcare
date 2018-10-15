package com.sxy.healthcare.me.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.common.event.ModifyAvatar;
import com.sxy.healthcare.common.event.NicknameEvent;
import com.sxy.healthcare.common.event.RxBus;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.FileUtils;
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.StringUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.common.utils.Util;
import com.sxy.healthcare.common.view.NicknameDialog;
import com.sxy.healthcare.me.bean.UserInfo;
import com.sxy.healthcare.me.dialog.DatePickerFragment;
import com.sxy.healthcare.me.dialog.ModifyDialog;
import com.sxy.healthcare.me.event.BirthEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;

public class ModifyInfoActivity extends BaseActivity implements View.OnClickListener,EasyPermissions.PermissionCallbacks {

    private static final String TAG = ModifyInfoActivity.class.getSimpleName();

    @BindView(R.id.tv_birth)
    TextView tvBirth;

    @BindView(R.id.tv_modify_pwd)
    TextView btnModifyPwd;

    @BindView(R.id.btn_submit)
    TextView btnSubmit;

    @BindView(R.id.rb_man)
    RadioButton radioMan;
    @BindView(R.id.rb_woman)
    RadioButton radioWonan;
    @BindView(R.id.rg_sex)
    RadioGroup radioGroup;

    private String mSex;

    private Disposable birthDis;

    private Disposable modifyDis;

    @BindView(R.id.tv_user_name)
    TextView mobile;

    @BindView(R.id.iv_user_avatar)
    CircleImageView avatar;

    @BindView(R.id.tv_card_no)
    TextView cardNo;

    @BindView(R.id.tv_phone)
    TextView phone;

    @BindView(R.id.tv_no)
    TextView tvNo;

    @BindView(R.id.tv_yc)
    TextView tvYc;

    @BindView(R.id.tv_balance)
    TextView tvBalance;

    @BindView(R.id.tv_yq)
    TextView tvYq;

    @BindView(R.id.tv_modify_info)
    TextView modifyInfo;

   /* @BindView(R.id.tv_nickname)
    EditText tvNickname;*/

    private UserInfo userInfo;

    private Uri tempUri;

    private Uri mURI;

    private String fileUrl;

    private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 0;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;//

    /* 头像名称 */
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;

    private NicknameDialog commonDialog;

   private Uri uritempFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_info);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        setCurrentTitle(R.string.profile_modify_info);
        doReturn();

    }

    @Override
    protected void initDatas() {
        super.initDatas();

        try {
            userInfo = (UserInfo) getIntent().getSerializableExtra(Constants.EXTRA_USER_INFO);

            if(null!=userInfo){
                LogUtils.d(TAG,"userInfo="+userInfo.toString());
                mSex = userInfo.getSex();
                mobile.setText(userInfo.getNickName());
                cardNo.setText("会员号："+userInfo.getCardNo());
                phone.setText(userInfo.getMobile());
                tvNo.setText(userInfo.getCardNo());
                tvBalance.setText(userInfo.getBalance()+"");
                tvYc.setText(userInfo.getFirstRecharge());
                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.default_head)// 正在加载中的图片
                        .error(R.drawable.default_head) // 加载失败的图片
                        .diskCacheStrategy(DiskCacheStrategy.ALL); // 磁盘缓存策略
                Glide.with(ModifyInfoActivity.this)
                        .load(userInfo.getHeadImg()).apply(options).into(avatar);

                tvBirth.setText(userInfo.getBirthday());


                if(userInfo.getSex().equals("男")||userInfo.getSex().equals("0")){
                    radioMan.setChecked(true);
                }else {
                    radioWonan.setChecked(true);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

     //   operateBus();
    }

    @Override
    protected void initListener() {
        super.initListener();

        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  commonDialog = null;
                commonDialog = NicknameDialog.newInstance();
                commonDialog.show(getSupportFragmentManager(),TAG);
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
                        if(StringUtils.isEmpty(commonDialog.getDesc())){
                            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"请输入昵称");
                            return;
                        }
                       doModify(commonDialog.getDesc());
                    }
                });*/
            }
        });

        cardNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*commonDialog = null;
                commonDialog = NicknameDialog.newInstance();
                commonDialog.show(getSupportFragmentManager(),TAG);
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
                        if(StringUtils.isEmpty(commonDialog.getDesc())){
                            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"请输入昵称");
                            return;
                        }

                            userInfo.setNickName(commonDialog.getDesc());
                            doModify(commonDialog.getDesc());
                    }
                });*/
            }
        });

        tvBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "DatePicker");
            }
        });

        btnModifyPwd.setOnClickListener(this);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton)ModifyInfoActivity.this.findViewById(radioGroup.getCheckedRadioButtonId());
                mSex = rb.getText().toString();
            }
        });

        btnSubmit.setOnClickListener(this);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent1 = new Intent(Intent.ACTION_PICK);
                intent1.setType("image/*");
                startActivityForResult(intent1, PHOTO_REQUEST_GALLERY);*/
              //  showChoosePicDialog();
            }
        });


        modifyInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ModifyDialog modifyDialog = ModifyDialog.newInstance();
                modifyDialog.show(getSupportFragmentManager(),TAG);
                modifyDialog.setCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        modifyDialog.dismiss();
                    }
                });

                modifyDialog.setJFListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonDialog = null;
                        commonDialog = NicknameDialog.newInstance();
                        commonDialog.show(getSupportFragmentManager(),TAG);
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
                                if(StringUtils.isEmpty(commonDialog.getDesc())){
                                    ToastUtils.shortToast(HealthcaseApplication.getApplication(),"请输入昵称");
                                    return;
                                }
                                doModify(commonDialog.getDesc());
                            }
                        });
                        modifyDialog.dismiss();
                    }
                });

                modifyDialog.setWxJFListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showChoosePicDialog();
                        modifyDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_modify_pwd:
                Intent intent = new Intent(ModifyInfoActivity.this,ModifyPwdActivity.class);
                intent.putExtra(Constants.EXTRA_USER_INFO,userInfo);
                startActivity(intent);
                Util.hideSoftKeyboard(this);
                break;
            case R.id.btn_submit:
                doModify("");
                break;
            default:
                break;
        }

    }

    /**
     * 修改资料
     * */
    private void doModify(final String nickname){

        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }

        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("birthday",tvBirth.getText().toString());
        if("男".equals(mSex)){
            jsonObject.addProperty("sex", 0);
        }else {
            jsonObject.addProperty("sex", 1);
        }


       /* if(StringUtils.isEmpty(mSex)){
            ToastUtils.shortToast(getApplicationContext(),"请选择性别～");
            return;
        }*/

        if(StringUtils.isEmpty(nickname)){
            jsonObject.addProperty("nickName",userInfo.getNickName());
        }else {
            jsonObject.addProperty("nickName",nickname);
        }




        destroyLoginDis();
        btnSubmit.setEnabled(false);

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
                .doChangeInfo(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        modifyDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            Gson gson = new Gson();

                            Type type = new TypeToken<Response<UserInfo>>(){}.getType();

                            Response<UserInfo>  response = gson.fromJson(result,type);

                            LogUtils.d(TAG,"result="+response.toString());

                            if(response.isSuccess()){
                                ToastUtils.shortToast(getApplicationContext(),"修改成功～");
                                if(StringUtils.isEmpty(nickname)){
                                    userInfo.setNickName(mobile.getText().toString());
                                }else {
                                    userInfo.setNickName(nickname);
                                }
                                LogUtils.d(TAG,"useinfo="+userInfo.toString());
                                mobile.setText(userInfo.getNickName());
                                NicknameEvent nicknameEvent = new NicknameEvent();
                                nicknameEvent.setNickname(userInfo.getNickName());
                                nicknameEvent.setSex(mSex);
                                nicknameEvent.setBirth(tvBirth.getText().toString());
                                EventBus.getDefault().post(nicknameEvent);
                            }else {
                                ToastUtils.shortToast(getApplicationContext(),response.getMsg());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        btnSubmit.setEnabled(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.shortToast(getApplicationContext(),"修改失败～");
                        btnSubmit.setEnabled(true);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    private void destroyLoginDis(){
        if (null!=modifyDis&&!modifyDis.isDisposed()){
            modifyDis.dispose();
        }
    }


    /**
     * 修改头像
     * */
    private void doModifyAvatar(Bitmap  bitmap){

        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }

        if(null==bitmap){
            return;
        }

        String path = FileUtils.saveBitmap(ModifyInfoActivity.this,bitmap);

        LogUtils.d(TAG,"[doModifyAvatar] path="+path
             );

        if(path==null){
            return;
        }
        File file = new File(path);

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part multipartFile =
                MultipartBody.Part.createFormData("headImgFile", file.getName(), requestBody);

        LogUtils.d(TAG,"[doModifyAvatar] 1111");

        ApiServiceFactory.getStringApiService()
                .updateMemberHeadImg(sharedPrefsUtil.getString(Constants.USER_TOKEN,""),
                        multipartFile)
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

                            LogUtils.d(TAG,"[doModifyAvatar]result="+result);

                            Gson gson = new Gson();

                            Type type = new TypeToken<Response<String>>(){}.getType();

                            Response<String>  response = gson.fromJson(result,type);

                            if(response.isSuccess()){
                                userInfo.setHeadImg(response.getData());
                                Glide.with(ModifyInfoActivity.this)
                                        .load(userInfo.getHeadImg()).apply(GlideUtils.getOptionsAvatar()).into(avatar);
                                ToastUtils.shortToast(getApplicationContext(),"修改成功～");
                                ModifyAvatar modifyAvatar = new ModifyAvatar();
                                modifyAvatar.setImgUrl(response.getData());
                                RxBus.get().post(modifyAvatar);
                            }else {
                                ToastUtils.shortToast(getApplicationContext(),response.getMsg());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.shortToast(getApplicationContext(),"修改失败～");
                        LogUtils.d(TAG,"[doModifyAvatar] 2222="+e.toString());

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = null;
        if(requestCode == PHOTO_REQUEST_CAREMA){
            LogUtils.d(TAG,"[onActivityResult] 11111111111111111111111");

            crop(tempUri); // 对图片进行裁剪处理

        } else if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                 uri = data.getData();
               // doModifyAvatar(uri);
                crop(uri);
            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据

            try {
                if (data != null) {
                    //  Bitmap bitmap = data.getParcelableExtra("data");
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
                    /**
                     * 获得图片
                     */
                    //avatar.setImageBitmap(bitmap);
                    Glide.with(ModifyInfoActivity.this)
                            .load(bitmap).apply(GlideUtils.getOptionsAvatar()).into(avatar);
                    doModifyAvatar(bitmap);

                }
                // 将临时文件删除
                if(tempFile!=null)
                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
     * 剪切图片
     */
    private void crop(Uri uri) {
        if(uri==null){
            return;
        }
        LogUtils.d(TAG,"[crop] uri="+uri.toString());


        Intent intent = new Intent("com.android.camera.action.CROP");
        //sdk>=24
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            intent.setDataAndType(uri, "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        } else {
                intent.setDataAndType(uri, "image/*");
        }

        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 图片格式
        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        LogUtils.d(TAG,"[crop] 222");
        startActivityForResult(intent, PHOTO_REQUEST_CUT);




       /* if (EasyPermissions.hasPermissions(ModifyInfoActivity.this, "android.permission.READ_EXTERNAL_STORAGE")) {

            // 裁剪图片意图
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            // 裁剪框的比例，1：1
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // 裁剪后输出图片的尺寸大小
            intent.putExtra("outputX", 250);
            intent.putExtra("outputY", 250);

            intent.putExtra("outputFormat", "JPEG");// 图片格式
            intent.putExtra("noFaceDetection", true);// 取消人脸识别
            intent.putExtra("return-data", true);
            // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
            LogUtils.d(TAG,"[crop] 222");
            startActivityForResult(intent, PHOTO_REQUEST_CUT);
        } else {
            //权限拒绝 申请权限
            EasyPermissions.requestPermissions(ModifyInfoActivity.this, "", 0x26, "android.permission.READ_EXTERNAL_STORAGE");
        }*/


    }


    public void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加图片");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogUtils.d(TAG,"which="+which);
                switch (which) {
                    case PHOTO_REQUEST_GALLERY:
                        if (EasyPermissions.hasPermissions(ModifyInfoActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
                            //具备权限 直接进行操作
                            // 选择本地照片
                            Intent intent1 = new Intent(Intent.ACTION_PICK);
                            intent1.setType("image/*");
                            startActivityForResult(intent1, PHOTO_REQUEST_GALLERY);
                        } else {
                            //权限拒绝 申请权限
                            EasyPermissions.requestPermissions(ModifyInfoActivity.this, "", 0x24, "android.permission.WRITE_EXTERNAL_STORAGE");
                        }
                       /* // 选择本地照片
                        Intent intent1 = new Intent(Intent.ACTION_PICK);
                        intent1.setType("image/*");
                        startActivityForResult(intent1, PHOTO_REQUEST_GALLERY);*/
                        break;
                    case PHOTO_REQUEST_CAREMA:
                        if (EasyPermissions.hasPermissions(ModifyInfoActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
                            //具备权限 直接进行操作
                            // 拍照
                            if (EasyPermissions.hasPermissions(ModifyInfoActivity.this, "android.permission.CAMERA")) {
                                //具备权限 直接进行操作
                                takePhotoByCamera();
                            } else {
                                //权限拒绝 申请权限
                                EasyPermissions.requestPermissions(ModifyInfoActivity.this, "", 0x23, "android.permission.CAMERA");
                            }
                        } else {
                            //权限拒绝 申请权限
                            EasyPermissions.requestPermissions(ModifyInfoActivity.this, "", 0x25, "android.permission.WRITE_EXTERNAL_STORAGE");
                        }


                        break;
                }
            }
        });
        builder.show();
    }

    private void takePhotoByCamera(){
        Intent openCameraIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sxy/";
            File outputFile = new File(filePath,"temp_image.jpeg");
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdir();
            }
            LogUtils.d(TAG,"[takePhotoByCamera] outputFile="+outputFile.getAbsolutePath());
            fileUrl = outputFile.getAbsolutePath();
            tempUri = FileProvider.getUriForFile(ModifyInfoActivity.this,
                    "com.sxy.healthcare.fileprovider",
                    outputFile);
            openCameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            openCameraIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            tempUri = Uri.fromFile(new File(Environment
                    .getExternalStorageDirectory(), "temp_image.jpeg"));

        }

        // 将拍照所得的相片保存到SD卡根目录
        LogUtils.d(TAG,"[takePhotoByCamera] tempUri="+tempUri.toString());
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        startActivityForResult(openCameraIntent, PHOTO_REQUEST_CAREMA);
    }



    /**
     * RxBus
     */
  /*  private void operateBus() {
      birthDis =  RxBus.get().toFlowable()
                .map(new Function<Object,BirthEvent>() {
                    @Override
                    public BirthEvent apply(@NonNull Object o) throws Exception {
                        return (BirthEvent) o;
                    }

                })
                .subscribe(new Consumer<BirthEvent>() {
                    @Override
                    public void accept(@NonNull BirthEvent birthEvent) throws Exception {
                        if (birthEvent != null) {
                            tvBirth.setText(birthEvent.getBirth());
                        }
                    }

                });
    }
*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        destroyLoginDis();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(BirthEvent birthEvent) {
        tvBirth.setText(birthEvent.getBirth());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @android.support.annotation.NonNull String[] permissions, @android.support.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @android.support.annotation.NonNull List<String> perms) {
        if (requestCode == 0x23) {
            takePhotoByCamera();
        }else  if(requestCode==0x24){
            Intent intent1 = new Intent(Intent.ACTION_PICK);
            intent1.setType("image/*");
            startActivityForResult(intent1, PHOTO_REQUEST_GALLERY);
        }else if(requestCode==0x25){
            // 拍照
            if (EasyPermissions.hasPermissions(ModifyInfoActivity.this, "android.permission.CAMERA")) {
                //具备权限 直接进行操作
                takePhotoByCamera();
            } else {
                //权限拒绝 申请权限
                EasyPermissions.requestPermissions(ModifyInfoActivity.this, "", 0x23, "android.permission.CAMERA");
            }
        }else if(requestCode==0x26){

            // 裁剪图片意图
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(mURI, "image/*");
            intent.putExtra("crop", "true");
            // 裁剪框的比例，1：1
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            // 裁剪后输出图片的尺寸大小
            intent.putExtra("outputX", 250);
            intent.putExtra("outputY", 250);

            intent.putExtra("outputFormat", "JPEG");// 图片格式
            intent.putExtra("noFaceDetection", true);// 取消人脸识别
            intent.putExtra("return-data", true);
            // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
            LogUtils.d(TAG,"[crop] 222");
            startActivityForResult(intent, PHOTO_REQUEST_CUT);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @android.support.annotation.NonNull List<String> perms) {
        if (requestCode == 0x23) {

        }
    }
}
