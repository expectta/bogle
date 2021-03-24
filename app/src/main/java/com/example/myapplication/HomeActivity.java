package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class HomeActivity extends YouTubeBaseActivity {
    //유튜브에서 영상을 id값으로 가져와서 동영상 재생함
    private String videoId = "ntBhEYOfQ_0";
    YouTubePlayerView youTubePlayerView;
    public static final String TAG = "로그";
    ScrollView svParseItem;
    ImageView ivParseFirstImage, ivParseSecondImage, ivParseThirdImage;
    TextView tvParseFirstTitle, tvParseSecondTitle, tvParseThirdTitle;
    ArrayList<String> parseItemTitle = new ArrayList<>();
    ArrayList<String> parseItemImageUrl = new ArrayList<>();
    Bitmap firstBitmap, secondBitmap, thirdBitmap;
    MyAsyncTask tipTextThread;
    TextView tvTipText, tvTemp, tvMinTemp, tvMaxTemp, tvHumidity;
    Button btnPlan, btnPost, btnMyHistory, btnLogOut, btnAdmin;
    Intent intent;
    String superUser;
    Thread thread;
    int PERMISSION_ID = 44;
    boolean isLoopThread = false;
    LocationManager locationManager;
    //현재 GPS 사용유무
    boolean isGPSEnabled = false;
    //네트워크 사용유무
    boolean isNetworkEnabled = false;
    //GPS 상태값
    boolean isGetLocation = false;
    Location location;
    //위도
    double lat;
    //경도
    double lon;
    //최소 GPS 정보 업데이트 거리 1000미터
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000;
    //최소 업데이트 시간 1분
    private static final long MIN_TIME_BW_UPADTES = 1000 * 60 * 1;
    Context mContext;
    URL url;
    String resultText = null;
    String urlPath = "http://api.openweathermap.org/data/2.5/weather?lat=37.476200&lon=126.973154&units=metric&appid=b3a6fd72ab285c2d24338cbadb9b64c0";
    HttpURLConnection httpURLConnection = null;
    private String forecastJsonStr;
    private Handler handler;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getVideoId();
        Log.d(TAG, "CommunityActivity - onStart() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        tipTextThread.cancel(true);
        //화면이 안보일때 영상 정지
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint({"WrongViewCast", "HandlerLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        youTubePlayerView = findViewById(R.id.youtubeView);
        svParseItem = findViewById(R.id.information);
        svParseItem.setHorizontalScrollBarEnabled(true);
        ivParseFirstImage = findViewById(R.id.parseImage1);
        ivParseSecondImage = findViewById(R.id.parseImage2);
        ivParseThirdImage = findViewById(R.id.parseImage3);
        tvParseFirstTitle = findViewById(R.id.parseTitle1);
        tvParseSecondTitle = findViewById(R.id.parseTitle2);
        tvParseThirdTitle = findViewById(R.id.parseTitle3);

        btnMyHistory = findViewById(R.id.btnMyHistory);
        btnLogOut = findViewById(R.id.btnLogOut);
        btnPlan = findViewById(R.id.btnDdivePlan);
        btnPost = findViewById(R.id.btnPost);
        btnAdmin = findViewById(R.id.btnAdministrator);

        tvTemp = findViewById(R.id.temp);
        tvMinTemp = findViewById(R.id.minTemp);
        tvMaxTemp = findViewById(R.id.maxTemp);
        tvHumidity = findViewById(R.id.humidity);

        intent = getIntent();
        superUser = intent.getStringExtra("superUser");
        tipTextThread = new MyAsyncTask();
        tipTextThread.execute();
        requestPermission();
        getLocation();
        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            //루퍼에의해 실행되는 함수.
            //송신 쓰레드에서 전달받은 파라메터를 통해 코드가 진행됨.
            public void handleMessage(@NonNull Message msg) {
                tvTemp.setText(msg.getData().getString("temp"));
                tvMinTemp.setText(msg.getData().getString("minTemp"));
                tvMaxTemp.setText(msg.getData().getString("maxTemp"));
                tvHumidity.setText(msg.getData().getString("humidity"));
                }
        };
        //날씨 쓰레드
        WeatherThread weatherThread = new WeatherThread();
        weatherThread.start();
        //접속자가 관리자인지 일반회원인지 판단하고 관리자에게만 보여줄 수 있는 버튼을 VISIBLE.
        if (superUser != null && superUser.equals("super")) {
            //접속자가 관리자면 버튼이 보인다.
            btnAdmin.setVisibility(View.VISIBLE);
        } else {
            //접속자가 일반회원일 경우 관리자 버튼이 안보인다.
            btnAdmin.setVisibility(View.GONE);
        }
        //webView1 버튼
        ivParseFirstImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, WebActivity.class);
                intent.putExtra("url", "https://divingholic.com/scuba-kick");
                startActivity(intent);
            }
        });
        //webView2 버튼
        ivParseSecondImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, WebActivity.class);
                intent.putExtra("url", "https://divingholic.com/anilao-diving-point");
                startActivity(intent);
            }
        });
        //webView3 버튼
        ivParseThirdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, WebActivity.class);
                intent.putExtra("url", "https://divingholic.com/freediving-word1");
                startActivity(intent);
            }
        });
        //활동계획 버튼
        btnPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CommunityActivity.class);
                startActivity(intent);
            }
        });
        //게시판 버튼
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ShareActivity.class);
                startActivity(intent);
            }
        });
        //내정보 버튼
        btnMyHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
                //내 정보를 확인하는 버튼을 입력했을때 현재 접속자가정보를 인탠트를 통해 정보를 전달한다.
                //전달할 데이터타입은 int
                startActivity(intent);
            }
        });
        //로그아웃 버튼
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //관리자 버튼
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AdministratorActivity.class);
                //액티비티를 호출할 때 호출된 액티비티의 인스턴스가현재 태스크의 루트에
                //이미 실행중인 경우 활동이 재실행되지 않는다.
                //새로운 인슽턴스를 생성하지 않는다.
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        // 팁 무한반복
        tvTipText = findViewById(R.id.tipText);
        //핸드폰이 인터넷과 연결이 되어있는지 아닌지를 확인하고 토스트 메세지를 띄우는 함수.
        checkInternetState();

