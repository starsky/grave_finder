package pl.itiner.fetch;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import pl.itiner.model.Departed;
import pl.itiner.model.DepartedFactory;
import android.net.Uri;
import android.util.Log;

public class WroclawDataHandler {

	private final QueryParams params;
	private String jSessionId;

	public WroclawDataHandler(QueryParams params) {
		this.params = params;
	}

	public List<? extends Departed> executeQuery() throws IOException {
		jSessionId = getJSessionId();
		String postData = postData();
		return DepartedFactory.parseElements(postData);
	}

	public HttpEntity getDetails(String partOfURL, String jSessionId) {
		HttpEntity entity = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://iwroclaw.pl" + partOfURL);
		try {
			httpget.setHeader("Cookie", "JSESSIONID=" + jSessionId);
			HttpResponse response = httpclient.execute(httpget);
			entity = response.getEntity();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}

	public String getJSessionId() {
		String jSessionId = null;
		HttpResponse response = null;
		try {
			response = HttpDownloadTask.getHttpResponse(Uri
					.parse("http://iwroclaw.pl/memento/app/search"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		jSessionId = response.getFirstHeader("Set-Cookie").getValue();
		jSessionId = jSessionId.replace("JSESSIONID=", "");
		jSessionId = jSessionId.substring(0, jSessionId.indexOf(";"));
		Log.i("JSESSIONID", jSessionId);
		return jSessionId;
	}

	public HttpGet moreResult(String jSessionId, int size) {
		HttpGet httpget = new HttpGet(
				"http://iwroclaw.pl/memento/app/list?p=1&s=" + size);
		httpget.setHeader("Cookie", "JSESSIONID=" + jSessionId);

		return httpget;
	}

	public String postData() {
		// Create a new HttpClient and Post Header
		String responseBody = null;
		HttpPost httppost = new HttpPost(
				"http://iwroclaw.pl/memento/app/search");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("lastName", params
				.getSurename()));
		nameValuePairs
				.add(new BasicNameValuePair("firstName", params.getName()));
		nameValuePairs.add(new BasicNameValuePair("birthDate", ""));
		nameValuePairs.add(new BasicNameValuePair("burialDate", ""));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		httppost.setHeader("Cookie", "JSESSIONID=" + jSessionId);
		httppost.getParams().setParameter(
				CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		HttpDownloadTask.sendPostRequest(httppost);
		HttpResponse moreResultEntity = HttpDownloadTask
				.sendGetRequest(moreResult(jSessionId, 100));

		if (moreResultEntity != null) {
			try {
				responseBody = EntityUtils.toString(moreResultEntity
						.getEntity());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return responseBody;

	}

}
