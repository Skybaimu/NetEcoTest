package com.yzsj.net.service;

import java.io.File;
import java.io.IOException;
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
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	public  Map<String, String> pmMap = new HashMap<String, String>();
	private static Logger log = LoggerFactory.getLogger(RedisManager.class);

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
			String path = "D:\\TAGId1.xlsx";
			List<String[]> list = POIUtil.readExcel(path);
			for (String[] strings: list) {
				INFO.put(strings[1],strings[2]);
			}
		} catch (Exception e) {
			log.error("读取excel表错误： " +e.getMessage(),e);
		}
	}

	/**
	 * 根据ip 端口 url获取openId
	 * 
	 * @return
	 */
//	@PostConstruct
	public void setOpenId() {
		String url = "https://" + config.getIp() + ":" + config.getPort() + "/rest/openapi/sm/session";
		try {
			List<NameValuePair> data = new ArrayList<NameValuePair>();
			data.add(new BasicNameValuePair("userid", config.getUserid()));
			data.add(new BasicNameValuePair("value", config.getPassword()));
			data.add(new BasicNameValuePair("ipaddress", config.getIpAddress()));
			String result = HttpClientUtils.put(url, data);
			JSONObject obj = toJson(result);
			if (result != null) {
				if ("0".equals(obj.getString("code"))) {
					openHeader = new BasicHeader("openid", obj.getString("data"));
				}
			}
//			openHeader = new BasicHeader("openid", "28d52c6b2deaeb7d9d17fab23a5dc32299e3bc961b791bf7");
//			test();
		} catch (Exception e) {
			log.error("获取openId错误：" + e.getMessage(),e);
			e.printStackTrace();
		}
	}


	/**
	 * 定时任务按时间获取数据并推送
	 * */
	@Scheduled(initialDelay = 5000, fixedDelay = 120000)
	public void publishData() {

		int a1 = (int)(Math.random() * 1078456456);
		int a2 = (int)(Math.random() * 1132155654);
		String publishValue = "[{\"ackTime\": null,\"ackUser\": null,\"acked\": false,\"additionalInformation\": null,\"additionalText\": null,\"alarmId\": 412348043,\"alarmName\": \"异常 (2QF1)\",\"alarmSN\": " + a1 + ",\"arrivedTime\": 1541396062345,\"clearUser\": null,\"cleared\": false,\"clearedTime\": null,\"clearedType\": 1,\"commentTime\": 0,\"commentUser\": \"\",\"comments\": \"\",\"devCsn\": 281,\"eventTime\": 1541396062000,\"eventType\": 4,\"latestLogTime\": 1541396062000,\"moDN\": \"NE=33554456.41234-28162\",\"moName\": \"配电柜_PDU8000_01044_BIN4[01]\",\"neDN\": \"NE=33554456.41234-28162\",\"neName\": \"配电柜_PDU8000_01044_BIN4[01]\",\"neType\": \"{PDU}HUAWEI_PDU8000_Bin\",\"objectInstance\": \"/北京市城市副中心/二楼机房/二楼机房模块1,管理域名称=二楼机房模块1，ECC IP=10.200.2.204，设备名称=配电柜_PDU8000_01044_BIN4[01]\",\"perceivedSeverity\": 4,\"probableCause\": null,\"proposedRepairActions\": null,\"probableCauseStr\": \"\",\"alarmGroupId\": 4123}, {\"ackTime\": null,\"ackUser\": null,\"acked\": false,\"additionalInformation\": null,\"additionalText\": null,\"alarmId\": 410097013,\"alarmName\": \"回风低温告警\",\"alarmSN\": "+ a2 + ",\"arrivedTime\": 1541401923636,\"clearUser\": null,\"cleared\": true,\"clearedTime\": 1541660205000,\"clearedType\": 1,\"commentTime\": 0,\"commentUser\": \"\",\"comments\": \"\",\"devCsn\": 282,\"eventTime\": 1541401923000,\"eventType\": 4,\"latestLogTime\": 1541401923000,\"moDN\": \"NE=33554456.41009-23556\",\"moName\": \"空调_NetCol5000-A025_01044_BIN4[03]\",\"neDN\": \"NE=33554456.41009-23556\",\"neName\": \"空调_NetCol5000-A025_01044_BIN4[03]\",\"neType\": \"{CARC}HUAWEI_NetCol5000-A025_Bin\",\"objectInstance\": \"/北京市城市副中心/二楼机房/二楼机房模块1,管理域名称=二楼机房模块1，ECC IP=10.200.2.204，设备名称=空调_NetCol5000-A025_01044_BIN4[03]\",\"perceivedSeverity\": 4,\"probableCause\": null,\"proposedRepairActions\": null,\"probableCauseStr\": \"\",\"alarmGroupId\": 41009}]";
		redisManager.publish("chan:mic_module_alarm", publishValue);
		/*if (openHeader == null) {
			setOpenId();
		}
		Map<String, String> retMap = getPmdata();
		if(retMap.containsKey(null))retMap.remove(null);
		for (Map.Entry<String, String> entry : retMap.entrySet()) {
			String publishValue = "{\"id\":" + entry.getKey() + ",\"f\":0,\"p\":2,\"t\":0,\"v\":" + entry.getValue()+ "}";
			redisManager.publish("chan:from_all_to_uckernal:value", publishValue);

		}*/
	}
