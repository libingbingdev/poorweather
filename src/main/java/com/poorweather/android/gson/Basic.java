package com.poorweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    /**
     * 使用@SerializedName注解来将Java对象里的属性跟json里字段对应值匹配起来
     * {"basic":
     *      {"cid":"CN101180104","location":"登封","parent_city":"郑州","admin_area":"河南","cnty":"中国",
     *      "lat":"34.45993805","lon":"113.0377655","tz":"+8.00","city":"登封","id":"CN101180104",
     *      "update":
     *          {"loc":"2018-08-29 18:46","utc":"2018-08-29 10:46"}}
     */
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }

    public Update update;

}
