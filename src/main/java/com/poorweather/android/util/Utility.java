package com.poorweather.android.util;

import android.text.TextUtils;
import android.util.Log;

import com.poorweather.android.db.City;
import com.poorweather.android.db.County;
import com.poorweather.android.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    private static final String TAG = "PoorWeather_Utility";

    /**
     * 解析和处理服务器返回的省级数据
     *
     * @param response
     * @return
     */
    public static boolean handleProvincesResponse(String response) {
        Log.d(TAG, "handleProvincesResponse,response = " + TextUtils.isEmpty(response));
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     *
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCitiesResponse(String response, int provinceId) {
        Log.d(TAG, "handleCitiesResponse,response = " + TextUtils.isEmpty(response));
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allcities = new JSONArray(response);
                for (int i = 0; i < allcities.length(); i++) {
                    JSONObject cityObject = allcities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceid(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理和解析服务器返回的县级数据
     *
     * @param response
     * @param cityId
     * @return
     */
    public static boolean handleCountiesResponse(String response, int cityId) {
        Log.d(TAG, "handleCountiesResponse,response = " + TextUtils.isEmpty(response));
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
