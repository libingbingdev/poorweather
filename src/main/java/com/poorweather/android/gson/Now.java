package com.poorweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * "now":
 *      {"cloud":"25","cond_code":"101","cond_txt":"多云","fl":"29","hum":"61","pcpn":"0.0","pres":"1006",
 *      "tmp":"29","vis":"6","wind_deg":"97","wind_dir":"东风","wind_sc":"3","wind_spd":"17",
 *      "cond":
 *          {"code":"101","txt":"多云"}},
 */
public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
