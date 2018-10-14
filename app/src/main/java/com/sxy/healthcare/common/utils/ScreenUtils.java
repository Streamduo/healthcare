package com.sxy.healthcare.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * @Description: 屏幕操作工具类
 * @author ※简单※
 * @date  2016-11-10 15:40
 */

public class ScreenUtils {
	
	private ScreenUtils()  
    {  
        /* cannot be instantiated */  
        throw new UnsupportedOperationException("cannot be instantiated");
    }  
  
    /** 
     * 获得屏幕高度 
     *  
     * @param context 
     * @return 
     */  
    public static int getScreenWidth(Context context)
    {  
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);  
        return outMetrics.widthPixels;

    }  
  
    /** 
     * 获得屏幕宽度 
     *  
     * @param context 
     * @return 
     */  
    public static int getScreenHeight(Context context)
    {  
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);  
        return outMetrics.heightPixels;
    }  
  
    /** 
     * 获得状态栏的高度 
     *  
     * @param context 
     * @return 
     */
    /**
     * 获取状态栏的高
     */
    public static int getStatusBarHeight(Activity context) {
        Rect frame = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        if(0 == statusBarHeight) {
            statusBarHeight = getStatusBarHeightByReflection(context);
        }
        return statusBarHeight;
    }

    public static int getStatusBarHeightByReflection(Context context) {
        Class<?> c;
        Object obj;
        Field field;
        // 默认为38，貌似大部分是这样的
        int x, statusBarHeight = 38;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }
  
    /** 
     * 获取当前屏幕截图，包含状态栏 
     *  
     * @param activity 
     * @return 
     */  
    public static Bitmap snapShotWithStatusBar(Activity activity)
    {  
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);  
        view.buildDrawingCache();  
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);  
        int height = getScreenHeight(activity);  
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();  
        return bp;  
  
    }  
  
    /** 
     * 获取当前屏幕截图，不包含状态栏 
     *  
     * @param activity 
     * @return 
     */  
    public static Bitmap snapShotWithoutStatusBar(Activity activity)
    {  
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);  
        view.buildDrawingCache();  
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
        int statusBarHeight = frame.top;  
  
        int width = getScreenWidth(activity);  
        int height = getScreenHeight(activity);  
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);  
        view.destroyDrawingCache();  
        return bp;  
  
    }


    public static float dip2px(float dipValue)
    {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return  (dipValue * scale + 0.5f);
    }
}
