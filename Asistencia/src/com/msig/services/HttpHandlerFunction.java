package com.msig.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpHandlerFunction {
	
	public String post(String URL, String function, String values){
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);
		
		//parameter for the web service
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("P_FUNCTION", function));
		parameters.add(new BasicNameValuePair("P_PARAMETERS", values));
		
		try {
			
			//send parameter to URL from main
			httpPost.setEntity(new UrlEncodedFormEntity(parameters));
			HttpResponse httpRespose = httpClient.execute(httpPost);
			
			//get response from URL Web Service 
			HttpEntity httpEntity = httpRespose.getEntity();
			String response = EntityUtils.toString(httpEntity);
			return response;
			
		} catch (Exception exception) {
			return "ERROR: "+URL+" NO DISPONIBLE" + exception.toString();
		}
	}
}
