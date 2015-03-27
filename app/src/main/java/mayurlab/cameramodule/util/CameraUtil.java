package mayurlab.cameramodule.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by "Mayur Solanki" on 26/03/15.
 */
public class CameraUtil {
	// directory name to store captured images
	private static final String IMAGE_DIRECTORY_NAME = "FloderName";
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final String IMG_PREFIX = "IMG_";
	private static final String IMG_POSTFIX = ".png";

	/**
	 * Creating file uri to store image
	 */
	public static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/**
	 * returning image
	 */
	private static File getOutputMediaFile(int type) {

		// * Checks if external storage is available for read and write */
		if (isExternalStorageWritable()) {
			// External sdcard location
			File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

			// Create the storage directory if it does not exist
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
					return null;
				}
			}

			File mediaFile;
			if (type == MEDIA_TYPE_IMAGE) {

				// if Directory exits  check if any file in there than
				// delete
				File[] contents = mediaStorageDir.listFiles();
				if (contents.length >= 1) {
					deleteRecursive(mediaStorageDir);
				}
				mediaFile = new File(mediaStorageDir.getPath() + File.separator + IMG_PREFIX + "temp" + IMG_POSTFIX);
			} else {
				return null;
			}
			return mediaFile;

		} else {
			return null;
		}

	}

	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 * This method is use for check ExIfInof of image.
	 *
	 * @param mediaFile
	 *
	 **/
	public static int checkExIfInfo(String mediaFile) {
		final ExifInterface exif;
		int rotation = 0;
		try {
			exif = new ExifInterface(mediaFile);
			final String exifOrientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
			if (exifOrientation.equals("6")) {
				rotation = 90;// Rotation angle
			} else if (exifOrientation.equals("1")) {
				rotation = 0;// Rotation angle
			} else if (exifOrientation.equals("8")) {
				rotation = 270;// Rotation angle
			} else if (exifOrientation.equals("3")) {
				rotation = 180;// Rotation angle
			} else if (exifOrientation.equals("0")) {
				rotation = 0;// Rotation angle
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return rotation;
	}

	/**
	 * This method is use for rotation of image.
	 *
	 * @param mediaFile
	 *
	 **/
	public static void rotateImage(String mediaFile, int rotation) {
		if (rotation != 0) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			Bitmap bitmap = BitmapFactory.decodeFile(mediaFile, options);
			if (bitmap != null) {

				Matrix matrix = new Matrix();
				matrix.setRotate(rotation);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				// bitmap.recycle();
				try {
					final FileOutputStream fos = new FileOutputStream(mediaFile);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Delete images or folder
	public static void deleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory()) {
			for (File child : fileOrDirectory.listFiles()) {
				deleteRecursive(child);
			}
		}

		fileOrDirectory.delete();
	}

}