/**
 * 获取实时指标数据
 * */
	public Map<String, String> getPmdata() {
		if (openHeader == null) {
			setOpenId();
		}
		if(INFO.isEmpty())loadTag();
		Map<String, String> map = new HashMap<String, String>();
		try {
			for (int i = 0; true; i++) {
				List<Header> headers = new ArrayList<Header>();
				headers.add(openHeader);
				headers.add(new BasicHeader("params", "{\"pageIndex\":" + i + ",\"pageSize\":" + 4000 + "}"));
				String url = "https://" + config.getIp() + ":" + config.getPort() + "/rest/openapi/neteco/pmdata";
				String result = HttpClientUtils.get(url, null, headers);
				JSONObject resultObj = toJson(result);
				if(resultObj.get("code").equals("1204")){
					log.info("openId已失效");
					setOpenId();
				}
				JSONArray jsonArray = resultObj.getJSONArray("data");
				for (int j = 0; j < jsonArray.size(); j++) {
					JSONObject obj = (JSONObject) jsonArray.get(j);
					map.put(INFO.get(obj.getString("dn")+obj.getString("counterId")),obj.getString("counterValue"));
				}
				if (StringUtils.isBlank(result)) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("获取实时指标数据异常或已完整获取数据" +e.getMessage(),e);
		}
		return map;
	}

	public static JSONObject toJson(String data) {
		if (StringUtils.isBlank(data)) {
			return null;
		}
		return JSON.parseObject(data);
	}



	@Test
	public  void test(){
		if (openHeader == null) {
			setOpenId();
		}
		Map<String, String> map = new HashMap<String, String>();
		File file = new File("d:/实时指标1.txt");
		String finalData = "";
		try {

			for (int i = 0; true; i++) {
				List<Header> headers = new ArrayList<Header>();
				headers.add(openHeader);
				headers.add(new BasicHeader("params", "{\"pageIndex\":" + i + ",\"pageSize\":" + 4000 + "}"));
				String url = "https://" + config.getIp() + ":" + config.getPort() + "/rest/openapi/neteco/pmdata";
				String result = HttpClientUtils.get(url, null, headers);
				JSONArray jsonArray = toJson(result).getJSONArray("data");
				for (int j = 0; j < jsonArray.size(); j++) {
					JSONObject obj = (JSONObject) jsonArray.get(j);
					////// 未完成
					finalData += obj.getString("dn") + "@" + obj.getString("counterId") + "@" +
									obj.getString("counterValue") + "@" +obj.getString("counterUnit") +
										"@" +obj.getString("functionSubsetId") + "@" +obj.getString("resultTime") + "@" +
												obj.getString("period")  +"\r\n";
//					map.put(obj.getString("id") , obj.getString("groupName") );
				}
				if (StringUtils.isBlank(result)) {
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			org.apache.commons.io.FileUtils.writeStringToFile(file,finalData );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
