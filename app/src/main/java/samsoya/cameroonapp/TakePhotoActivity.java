package samsoya.cameroonapp;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TakePhotoActivity extends AppCompatActivity {
    private FrameLayout mContentView;
    private byte[] photoBytes = null;
    Camera camera = getCameraInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        getSupportActionBar().hide(); //hide the title bar

        if (camera == null) {
            //TODO(team): determine behavior if camera is taken
        }

        CameraPreview preview = new CameraPreview(this, camera);

        mContentView = findViewById(R.id.camera_view);

        mContentView.addView(preview);


        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        Button takePhotoButton = findViewById(R.id.take_photo_button);
        final Camera.PictureCallback mPicture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                photoBytes = data;
                camera.stopPreview();
                camera.release();
                Intent intentWithPictureData = new Intent(TakePhotoActivity.this, ConfirmPhotoActivity.class);
                intentWithPictureData.putExtra(getString(R.string.photo_extra), photoBytes);
                startActivity(intentWithPictureData);
            }
        };
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(null, null, mPicture);
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        //delayedHide(100);
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseCamera(){
        if (camera != null){
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }
}
