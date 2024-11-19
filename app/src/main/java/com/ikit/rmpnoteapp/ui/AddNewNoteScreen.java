package com.ikit.rmpnoteapp.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.internal.PlacemarkMapObjectBinding;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import io.realm.Realm;

public class AddNewNoteScreen extends AppCompatActivity {
    private MapView mapView;
    private LocationManager locationManager;
    private LocationListener myLocationListener;
    private InputListener myInputListener;
    private Point myLocation;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().setTitle("Добавление новой заметки");

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_note_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();

        EditText titleInput = findViewById(R.id.noteName);
        EditText shortDescr = findViewById(R.id.shortDescription);
        EditText fullDesc = findViewById(R.id.fullDescription);
        EditText noteType = findViewById(R.id.noteType);
        Button saveBtn = findViewById(R.id.addNoteButton);


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getText().toString();
                String sd = shortDescr.getText().toString();
                String fd = fullDesc.getText().toString();
                String nt = noteType.getText().toString();
                long createTime = System.currentTimeMillis();

                realm.beginTransaction();

                long prevId = 123L;
                try {
                    prevId = (long) realm.where(Note.class).max("id");
                } catch (NullPointerException e) {
                    prevId = 0L;
                }
                Note note = realm.createObject(Note.class, prevId + 1);

                note.setTitle(title);
                note.setShortDescription(sd);
                note.setFullDescription(fd);
                note.setTimeWhenCreated(createTime);
                note.setNoteType(nt);

                if (longitude != 0) {
                    note.setLongitude(longitude);
                    note.setLatitude(latitude);
                }

                realm.commitTransaction();
                finish();
            }
        });

        MapKitFactory.initialize(this);
        mapView = findViewById(R.id.mapview);
        mapView.setScaleY(0.95f);
        mapView.setScaleX(0.95f);
        ImageProvider image = ImageProvider.fromResource(getApplicationContext(), R.drawable.location);

        requestLocationPermission();

        myInputListener = new InputListener() {
            PlacemarkMapObject currPoint = null;
            @Override
            public void onMapTap(@NonNull Map map, @NonNull Point point) {
                moveCamera(myLocation, 18);
            }

            @Override
            public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
                if (currPoint != null) {
                    mapView.getMapWindow().getMap().getMapObjects().remove(currPoint);
                }
                currPoint = mapView.getMapWindow().getMap().getMapObjects().addPlacemark(point, image);
                currPoint.setText(titleInput.getText().toString());
                longitude = point.getLongitude();
                latitude = point.getLatitude();
            }
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