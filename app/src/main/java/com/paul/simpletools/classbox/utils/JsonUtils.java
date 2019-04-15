package com.paul.simpletools.classbox.utils;


import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Liu ZhuangFei on 2018/8/24.
 */
public class JsonUtils {
    public static String createJsonData(){
        String json="{\"agent\":\"Mozilla/5.0%20(Linux%20Android%205.1.1%20OPPO%20A33%20Build/LMY47V%20wv)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Version/4.0%20Chrome/42.0.2311.138%20Mobile%20Safari/537.36%20classbox2/10.2.2\",\"app_version\":\"10.2.2\",\"brand\":\"OPPO\",\"channel\":\"local\",\"client_id\":\"d2a914e230d5339e7edff25d7609b6af\",\"network\":\"wifi\",\"platform\":\"android\",\"unit_type\":\"OPPO%20A33\",\"request_time\":\"1535086479\"}";
        return json;
    }

    public static String getStringFromObj(JSONObject obj,String key,String def){
        if(obj.has(key)) {
            try {
                return obj.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
                return def;
            }
        }
        return def;
    }

    public static int getIntFromObj(JSONObject obj,String key,int def){
        if(obj.has(key)) {
            try {
                return obj.getInt(key);
            } catch (JSONException e) {
                e.printStackTrace();
                return def;
            }
        }
        return def;
    }
}
