package pl.itiner.fetch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Build;

final class HttpDownloadTask  {

	private static final String USER_AGENT = "Grave-finder (www.itiner.pl)";

	
	private HttpDownloadTask() {
		
	}
	
	@SuppressLint("NewApi")
	static String getResponse(Uri uri) throws IOException {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			AndroidHttpClient client = AndroidHttpClient
					.newInstance(USER_AGENT);
			HttpResponse resp = client.execute(new HttpGet(uri.toString()));
			OutputStream os = new ByteArrayOutputStream();
			resp.getEntity().writeTo(os);
			client.close();
			os.close();
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

}