//        try {
//            httpURLConnection.setRequestMethod("POST");
//            httpURLConnection.setRequestProperty("Cache-Control", "no-cache");
//            httpURLConnection.setRequestProperty("Content-Type", "application/json");
//            httpURLConnection.setRequestProperty("Accept", "application/json");
//            httpURLConnection.setDoOutput(true);
//            httpURLConnection.setDoOutput(true);
//            int responseCode = httpURLConnection.getResponseCode();
//            if (responseCode != 200)
//                throw new IOException("Post failed with error cod " + responseCode);
//
//            InputStreamReader inputStream = new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8");
//            BufferedReader reader = new BufferedReader(inputStream);
//            StringBuilder builder = new StringBuilder();
//
//            String text;
//            while ((text = reader.readLine()) != null) {
//                builder.append(text + "\n");
//            }
//            resultText = builder.toString();
//            Log.d(TAG, resultText + "날씨 결과값");
//        } catch (ProtocolException e) {
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (httpURLConnection != null) {
//                httpURLConnection.disconnect();
//            }
//        }
        //
        youTubePlayerView.initialize("AIzaSyAj362YNmpwY9cK79Wtx7iyLMEG8PhIVXM",
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(
                            YouTubePlayer.Provider provider,
                            final YouTubePlayer youTubePlayer,
                            boolean b) {
                        if (!b) {
                            youTubePlayer.cueVideo(videoId);
                        }
                        youTubePlayer.setPlayerStateChangeListener(
                                new YouTubePlayer.PlayerStateChangeListener() {
                                    @Override
                                    public void onLoading() {
                                    }

                                    @Override
                                    public void onLoaded(String s) {
                                        youTubePlayer.play();
                                    }
                                    @Override
                                    public void onAdStarted() {
                                    }
                                    @Override
                                    public void onVideoStarted() {
                                    }
                                    @Override
                                    public void onVideoEnded() {
                                    }
                                    @Override
                                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                                    }
                                });
                    }

                    @Override
                    public void onInitializationFailure(
                            YouTubePlayer.Provider provider,
                            YouTubeInitializationResult youTubeInitializationResult) {
                    }
                });
    }

    //openweathermap API에서 날씨 정보를 받아와서 JSON으로 파싱하는 함수.
    class WeatherThread extends Thread {
        @Override
        public void run() {
            BufferedReader reader = null;
            try {
                /*현재 서울의 날씨
                http://api.openweathermap.org/data/2.5/weather?lat=37.56826&lon=126.977829&APPID=[발급
                오늘의 서울 날씨
                http://api.openweathermap.org/data/2.5/forecast/daily?lat=37.56826&lon=126.977829&APPID=[발급
                5일간 서울의 예보
                http://api.openweathermap.org/data/2.5/forecast/city?lat=37.56826&lon=126.977829&APPID=[발급*/
                //새 URL 객체
                String todayWeatherUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&units=metric&appid=b3a6fd72ab285c2d24338cbadb9b64c0";
                URL url = new URL(todayWeatherUrl);
                //새 URLConnection
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                //InputStream 을 사용해 데이터 읽어들이기
                InputStream inputStream = httpURLConnection.getInputStream();

                //StringBuffer 에 데이터 저장
                StringBuffer buffer = new StringBuffer(); // 새로운 StringBuffer 생성
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    // 불러온 데이터가 비어있음.
                    forecastJsonStr = null;
                }
                //로드한 데이터 문자열 변수에 저장.
                forecastJsonStr = buffer.toString();
                /*
                {
                    "coord":{
                    "lon":126.98,  경도
                    "lat":37.49    위도
                                        },
                    "weather":[
                    {
                    "id":701,             도시 ID
                    "main":"Mist",        현재 날씨 상태
                    "description":"mist",  날씨 설명
                    "icon":"50d"           날씨 아이콘
                    }],
                    "base":"stations",
                    "main":{
                    "temp":18,             온도
                    "feels_like":19.62,    체감온도도
                     "temp_min":18,
                    "temp_max":18,
                    "pressure":1005,      기압
                    "humidity":93         습도
                     }
                    "visibility":3200,
                        "wind":{
                    "speed":1,
                            "deg":200
                        },
                    "clouds":{
                    "all":75      구름의 양 75%
                     },
                    "dt":1591130484,          데이터 회신 시간
                        "sys":{
                    "type":1,
                     "id":8105,
                     "country":"KR",          국가
                     "sunrise":1591128730,
                     "sunset":1591181318
                      },
                    "timezone":32400,
                    "id":6800035,
                    "name":"Banpobondong",   도시이름
                    "cod":200
                   }
                */
                Log.d(TAG, forecastJsonStr + " 최종인가??");
                JSONObject jsonObject = new JSONObject(forecastJsonStr);
                Log.d(TAG, jsonObject.toString() + "제이슨오브젝트");
                if (jsonObject != null) {
                    String iconName = "";
                    String nowTemp = "";
                    String maxTemp = "";
                    String minTemp = "";
                    String humidity = "";
                    String speed = "";
                    String main = "";
                    String descriprion = "";
                    try {
                        iconName = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                        nowTemp = jsonObject.getJSONObject("main").getString("temp");
                        humidity = jsonObject.getJSONObject("main").getString("humidity");
                        minTemp = jsonObject.getJSONObject("main").getString("temp_min");
                        maxTemp = jsonObject.getJSONObject("main").getString("temp_max");
                        speed = jsonObject.getJSONObject("wind").getString("speed");
                        main = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
                        descriprion = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    descriprion = transferWeather(descriprion);
                    final String msg = descriprion + "습도 " + humidity + "%, 풍속" + speed + "m/s" + "온도 현재:" +
                            nowTemp + "/ 최저" + minTemp + " /최고 : " + maxTemp;
                    Log.d(TAG, msg + "오늘의 날씨~");
                    Bundle data = new Bundle();
                    data.putString("temp", nowTemp);
                    data.putString("minTemp", minTemp);
                    data.putString("maxTemp",maxTemp);
                    data.putString("humidity", humidity);
                    Message message = new Message();
                    message.setData(data);
                    handler.sendMessage(message);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                forecastJsonStr = null;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    //HttpURLConnection 연결 끊기
                    httpURLConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }
        }

    }

    //사용자가 권한을 허가했는지 체크하는 메서드
    boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            return true;
        }
        return false;
    }

    //사용자에게 위치정보 권한을 요청하는 메서드
    void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    //사용자가 권한을 허가하거나 거절했을 때에 불러오는 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

    //사용자가 사용기기의 위치정보를 on 해놓았는지 확인하는 메서드
    boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            int gpsCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (gpsCheck == PackageManager.PERMISSION_DENIED) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
            //GPS 정보 가져오기
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                //GPS와 네트워크 사용이 가능하지 않을때 소수 그현
                Toast.makeText(this, "인터넷 또는 GPS 연결 안됨.", Toast.LENGTH_SHORT).show();

            } else {
                this.isGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPADTES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    if (locationManager != null) {

                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            //위도경도저장
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            Log.d(TAG, lat + " 위도");
                            Log.d(TAG, lon + " 경도");
                        }
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPADTES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();

                            }
                        }
                    }
                }
            }
