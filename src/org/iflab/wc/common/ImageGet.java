package org.iflab.wc.common;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/*
 * loadPictureFromUrl(String url)获取网络图片
 * getBitmap(String filePath)获取本地图片
 */
public class ImageGet {

	public Bitmap LoadPictureFromUrl(String url) {
		Bitmap bitmap = null;
		if (url.equals(null) || url.equals("") || url == null) {
			return bitmap;
		}
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpRequest = new HttpGet(url);
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		httpRequest.setParams(httpParams);
		try {
			HttpResponse response = httpClient.execute(httpRequest);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				return bitmap;
			}
			HttpEntity entity = response.getEntity();
			BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
			InputStream instream = bufHttpEntity.getContent();
			bitmap = BitmapFactory.decodeStream(instream);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return bitmap;
	}
	
	public Bitmap getBitmap(String filePath) {
		return decodeSampledBitmapFromResource(filePath, 100, 100);
	}

	public Bitmap getBitmap(String filePath, int reqWidth, int reqHeight) {
		return decodeSampledBitmapFromResource(filePath, reqWidth, reqHeight);
	}

	public Bitmap decodeSampledBitmapFromResource(String filepath,
			int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filepath, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
		return bitmap;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
}
