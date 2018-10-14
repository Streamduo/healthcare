package com.sxy.healthcare.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.sxy.healthcare.base.AppConfig;


/**
 * @author ※简单※
 * @Description: SharedPreferences工具类
 * @date 2016-11-15 15:40
 */


public class SharedPrefsUtil {

    private static final String TAG = SharedPrefsUtil.class.getSimpleName();

    /**
     * SharedPrefsUtil 实例
     */
    private static SharedPrefsUtil mInstance;

    /**
     * SharedPreferences 实例
     */
    private SharedPreferences mSharedPreferences;


    /**
     * 第一次使用
     */
    public static final String FIRST_USE = "firstUse";


    private SharedPrefsUtil(Context context) {
        mSharedPreferences = context.getSharedPreferences(AppConfig.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    /**
     * @param context
     * @return SharedPreferencesUtil
     * @throws
     * @Title: getInstance
     * @Description: 获取SharedPrefsUtil的一个实例
     */
    public static synchronized SharedPrefsUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefsUtil(context);
        }
        return mInstance;
    }

    /**
     * @param key
     * @param value
     * @return void
     * @throws
     * @Title: setString
     * @Description: 添加String类型的数据
     */
    public void setString(String key, String value) {
        putObject(key, value);
    }

    /**
     * @param key
     * @param defValue
     * @return String
     * @throws
     * @Title: getString
     * @Description: 获取String类型的数据
     */
    public String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    /**
     * @param key
     * @param value
     * @return void
     * @throws
     * @Title: setBoolean
     * @Description: 添加boolean类型的数据
     */
    public void setBoolean(String key, boolean value) {
        putObject(key, value);
    }

    /**
     * @param key
     * @param defValue
     * @return boolean
     * @throws
     * @Title: getBoolean
     * @Description: 获取boolean类型的数据
     */
    public boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    /**
     * @param key
     * @param value
     * @return void
     * @throws
     * @Title: setInt
     * @Description: 添加int型数据
     */
    public void setInt(String key, int value) {
        putObject(key, value);
    }

    /**
     * @param key
     * @param defValue
     * @return int
     * @throws
     * @Title: getInt
     * @Description: 获取int型数据
     */
    public int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    /**
     * @param key
     * @param value
     * @return void
     * @throws
     * @Title: setLong
     * @Description: 添加int型数据
     */
    public void setLong(String key, long value) {
        putObject(key, value);
    }

    /**
     * @param key
     * @param defValue
     * @return int
     * @throws
     * @Title: getLong
     * @Description: 获取int型数据
     */
    public long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    /**
     * @param key
     * @param value
     * @return void
     * @throws
     * @Title: putObject
     * @Description: 添加数据
     */
    private void putObject(String key, Object value) {
        if (value != null) {
            Editor editor = mSharedPreferences.edit();
            if (mSharedPreferences.contains(key)) {
                editor.remove(key);
            }

            if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Float) {
                editor.putFloat(key, (Float) value);
            } else if (value instanceof Integer) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof Long) {
                editor.putLong(key, (Long) value);
            } else if (value instanceof String) {
                editor.putString(key, (String) value);
            }

            editor.apply();
        }
    }

    /**
     * @return void
     * @throws
     * @Title: clearAll
     * @Description: 清除所有数据
     */
    public void clearAll() {
        Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * @param key
     * @return void
     * @throws
     * @Title: removeByKey
     * @Description: 根据key清除对应的数据
     */
    public void removeByKey(String key) {
        Editor editor = mSharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

}
