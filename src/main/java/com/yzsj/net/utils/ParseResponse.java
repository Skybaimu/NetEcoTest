package com.yzsj.net.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ParseResponse {
    public  Map<String, String> getParseResponse(String input)
    {
        Map<String, String> retMap = new HashMap<String, String>();
        JSONObject jObject = JSONObject.parseObject(input);
        if (null == jObject)
        {
            return retMap;
        }
        if (null != jObject.get("code"))
        {
            String i = jObject.get("code").toString();
            retMap.put("code", i);
        }
        if (null != jObject.get("data"))
        {
            String data = jObject.get("data").toString();
            retMap.put("data", data.toString());
        }
        if (null != jObject.get("description"))
        {
            String des = jObject.get("description").toString();
            retMap.put("description", des.toString());
        }
        if (null != jObject.get("currentPage"))
        {
            String currentPage = jObject.get("currentPage").toString();
            retMap.put("currentPage", currentPage.toString());
        }
        if (null != jObject.get("totalPage"))
        {
            String totalPage = jObject.get("totalPage").toString();
            retMap.put("totalPage", totalPage.toString());
        }
        if (null != jObject.get("pageSize"))
        {
            String pageSize = jObject.get("pageSize").toString();
            retMap.put("pageSize", pageSize.toString());
        }
        return retMap;
    }

}
