package pl.itiner.fetch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Connection.Response;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Build;

final class HttpDownloadTask {

	private static final String USER_AGENT = "Grave-finder (www.itiner.pl)";

	private HttpDownloadTask() {

	}

	@SuppressLint("NewApi")
	static String getResponse(Uri uri) throws IOException {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			AndroidHttpClient client = AndroidHttpClient
					.newInstance(USER_AGENT);
			OutputStream os = new ByteArrayOutputStream();
			try {
				HttpResponse resp = client.execute(new HttpGet(uri.toString()));
				resp.getEntity().writeTo(os);
			} finally {
				client.close();
				os.close();
			}
			return os.toString();
		} else {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(uri.toString());
			request.setHeader("User-Agent", USER_AGENT);
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() != 200) {
				throw new IOException("Invalid response from server: "
						+ status.toString());
			}
			HttpEntity entity = response.getEntity();
			InputStream inputStream = entity.getContent();
			ByteArrayOutputStream content = new ByteArrayOutputStream();
			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}
			return new String(content.toByteArray());
		}
	}
	static HttpResponse getHttpResponse(Uri uri) throws IOException {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			AndroidHttpClient client = AndroidHttpClient
					.newInstance(USER_AGENT);
			HttpResponse resp;
			OutputStream os = new ByteArrayOutputStream();
			try {
				resp = client.execute(new HttpGet(uri.toString()));
				resp.getEntity().writeTo(os);
			} finally {
				client.close();
				os.close();
			}
			return resp;
		} else {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(uri.toString());
			request.setHeader("User-Agent", USER_AGENT);
			HttpResponse response = client.execute(request);
			return response;
		}
	}
	
	public static HttpResponse sendPostRequest(HttpPost httpPost)
	{
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		try {
			response = client.execute(httpPost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
		
	}
	
	public static HttpResponse sendGetRequest(HttpGet httpGet)
	{
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = null;
		try {
			response = client.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
		
	}
	

}