//            getWeatherData(lat , lon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    @SuppressLint("StaticFieldLeak")
    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /* Showing the ProgressBar, Making the main design GONE */
//            findViewById(R.id.loader).setVisibility(View.VISIBLE);
//            findViewById(R.id.mainContainer).setVisibility(View.GONE);
//            findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }


    //    public Location getLocation() {
//        try {
//            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
//            //GPS 정보 가져오기
//            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            //현재 네트워크 상태 값 알아오기
//            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//            if (!isGPSEnabled && !isNetworkEnabled) {
//                //GPS와 네트워크사이용이 가능하지 않을때 소스 구현
//            } else {
//                this.isGetLocation = true;
//                //네트워크 정보로 부터 위치값 가져오기
//                if (isNetworkEnabled) {
//                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        // TODO: Consider calling
//                        //    ActivityCompat#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for ActivityCompat#requestPermissions for more details.
//                        return TODO;
//                    }
//                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPADTES,
//                            MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
//                    if (locationManager != null) {
//                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        if (location != null) {
//                            //위도 경도 저장
//                            lat = location.getLatitude();
//                            lon = location.getLongitude();
//
//                        }
//                    }
//                }
//                if (isGPSEnabled) {
//                    if (location == null) {
//                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPADTES,
//                                MIN_DISTANCE_CHANGE_FOR_UPDATES,locationListener);
//                        if (locationManager != null) {
//                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//
//                            if (location != null) {
//                                lat = location.getLatitude();
//                                lon = location.getLongitude();
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
//
//    }
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            getWeatherData(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    //GPS로 현재 위치를 파악하고 위도/경도를 기반으로 날씨정보를 파싱.
    void getWeatherData(double lat, double lng) {
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lng + "&units=metric&appid=b3a6fd72ab285c2d24338cbadb9b64c0";
        ReceiveWeatherTask receiveUseTask = new ReceiveWeatherTask();
        receiveUseTask.execute(url);
        Log.d(TAG, url + "URL");
    }

    class ReceiveWeatherTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... datas) {
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(datas[0]).openConnection();
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.connect();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String readed;
                    while ((readed = bufferedReader.readLine()) != null) {
                        JSONObject jsonObject = new JSONObject(readed);
                        String result = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                        Log.d(TAG, result + "결과값");
                        return jsonObject;
                    }
                } else {
                    return null;
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

        }
    }

    private String transferWeather(String weather) {
        weather = weather.toLowerCase();
        if (weather.equals("haze")) {
            return "안개";
        } else if (weather.equals("fog")) {
            return "안개";
        } else if (weather.equals("clouds")) {
            return "구름";
        } else if (weather.equals("few clouds")) {
            return "구름 조금";
        } else if (weather.equals("scatterred clouds")) {
            return "구름 낌";
        } else if (weather.equals("broken clouds")) {
            return "구름 많음";
        } else if (weather.equals("overcast clouds")) {
            return "구름 많음";
        } else if (weather.equals("clear sky")) {
            return "맑음";
        }
        return "";
    }

    //커뮤니티 게시판 팅 정보를 지속적으로 출하는 쓰레드 클래스.
    @SuppressLint("StaticFieldLeak")
    class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        String[] tipTextArray = new String[]{"TIP : 다이빙 전 스트레칭 필수!!",
                "TIP : 수신호를 꼭 숙지하세요!!",
                "TIP : 해양생물은 만지면 안돼요~"};
        String text;

        //초기화코드
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "MyAsyncTask - onPreExecute() called");
            isLoopThread = true;
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "MyAsyncTask - onPostExecute() called");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.d(TAG, "MyAsyncTask - onProgressUpdate() called");
            tvTipText.setText(text);
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG, "MyAsyncTask - onCancelled() called");
            super.onCancelled();
        }

        //백그라운드 작업 구현
        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "MyAsyncTask - doInBackground() called");
            do {
                for (int i = 0; i < tipTextArray.length; i++) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    text = tipTextArray[i];
                    publishProgress();
                }
            } while (isLoopThread);
            return null;
        }
    }

    //    public void weatherParsing(){
