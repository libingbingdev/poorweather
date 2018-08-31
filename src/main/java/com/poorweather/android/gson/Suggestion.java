package com.poorweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * "suggestion":
 *      {"comf":
 *          {"type":"comf","brf":"较舒适","txt":"今天夜间天气阴沉，同时较大的空气湿度，会使您感到有点儿闷热，但大部分人还是完全可以接受。"},
 *      "sport":
 *          {"type":"sport","brf":"较不宜","txt":"阴天，且天气较热，请减少运动时间并降低运动强度。"},
 *      "cw":
 *          {"type":"cw","brf":"较适宜","txt":"较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。"}}
 */
public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    public Sport sport;
    @SerializedName("cw")
    public CarWash carWash;

    public class Comfort {//舒适度
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("txt")
        public String info;
    }
}
