package cn.ichiva.luckysheet.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class LuckySheetHelper {

    public static void getAdmin(String json){
        JSONObject jsonObject = JSON.parseObject(json);
        JSONArray data = jsonObject.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            JSONArray celldata = data.getJSONObject(i).getJSONArray("celldata");
        }
    }

}
