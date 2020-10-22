package pl.milosz.mam_1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.content.Context.SENSOR_SERVICE;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    ConstraintLayout layout;

    // Pozycja
    Location userLocation;
    Location GGLocation;
    Location WETILocation;
    Location OBCLocation;

    //Camera
    FrameLayout frame;
    SurfaceView surface;
    private Camera camera;
    PreviewCamera previewCamera;
    CustomView customView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            }
            if (getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 3);

            }
        }

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM);
        mSensorManager.registerListener((SensorEventListener) this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        frame = findViewById(R.id.frame);
        surface = findViewById(R.id.surface);
        customView = new CustomView(this);
        layout = findViewById(R.id.layout);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED){
            camera = Camera.open();
            previewCamera = new PreviewCamera(this, camera);
            frame.addView(previewCamera);
        }

        layout.addView(customView);


        // na wszelki wypadek ustawiamy lokacje, która potem zostanie nadpisana
        userLocation = new Location("Provider");
        userLocation.setLatitude(54.391346);
        userLocation.setLatitude(18.569857);

        GGLocation = new Location("Provider");
        GGLocation.setLatitude(54.371482);
        GGLocation.setLongitude(18.619138);
        WETILocation = new Location("Provider");
        WETILocation.setLatitude(54.371659);
        WETILocation.setLongitude(18.612709);
        OBCLocation = new Location("Provider");
        OBCLocation.setLatitude(54.403179);
        OBCLocation.setLongitude(18.570772);

        GetLocation();
    }


    private void GetLocation() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 4);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 5);
        }
        LocationServices.getFusedLocationProviderClient(this)
               .requestLocationUpdates(locationRequest, new LocationCallback(){
                   @Override
                   public void onLocationResult(LocationResult locationResult) {
                       super.onLocationResult(locationResult);
                       if(locationResult !=null && locationResult.getLocations().size() > 0){
                           int latestLocationIndex = locationResult.getLocations().size()-1;
                           double lat = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                           double lon = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                           userLocation.setLatitude(lat);
                           userLocation.setLongitude(lon);
                           customView.setLocationData(lat,lon);
                           customView.invalidate();
                       }
                   }
               }, Looper.getMainLooper());
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM);
        mSensorManager.registerListener((SensorEventListener) this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener((SensorEventListener) this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

    float[] mGravity;
    float[] mGeomagnetic;
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                float[] cameraVector = {0, 0, -1};
                float[] deviceVector = new float[3];
                deviceVector[0] = R[0]*cameraVector[0] +
                        R[1]*cameraVector[1] +
                        R[2]*cameraVector[2];
                deviceVector[1] = R[3]*cameraVector[0] +
                        R[4]*cameraVector[1] +
                        R[5]*cameraVector[2];
                deviceVector[2] = R[6]*cameraVector[0] +
                        R[7]*cameraVector[1] +
                        R[8]*cameraVector[2];
                //Log.i()
                customView.setDeviceVectorData(deviceVector);
                if(userLocation!=null){
                    float bearingGG = userLocation.bearingTo(GGLocation);
                    float[] vectorGG= new float[3];
                    vectorGG[0] = (float) Math.sin(Math.toRadians(bearingGG));
                    vectorGG[1] = (float) Math.cos(Math.toRadians(bearingGG));
                    vectorGG[2] = 0.0f;
                    float angleGG = (float) Math.toDegrees(getAngle(vectorGG,deviceVector));
                    customView.setGGData(bearingGG, vectorGG, angleGG);

                    //WETI
                    float bearingWETI = userLocation.bearingTo(WETILocation);
                    float[] vectorWETI= new float[3];
                    vectorWETI[0] = (float) Math.sin(Math.toRadians(bearingWETI));
                    vectorWETI[1] = (float) Math.cos(Math.toRadians(bearingWETI));
                    vectorWETI[2] = 0.0f;
                    float angleWETI = (float) Math.toDegrees(getAngle(vectorWETI,deviceVector));
                    customView.setWETIData(bearingWETI, vectorWETI, angleWETI);

                    //Olivia Business Centre
                    float bearingOBC = userLocation.bearingTo(OBCLocation);
                    float[] vectorOBC= new float[3];
                    vectorOBC[0] = (float) Math.sin(Math.toRadians(bearingOBC));
                    vectorOBC[1] = (float) Math.cos(Math.toRadians(bearingOBC));
                    vectorOBC[2] = 0.0f;
                    float angleOBC = (float) Math.toDegrees(getAngle(vectorOBC,deviceVector));
                    customView.setOBCData(bearingOBC, vectorOBC, angleOBC);
                }

                customView.setData(formatValue(event.values[0]),formatValue(event.values[1]),formatValue(event.values[2]),
                        formatValue(orientation[0]),formatValue(orientation[1]),formatValue(orientation[2]));
                customView.invalidate();
            }
        }
    }

    public float formatValue(float value){
        return (float) (Math.floor(value * 100) / 100);
    }

    public float vectorMagnitude(float[] vec)
    {
        return (float) Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);
    }

    float dotProduct(float[] a, float[] b)
    {
        return a[0]*b[0] + a[1]*b[1] + a[2]*b[2];
    }

    public float getAngle(float[] a, float b[])
    {
        return (float) Math.acos(
                dotProduct(a, b) / (vectorMagnitude(a) * vectorMagnitude(b))
        );
    }

}

