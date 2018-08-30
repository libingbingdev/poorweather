package com.poorweather.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.poorweather.android.db.City;
import com.poorweather.android.db.County;
import com.poorweather.android.db.Province;
import com.poorweather.android.util.HttpUtil;
import com.poorweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 遍历省市县数据的碎片布局
 */
public class ChoolseAreaFragment extends Fragment {
    private static final String TAG = "PoorWeather_ChoolseAreaFragment";
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;
    private static final String TYPE_PROVINCE = "province";
    private static final String TYPE_CITY = "city";
    private static final String TYPE_COUNTY = "county";
    private static final String AREA_URL = "http://guolin.tech/api/china/";
    /**
     * 替代ProgressDialog,sdk大于等于26
     */
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    /**
     * 省列表
     */
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    /**
     * 被选中的省份
     */
    private Province selectedProvince;
    private City selectedCity;
    /**
     * 当前的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated()");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "listView.onItemClick,currentLevel = " + currentLevel);
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "backButton.onItemClick,currentLevel = " + currentLevel);
                if (currentLevel == LEVEL_CITY) {
                    queryProvices();
                } else if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                }
            }
        });
        queryProvices();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvices() {
        Log.d(TAG, "queryProvices()");
        titleText.setText(R.string.china);
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        Log.d(TAG, "provinceList.size() = " + provinceList.size());
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList
                    ) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = AREA_URL;
            queryFromServer(address, TYPE_PROVINCE);
        }
    }

    /**
     * 查询省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        Log.d(TAG, "queryCities()");
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",
                String.valueOf(selectedProvince.getId())).find(City.class);
        Log.d(TAG, "cityList.size() = " + cityList.size());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList
                    ) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = AREA_URL + provinceCode;
            queryFromServer(address, TYPE_CITY);
        }
    }

    /**
     * 查询选择市内所有的县，优先从数据库查询，如果没有查询到再去服务器查询
     */
    private void queryCounties() {
        Log.d(TAG, "queryCounties()");
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?",
                String.valueOf(selectedCity.getId())).find(County.class);
        Log.d(TAG, "countyList.size() = " + countyList.size());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList
                    ) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            Log.d(TAG, selectedProvince.getProvinceCode() + "/" + selectedCity.getCityCode());
            String address = AREA_URL + provinceCode + "/" + cityCode;
            queryFromServer(address, TYPE_COUNTY);
        }
    }

    /**
     * 根据传入的地址和类型从服务器获取省市县数据
     *
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {
        showProcessDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //通过runInUiThread回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProcessDialog();
                        Toast.makeText(getContext(), getString(R.string.load_fail), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "type = " + type.toString());
                String responseText = response.body().string();
                boolean result = false;
                if (TYPE_PROVINCE.equals(type)) {
                    result = Utility.handleProvincesResponse(responseText);
                } else if (TYPE_CITY.equals(type)) {
                    result = Utility.handleCitiesResponse(responseText, selectedProvince.getId());
                } else if (TYPE_COUNTY.equals(type)) {
                    result = Utility.handleCountiesResponse(responseText, selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProcessDialog();
                            if (TYPE_PROVINCE.equals(type)) {
                                queryProvices();
                            } else if (TYPE_CITY.equals(type)) {
                                queryCities();
                            } else if (TYPE_COUNTY.equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProcessDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProcessDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
