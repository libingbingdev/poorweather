package com.poorweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * "aqi":
 *      {"city":
 *          {"aqi":"78","pm25":"50","qlty":"良"}},
 */
public class AQI {
    @SerializedName("city")
    public AQICity aqiCity;

    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
