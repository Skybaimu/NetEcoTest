package com.yzsj.net.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.yzsj.net.config.ConfigUtil;
import com.yzsj.net.utils.OpenId;
import com.yzsj.neteco.util.QueryHttpsResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzsj.net.config.WMK;
import com.yzsj.net.utils.HttpClientUtils;
import com.yzsj.net.utils.POIUtil;
import com.yzsj.net.utils.RedisManager;
import org.springframework.web.client.RestTemplate;

@Component
@EnableScheduling
public class RemoteServer {
	public Header openHeader = null;
	public  Map<String, String> INFO = new HashMap<>();
/*
	@Autowired
	public WMK config;
*/
   @Autowired
	ConfigUtil config;

	@Autowired
	private RedisManager redisManager;

//	@PostConstruct
	public void loadTag() {
		try {
			String path = "D:\\excelTest.xlsx";
			List<String[]> list = POIUtil.readExcel(path);
			String path1 = new File("").getAbsolutePath();

			System.out.println(path1);
			Map<String ,String> map = new HashMap<>();
			for (String[] strings: list) {
				INFO.put(strings[0],strings[1]);
			}

		} catch (Exception e) {
		}
	}

	/**
	 * 根据ip 端口 url获取openId
	 * 
	 * @return
	 */
	@PostConstruct
	public void setOpenId() {
		String url = "https://" + config.getIp() + ":" + config.getPort() + "/rest/openapi/sm/session";
		try {
			List<NameValuePair> data = new ArrayList<NameValuePair>();
			data.add(new BasicNameValuePair("userid", config.getUserid()));
			data.add(new BasicNameValuePair("value", config.getPassword()));
			data.add(new BasicNameValuePair("ipaddress", config.getIpAddress()));
//			String result = HttpClientUtils.put(url, data);
//			String result = new OpenId().getOpenId(url,config.getPort(),data);
			/*JSONObject obj = toJson(result);
			if (result != null) {
				if ("0".equals(obj.getString("code"))) {
					openHeader = new BasicHeader("openid", obj.getString("data"));
				}

			}*/
			openHeader = new BasicHeader("openid", "f7951fcae422849948e7d9f3ff0c38465c9359ccbcb518b7");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Scheduled(initialDelay = 10000, fixedDelay = 5000)
	public void publishData() {
		if (openHeader == null) {
			setOpenId();
		}
		Map<String, String> retMap = getPmdata();
		String url="http://127.0.0.1:8086/write?db=uchdlog";

		String pointValue="kpi TAG012=2143,TAG001";
		String time = " 1434059023012571211";

		RestTemplate restTemplate=new RestTemplate();
		for(int i = 1 ;i < 20 ; i ++){
			String data = pointValue+i +"=13,TAG0031"+ i + "=" + i +",TAG9"+ i +"=13"+ i  +",TAG77"+ i +"=199"+ i + time;
			restTemplate.postForObject(url,data,Object.class);
		}
		String influxData = "";
		for (Map.Entry<String, String> entry : retMap.entrySet()) {
			String publishValue = "{\"id\":" + entry.getKey() + ",\"f\":0,\"p\":2,\"t\":0,\"v\":" + entry.getValue()+ "}";
			redisManager.publish("chan:from_all_to_uckernal:value", publishValue);

		}
	}

	public Map<String, String> getPmdata() {
		if (openHeader == null) {
			setOpenId();
		}
		Map<String, String> map = new HashMap<String, String>();
		try {
			for (int i = 0; true; i++) {
				/*List<BasicNameValuePair> headers = new ArrayList<BasicNameValuePair>();
				headers.add(new BasicNameValuePair("openid", "b800b1fd46671cd7370a64baa58434437e0566205db37351"));
				headers.add(new BasicNameValuePair("params", "{\"pageIndex\":" + 1 + ",\"pageSize\":" + 4000 + "}"));*/
				List<Header> headers = new ArrayList<Header>();
				headers.add(openHeader);
				headers.add(new BasicHeader("params", "{\"pageIndex\":" + i + ",\"pageSize\":" + 4000 + "}"));
				String url = "https://" + config.getIp() + ":" + config.getPort() + "/rest/openapi/neteco/pmdata";
				String result = HttpClientUtils.get(url, null, headers);
				/*String KPIData =  new QueryHttpsResult().
						getHttpsResult(config.getPort(),url + "/rest/openapi/neteco/pmdata",headers,null,"get");*/

				JSONArray jsonArray = toJson(result).getJSONArray("data");
				for (int j = 0; j < jsonArray.size(); j++) {
					JSONObject obj = (JSONObject) jsonArray.get(j);
					////// 未完成
					map.put(obj.getString("dn") + obj.getString("counterId"),obj.getString("counterValue"));
				}
				if (StringUtils.isBlank(result)) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public static JSONObject toJson(String data) {
		if (StringUtils.isBlank(data)) {
			return null;
		}
		return JSON.parseObject(data);
	}
}
