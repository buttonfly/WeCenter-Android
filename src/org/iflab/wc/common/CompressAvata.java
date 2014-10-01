package org.iflab.wc.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

public class CompressAvata {
	String compressAvatarPath;

	public CompressAvata(String avatarPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(avatarPath, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 400f;
		float ww = 240f;
		int be = 8;// 
		newOpts.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(avatarPath, newOpts);
		Bitmap Cbitmap = compressByCutQuality(bitmap);
		String path = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED) ? Environment
				.getExternalStorageDirectory().getPath() + "/fanfan" : null
				+ "/fanfan";
		File foldFile = new File(path);
		if (!foldFile.exists()) {
			foldFile.mkdir();
		}

		compressAvatarPath = path + File.separator + "avatarImageMin.jpg";
		File file = new File(path + File.separator + "avatarImageMin.jpg");
		try {
			file.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, outputStream);
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getCompressAvatarPath() {
		return compressAvatarPath;
	}

	public Boolean isConformSize() {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(compressAvatarPath, newOpts);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 512) {
			return true;
		} else {
			return false;
		}
	}

	private Bitmap compressByCutQuality(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 90, baos);
		int options = 80;
		while (baos.toByteArray().length / 1024 > 512) {
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 10;
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;

	}
}
