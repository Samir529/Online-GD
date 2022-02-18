package edu.ewubd.cse489_project;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@SuppressWarnings("ALL")
public class JSONParser {

	private String TAG = "JSONParser";
	private static JSONParser instance = new JSONParser();
	private JSONParser() {}
	public static JSONParser getInstance() {
		return instance;
	}

	public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {

		HttpURLConnection http = null;
		InputStream is = null;
		JSONObject jObj = null;
		String json = "";
		// Making HTTP request
		try {
			// check for request method
			if (method == "POST") {
				//httpClient = new DefaultHttpClient();
				if(params != null) {
					String paramString = URLEncodedUtils.format(params, "utf-8");
					url += "?" + paramString;
				}
			}
			System.out.println("@JSONParser-"+": "+ url);
			URL urlc = new URL(url);
			http = (HttpURLConnection) urlc.openConnection();
			//System.out.println("Here 2");
			http = (HttpURLConnection) urlc.openConnection();
			//System.out.println("Here 3");
			http.connect();
			is = http.getInputStream();
			//System.out.println("Here 4");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
			System.out.println(json);
			jObj = new JSONObject(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			http.disconnect();
		} catch (Exception e) {
		}
		return jObj;
	}
}
