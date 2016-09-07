package com.example.gio.flashlight;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageView ivSwitch;
    ImageView ivDiscotec;

    boolean turnedOnMain;
    boolean turnedODiscotec;

    Camera camera;
    Camera.Parameters p;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        turnedOnMain = false;
        turnedODiscotec = false;

        PackageManager pkm = this.getPackageManager();
        if (!pkm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(MainActivity.this, "Device doesn't have camera", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            setUpViews();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Enother Program is using camera", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (camera != null) {
            camera.release();
        }
        stopHandler();
    }


    public void setUpViews() {
        ivSwitch = (ImageView) findViewById(R.id.ivSwitch);
        ivSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               stopHandler();
                if (turnedOnMain) {
                    turnOfFlashLight();
                    ivSwitch.setImageResource(R.drawable.moontransparent);
                    ivDiscotec.setVisibility(View.INVISIBLE);
                    turnedOnMain = false;
                } else {
                    turnOnFlashLight();
                    ivSwitch.setImageResource(R.drawable.suntransparent);
                    ivDiscotec.setVisibility(View.VISIBLE);
                    turnedOnMain = true;
                }
            }
        });

        ivDiscotec = (ImageView) findViewById(R.id.ivDiscotec);
        ivDiscotec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  if(turnedODiscotec){
                      stopHandler();
                      turnedODiscotec = false;
                      if (!camera.getParameters().getFlashMode().equals("torch")) {
                          turnOnFlashLight();
                          turnedOnMain=true;
                      }
                      return;
                  }

                turnedODiscotec = true;
                handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        if (turnedOnMain) {
                            turnOfFlashLight();
                            turnedOnMain = false;
                        } else {
                            turnOnFlashLight();
                            turnedOnMain = true;
                        }
                        handler.postDelayed(this, 400);
                    }
                };

                handler.postDelayed(r, 200);

            }
        });

        camera = Camera.open();
        p = camera.getParameters();
    }


    public void turnOnFlashLight() {
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(p);
        camera.startPreview();
    }


    public void turnOfFlashLight() {
        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(p);
        camera.stopPreview();
    }


    public void stopHandler() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
