package fc.myhelloworld;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private final LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateLocationDisplay(location,
                    R.id.display_gps_latitude,
                    R.id.display_gps_longitude,
                    R.id.display_gps_accuracy,
                    R.id.display_gps_extra,
                    R.id.display_gps_updated);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            updateProviderStatus(provider, status, R.id.display_gps_status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            updateProviderEnabled(provider, R.id.display_gps_status_enabled);
        }

        @Override
        public void onProviderDisabled(String provider) {
            updateProviderDisabled(provider, R.id.display_gps_status_enabled);
        }
    };

    private final LocationListener locationListenerGSM = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateLocationDisplay(location,
                    R.id.display_gsm_latitude,
                    R.id.display_gsm_longitude,
                    R.id.display_gsm_accuracy,
                    R.id.display_gsm_extra,
                    R.id.display_gsm_updated);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            updateProviderStatus(provider, status, R.id.display_gsm_status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            updateProviderEnabled(provider, R.id.display_gsm_status_enabled);
        }

        @Override
        public void onProviderDisabled(String provider) {
            updateProviderDisabled(provider, R.id.display_gsm_status_enabled);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeLocationManager(LocationManager.GPS_PROVIDER, locationListenerGPS);
        initializeLocationManager(LocationManager.NETWORK_PROVIDER, locationListenerGSM);
    }

    private void initializeLocationManager(String provider, LocationListener listener) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (locationManager.isProviderEnabled(provider)) {
                listener.onProviderEnabled(provider);
            } else {
                listener.onProviderDisabled(provider);
            }
            locationManager.requestLocationUpdates(provider, 0, 0, listener);
        }
    }

    private void updateLocationDisplay(Location location, int latViewId, int lngViewId, int accViewId, int extraViewId, int updateViewId) {
        ((TextView) findViewById(latViewId)).setText("Latitude: " + location.getLatitude());
        ((TextView) findViewById(lngViewId)).setText("Longitude: " + location.getLongitude());
        ((TextView) findViewById(accViewId)).setText("Accuracy: " + location.getAccuracy());

        Bundle extras = location.getExtras();
        if (extras != null) {
            StringBuilder extraInfo = new StringBuilder();
            for (String key : extras.keySet()) {
                extraInfo.append("  ").append(key).append(": ").append(extras.get(key).toString()).append("\n");
            }
            ((TextView) findViewById(extraViewId)).setText(extraInfo.toString());
        }

        ((TextView) findViewById(updateViewId)).setText("Updated: " + DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
    }

    private void updateProviderStatus(String provider, int status, int statusViewId) {
        String statusText = "Status (" + provider + "): ";
        switch (status) {
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                statusText += "Temporarily unavailable";
                break;
            case LocationProvider.OUT_OF_SERVICE:
                statusText += "Out of service";
                break;
            case LocationProvider.AVAILABLE:
                statusText += "Available";
                break;
            default:
                statusText += "Unknown";
                break;
        }
        ((TextView) findViewById(statusViewId)).setText(statusText);
    }

    private void updateProviderEnabled(String provider, int statusEnabledViewId) {
        ((TextView) findViewById(statusEnabledViewId)).setText(provider + " enabled");
    }

    private void updateProviderDisabled(String provider, int statusEnabledViewId) {
        ((TextView) findViewById(statusEnabledViewId)).setText(provider + " disabled");
    }

    public void sendMessage(View view) {
        updateTextViews(R.id.display_gps_status, R.id.display_gps_status_enabled, R.id.display_gsm_status, R.id.display_gsm_status_enabled, "Status reset");

        updateTextViews(R.id.display_gps_latitude, R.id.display_gps_longitude, R.id.display_gps_accuracy, R.id.display_gps_extra,
                R.id.display_gsm_latitude, R.id.display_gsm_longitude, R.id.display_gsm_accuracy, R.id.display_gsm_extra, "Location reset");
    }

    private void updateTextViews(int... viewIds, String text) {
        for (int id : viewIds) {
            ((TextView) findViewById(id)).setText(text);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
