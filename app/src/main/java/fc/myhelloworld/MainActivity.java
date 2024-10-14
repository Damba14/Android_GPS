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

    /**
     * LocationListener для обработки GPS данных.
     * Обновляет UI при изменении местоположения, статусе провайдера и его доступности.
     */
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

    /**
     * LocationListener для обработки данных GSM сети (через Network Provider).
     * Обновляет UI при изменении местоположения, статусе провайдера и его доступности.
     */
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

    /**
     * Метод вызывается при создании Activity. Инициализирует GPS и GSM провайдеры.
     * @param savedInstanceState сохраненное состояние Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация GPS провайдера
        initializeLocationManager(LocationManager.GPS_PROVIDER, locationListenerGPS);

        // Инициализация Network провайдера (GSM)
        initializeLocationManager(LocationManager.NETWORK_PROVIDER, locationListenerGSM);
    }

    /**
     * Инициализация LocationManager для получения данных о местоположении.
     * @param provider провайдер местоположения (GPS или Network).
     * @param listener слушатель для обновления данных о местоположении.
     */
    private void initializeLocationManager(String provider, LocationListener listener) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            // Проверка, включен ли провайдер
            if (locationManager.isProviderEnabled(provider)) {
                listener.onProviderEnabled(provider);
            } else {
                listener.onProviderDisabled(provider);
            }
            // Запрос обновлений местоположения
            locationManager.requestLocationUpdates(provider, 0, 0, listener);
        }
    }

    /**
     * Обновление отображения данных о местоположении в UI.
     * @param location объект Location с данными о местоположении.
     * @param latViewId ID TextView для отображения широты.
     * @param lngViewId ID TextView для отображения долготы.
     * @param accViewId ID TextView для отображения точности.
     * @param extraViewId ID TextView для отображения дополнительных данных.
     * @param updateViewId ID TextView для отображения времени обновления.
     */
    private void updateLocationDisplay(Location location, int latViewId, int lngViewId, int accViewId, int extraViewId, int updateViewId) {
        ((TextView) findViewById(latViewId)).setText("Latitude: " + location.getLatitude());
        ((TextView) findViewById(lngViewId)).setText("Longitude: " + location.getLongitude());
        ((TextView) findViewById(accViewId)).setText("Accuracy: " + location.getAccuracy());

        // Обработка дополнительных данных о местоположении
        Bundle extras = location.getExtras();
        if (extras != null) {
            StringBuilder extraInfo = new StringBuilder();
            for (String key : extras.keySet()) {
                extraInfo.append("  ").append(key).append(": ").append(extras.get(key).toString()).append("\n");
            }
            ((TextView) findViewById(extraViewId)).setText(extraInfo.toString());
        }

        // Обновление времени последнего обновления
        ((TextView) findViewById(updateViewId)).setText("Updated: " + DateFormat.getDateTimeInstance().format(System.currentTimeMillis()));
    }

    /**
     * Обновление статуса провайдера.
     * @param provider имя провайдера (GPS или Network).
     * @param status текущий статус провайдера.
     * @param statusViewId ID TextView для отображения статуса.
     */
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

    /**
     * Обновление статуса включенного провайдера.
     * @param provider имя провайдера.
     * @param statusEnabledViewId ID TextView для отображения статуса включения.
     */
    private void updateProviderEnabled(String provider, int statusEnabledViewId) {
        ((TextView) findViewById(statusEnabledViewId)).setText(provider + " enabled");
    }

    /**
     * Обновление статуса выключенного провайдера.
     * @param provider имя провайдера.
     * @param statusEnabledViewId ID TextView для отображения статуса выключения.
     */
    private void updateProviderDisabled(String provider, int statusEnabledViewId) {
        ((TextView) findViewById(statusEnabledViewId)).setText(provider + " disabled");
    }

    /**
     * Обработчик кнопки, который сбрасывает текстовые поля при нажатии.
     * @param view элемент View, вызвавший действие.
     */
    public void sendMessage(View view) {
        // Сброс статуса
        updateTextViews(R.id.display_gps_status, R.id.display_gps_status_enabled, R.id.display_gsm_status, R.id.display_gsm_status_enabled, "Status reset");

        // Сброс местоположения
        updateTextViews(R.id.display_gps_latitude, R.id.display_gps_longitude, R.id.display_gps_accuracy, R.id.display_gps_extra,
                R.id.display_gsm_latitude, R.id.display_gsm_longitude, R.id.display_gsm_accuracy, R.id.display_gsm_extra, "Location reset");
    }

    /**
     * Обновление текста для нескольких TextView.
     * @param viewIds массив ID TextView для обновления.
     * @param text текст, который нужно установить.
     */
    private void updateTextViews(int... viewIds, String text) {
        for (int id : viewIds) {
            ((TextView) findViewById(id)).setText(text);
        }
    }

    /**
     * Создание меню.
     * @param menu объект Menu для заполнения.
     * @return true, если меню успешно создано.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Обработка выбора пунктов меню.
     * @param item выбранный пункт меню.
     * @return true, если действие успешно обработано.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
