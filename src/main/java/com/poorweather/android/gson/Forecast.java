package com.poorweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * "daily_forecast":[
 *      {"date":"2018-08-29",
 *       "cond":{"txt_d":"多云"},
 *       "tmp":{"max":"31","min":"23"}
 *       },
 *      {"date":"2018-08-30",
 *       "cond":{"txt_d":"阴"},
 *       "tmp":{"max":"28","min":"21"}
 *      },
 *      {"date":"2018-08-31",
 *      "cond":{"txt_d":"阴"},
 *      "tmp":{"max":"26","min":"21"}
 *      }
 * ]
 */
public class Forecast {
    public String date;
    public Cond cond;
    @SerializedName("tmp")
    public Temperature temperature;
    public class Cond{
        @SerializedName("txt_d")
        public String info;
    }
    public class Temperature{
        public String max;
        public String min;
    }
}
