package com.ikit.rmpnoteapp.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ikit.rmpnoteapp.R;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.location.Purpose;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

public class MapWithPoint extends AppCompatActivity {
    private MapView mapView;
    private LocationManager locationManager;
    private LocationListener myLocationListener;
    private InputListener myInputListener;
    private Point myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_with_point);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MapKitFactory.initialize(this);
        mapView = findViewById(R.id.mapview);

        double latitude = getIntent().getDoubleExtra("latitude", 1);
        double longitude = getIntent().getDoubleExtra("longitude", 1);
        mapView.getMapWindow().getMap().getMapObjects().addPlacemark(new Point(latitude, longitude),
                ImageProvider.fromResource(getApplicationContext(), R.drawable.location));

        myInputListener = new InputListener() {
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point point) {
                moveCamera(myLocation, 18);
            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {}
        };

        locationManager = MapKitFactory.getInstance().createLocationManager();
        myLocationListener = new LocationListener() {
            @Override
            public void onLocationUpdated(@NonNull Location location) {
                myLocation = location.getPosition();
            }

            @Override
            public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) {}
        };
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                    1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
        subscribeToLocationUpdate();
        mapPressListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MapKitFactory.getInstance().onStop();
        mapView.onStop();
    }
    private void subscribeToLocationUpdate() {
        if (locationManager != null && myLocationListener != null) {
            locationManager.subscribeForLocationUpdates(0, 1000, 1, false, FilteringMode.OFF, Purpose.NAVIGATION, myLocationListener);
        }
    }

    private void mapPressListener() {
        mapView.getMapWindow().getMap().addInputListener(myInputListener);
    }
    private void moveCamera(Point point, float zoom) {
        mapView.getMapWindow().getMap().move(
                new CameraPosition(point, zoom, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1),
                null);
    }
}