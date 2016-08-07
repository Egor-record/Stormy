package com.siaskov.stormy;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private CurrentWeather mCurrentWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Указываем от куда получать инфу
        String apiKey = "3e05f0c9b39ab4f24a1f7b90ac0d0746";
        double latitude = 37.8267;
        double longitude = -122.423;
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey + "/" + latitude + "," + longitude;



        // Если сеть есть
        if (isNetworkAvalible()) {



        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(forecastUrl)
                .build();

        Call call = client.newCall(request);
            // посылаем запрос
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }


            // всякая шляпа
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {

                    if (response.isSuccessful()) {
                        String jsonDate = response.body().string();
                        Log.v(TAG, jsonDate);
                        mCurrentWeather = getCurrentDetails(jsonDate);
                    } else {
                        alertUserAboutError();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Exeption caught ", e);
                }
                catch   (JSONException e) {
                    Log.e(TAG, "Exeption caught ", e);
                }
            }
        });

    } else {
            Toast.makeText(this, R.string.network_unavalible_message, Toast.LENGTH_LONG ).show();
        }
        Log.d(TAG, "Main ");

    }


    // Формируем массив из данных
    private CurrentWeather getCurrentDetails(String jsonDate) throws JSONException {


        JSONObject forecast = new JSONObject(jsonDate);

        // получаем таймзон
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON:"  + timezone);

        // подмассив куррентли
        JSONObject currently = forecast.getJSONObject("currently");
        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimezone(timezone);

        Log.d(TAG, currentWeather.getFormattedTime());

        return new CurrentWeather();
    }


    // проверяем доступна ли сеть
    private boolean isNetworkAvalible() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvalible = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvalible = true;
        }

        return isAvalible;
    }





    // alert ошибку
    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }
}
