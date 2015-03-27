package mayurlab.cameramodule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import mayurlab.cameramodule.util.CameraUtil;

/**
 * Created by "Mayur Solanki" on 26/03/15.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private OrientationEventListener orientationListener;
    private Button mBtnCamera;
    private ImageView imgPreview;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static Uri fileUri; // file url to store image
    private String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnCamera = (Button) findViewById(R.id.main_btn_capture);
        imgPreview = (ImageView) findViewById(R.id.main_iv_privew);
        mBtnCamera.setOnClickListener(this);
        if (savedInstanceState != null) {
            path = savedInstanceState.getString("path");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("path", path);
        super.onSaveInstanceState(outState);
    }


    /*
     * Capturing Camera Image will lauch camera app requrest image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = CameraUtil.getOutputMediaFileUri(CameraUtil.MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        path = fileUri.getPath().toString();
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view

                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /*
     * Display image from a path to ImageView
     */
    private void previewCapturedImage() {
        new showImageAsyncTask().execute();
    }

    @Override
    public void onClick(View view) {
        captureImage();
    }

    public class showImageAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {

                int rotation;

                rotation = CameraUtil.checkExIfInfo(path);
                Log.e("Rotation :", "" + rotation);
                if (rotation != 0) {
                    CameraUtil.rotateImage(path, rotation);
                }


            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
            ImageLoader.getInstance().displayImage("file://" + path,
                    imgPreview);
        }
    }
}