//            try {
//                //접근할려는 URL 생성
//                url = new URL(urlPath);
//                httpURLConnection = (HttpURLConnection) url.openConnection();
//                httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
//                httpURLConnection.setReadTimeout(Data);
//
//                int reponseCode = httpURLConnection.getResponseCode();
//                if(reponseCode != 200) throw new IOException(("Post failed with error code" +
//                        reponseCode));
//                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8");
//                BufferedReader reader = new BufferedReader(inputStreamReader);
//                Log.d(TAG, reader.toString() + "날씨정보");
//                StringBuilder builder = new StringBuilder();
//
//                String text;
//                while ((text = reader.readLine()) != null) {
//                    builder.append(text + "\n");
//                }
//                result = builder.toString();
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//    }
    //JSOUP을 이용해 유튜브 영상의 ID 값을 추출하는 함수
    //쓰레드 + runOnUiThread로 구현됨.
    public void getVideoId() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //scrollView에 보여줄 item의 갯수
                final int scrollItemNumber = 4;
                int firstImage = 0;
                int secondImage = 1;
                int thirdImage = 2;
                final StringBuilder builder = new StringBuilder();
                try {
                    //사이트 접속하여 HTML 코드를 document로 담는다.
                    Document document = Jsoup.connect("https://divingholic.com/category/ocean/scuba-diving").get();
                    //div(태그). class = td-module-thumb > <a> > <img>
                    Elements necessaryText = document.select("div.td-module-thumb a img");
                    for (int i = 0; i < scrollItemNumber; i++) {
                        parseItemTitle.add(necessaryText.get(i).attr("title"));
                        parseItemImageUrl.add(necessaryText.get(i).attr("data-img-url"));
                        Log.d(TAG, parseItemImageUrl.get(i) + " \n  이미지 유알엘");
                        Log.d(TAG, parseItemTitle.get(i) + "\n  타이틀");
                    }
                    //URL 객체를 생성할때 JSOUP으로 파싱한 image URL로 생성한다.
                    URL firstImageUrl = new URL(parseItemImageUrl.get(firstImage));
                    URL secondImageUrl = new URL(parseItemImageUrl.get(secondImage));
                    URL thirdImageUrl = new URL(parseItemImageUrl.get(thirdImage));
                    Log.d(TAG, thirdImageUrl.toString());
                    //Bitmap으로 전환하는 과정.
                    HttpURLConnection fristHttpURLConnection = (HttpURLConnection) firstImageUrl.openConnection();
                    fristHttpURLConnection.setDoInput(true);
                    fristHttpURLConnection.connect();

                    InputStream firstIinputStream = fristHttpURLConnection.getInputStream();
                    //파싱완료한 이미지 URL을 bitmap으로 전환
                    firstBitmap = BitmapFactory.decodeStream(firstIinputStream);
                    HttpURLConnection secondHttpURLConnection = (HttpURLConnection) secondImageUrl.openConnection();
                    secondHttpURLConnection.setDoInput(true);
                    secondHttpURLConnection.connect();

                    InputStream secondIinputStream = secondHttpURLConnection.getInputStream();
                    //파싱완료한 이미지 URL을 bitmap으로 전환
                    secondBitmap = BitmapFactory.decodeStream(secondIinputStream);
                    HttpURLConnection thirdHttpURLConnection = (HttpURLConnection) thirdImageUrl.openConnection();
                    thirdHttpURLConnection.setDoInput(true);
                    thirdHttpURLConnection.connect();

                    InputStream thirdIinputStream = thirdHttpURLConnection.getInputStream();
                    //파싱완료한 이미지 URL을 bitmap으로 전환
                    thirdBitmap = BitmapFactory.decodeStream(thirdIinputStream);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //제목으로 사용할 파싱한 text numbering
                        int parseFirstTitle = 0;
                        int parseSecondTitle = 1;
                        int parseThirdTitle = 2;
                        ivParseFirstImage.setImageBitmap(firstBitmap);
                        tvParseFirstTitle.setText(parseItemTitle.get(parseFirstTitle));
                        ivParseSecondImage.setImageBitmap(secondBitmap);
                        tvParseSecondTitle.setText(parseItemTitle.get(parseSecondTitle));
                        ivParseThirdImage.setImageBitmap(thirdBitmap);
                        tvParseThirdTitle.setText(parseItemTitle.get(parseThirdTitle));
                    }
                });
            }
        }).start();
    }

    //인터넷에 연결이 됐는지 확인하는 함수
    public void checkInternetState() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            Toast.makeText(this, "인터넷 연결 됨", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "인터넷 연결 안됨", Toast.LENGTH_SHORT).show();
        }
    }
}


