package com.ui.g5.hores;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Debug;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.se.omapi.Session;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.utils.BonusPackHelper;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;


public class main_screen extends AppCompatActivity implements MapEventsReceiver {

    ImageView btnmenu;
    String url_getdata = "https://nqphu1998.000webhostapp.com/getdata.php";
    ArrayList<User> arrayList;
    String user = "", email = "";
    org.osmdroid.views.MapView map = null;                                                          // Map
    String KEY = "87c22c26-36bd-468d-9223-61be31580373";                                            // Graphhooper : https://graphhopper.com/dashboard/#/documentation
    String locale = "vi_VI";                                                                        // Khu vực tìm kiếm ở Việt Nam
    ArrayList<Place> places;                                                                        // Mảng các địa điểm khi tìm kiếm
    String urlGeocoding = "https://graphhopper.com/api/1/geocode?q=";                               // URL Geocodinh
    String urlReverseGeocoding = "https://graphhopper.com/api/1/geocode?&reverse=true&point=";      // Url Reverse Geocodinh
    GeoPoint myPoint;                                                                               // Toa do cua nguoi dung
    MyLocationNewOverlay myLocationNewOverlay;                                                      // Quan ly dinh vi
    FloatingActionButton location, floatBtn, btnTimkiem, btnChiduong, btnTimkiemxungquanh;
    Place myPlace;
    Place origin, destination;
    boolean HideFloatBtn = true;
    MapEventsOverlay mapEventsOverlay;                                                              // Xu ly khi touch vao man hinh ( add marker)
    ArrayList<Place> historySearch = new ArrayList<Place>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sử dụng Mapview của osmdroid
        Context ctx = getApplicationContext();
        org.osmdroid.config.Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main_screen);
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("username") != null) {
            user = (String) bundle.getString("username");
            email = (String) bundle.getString("email");

        }

        btnmenu = (ImageView) findViewById(R.id.btnmenu);
        btnmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showmenu();
            }
        });

        // Điều kiện để dùng thư viện osmbonuspack
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Ánh xạ
        map = findViewById(R.id.map);
        myLocationNewOverlay = new MyLocationNewOverlay(map);
        location = (FloatingActionButton) findViewById(R.id.locateBtn);
        floatBtn = (FloatingActionButton) findViewById(R.id.floatingBtn);
        btnChiduong = (FloatingActionButton) findViewById(R.id.btnChiduong);
        btnTimkiem = (FloatingActionButton) findViewById(R.id.btnTimkiem);
        btnTimkiemxungquanh = (FloatingActionButton) findViewById(R.id.btnTimkiemxungquanh);

        // Thiết lập cơ bản cho map
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        final IMapController mapController = map.getController();
        mapController.setZoom(15);

        // Địa điểm mặc định startpoint : Đại học khoa học tự nhiên HCM
        myPoint = new GeoPoint(10.762367, 106.681307);
        mapController.setCenter(myPoint);

        // Add marker
        Marker startMarker = new Marker(map);
        startMarker.setPosition(myPoint);
        startMarker.setIcon(getResources().getDrawable(R.drawable.marker_default));
        map.getOverlays().add(startMarker);
        startMarker.setTitle("Đại học Khoa học tự nhiên - ĐHQG tp HCM");

        // Khi touch vao mot vi tri tren ban do
        mapEventsOverlay = new MapEventsOverlay(this);
        map.getOverlays().add(0, mapEventsOverlay); //inserted at the "bottom" of all overlays

        // My Location
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocation();
            }
        });

        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HideFloatBtn == true) {
                    HideFloatBtn = false;
                    ShowFloatingBtn();
                } else {
                    HideFloatingBtn();
                    HideFloatBtn = true;
                }
            }
        });

    }

    private void ShowFloatingBtn() {
        btnTimkiem.show();
        btnChiduong.show();
        btnTimkiemxungquanh.show();
    }

    private void HideFloatingBtn() {
        btnTimkiem.hide();
        btnChiduong.hide();
        btnTimkiemxungquanh.hide();
    }

    private void showmenu() {
        PopupMenu popupMenu = new PopupMenu(main_screen.this, btnmenu);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.getMenu().getItem(0).setTitle("Username:" + user);
        popupMenu.getMenu().getItem(1).setTitle("Email:" + email);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.itemtimkiem:
                        break;
                    case R.id.itemdiadiem:
                        break;
                    case R.id.itemthem:
                        break;
                    case R.id.itemshare:
                        break;
                    case R.id.itemhuongdan:
                        break;
                    case R.id.itemthongtin:
                        break;
                    case R.id.itemdangxuat:
                        Logout();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }


    // Siggle tap
    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {

        myPoint = p;
        myPlace = reverseGeocoding(p);
        // Xoa marker tren map
        map.getOverlays().clear();

        // Them mapEventsOverlay de xu ly touch
        map.getOverlays().add(0, mapEventsOverlay);

        myPlace.addMarker(map);
        map.invalidate();

        return true;
    }

    // Long press
    @Override
    public boolean longPressHelper(GeoPoint p) {
        return true;
    }

    public void onResume() {
        super.onResume();
        map.onResume();

        myLocationNewOverlay.disableFollowLocation();
        myLocationNewOverlay.disableMyLocation();
        map.getOverlays().add(myLocationNewOverlay);
    }

    public void onPause() {
        super.onPause();
        map.onPause();
    }

    // Logout
    private void Logout(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (isLoggedIn) {
            LoginManager.getInstance().logOut();
            Log.d("NOTICE: ", "CLEAR ACCESS TOKEN");
        }

        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emptyLayout, new Signin());

        ft.commit();
        Toast.makeText(getApplication(), "Logout successfully", Toast.LENGTH_SHORT).show();
    }

    // Tìm địa điểm dựa vào tên và trả về địa điểm đầu tiên trong mảng
    public Place Geocoding(String str) throws JSONException {
        String url = urlGeocoding + str + "&locale=" + locale + "&debug=true&key=" + KEY;           // URL theo khi tìm kiếm dựa trên Graphhoper
        String jString = BonusPackHelper.requestStringFromUrl(url);                                 // Lấy thông tin dạng JSON về

        // Xóa hết các marker trên bản đồ
        map.getOverlays().clear();
        // Them mapEventsOverlay de xu ly touch
        map.getOverlays().add(0, mapEventsOverlay);
        if (places != null) {
            places.clear();
        }

        // Danh sách các địa điểm
        places = new ArrayList<>();

        // Phân tích mảng JSON
        JSONObject jsonObject = new JSONObject(jString);
        JSONArray jsonArray = jsonObject.optJSONArray("hits");
        if (jsonArray == null || jsonArray.length() == 0) {
            Toast.makeText(this, "No place", Toast.LENGTH_SHORT).show();
            return null;
        }

        // Thêm các địa điểm vào mảng places
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jPlace = jsonArray.getJSONObject(i);
            Place place = new Place(jPlace);
            places.add(place);

        }

        return places.get(0);

    }

    // Bắt sự kiện cho cac button
    public void callFunction(View view) {
        switch (view.getId()) {
            case R.id.btnTimkiem:
                searchPlace();
                break;

            case R.id.btnChiduong:
                routing();
                break;

            case R.id.btnTimkiemxungquanh:
                searchHotelnRestaurant();
                break;

            default:
                break;
        }

    }

    // Tim dia diem
    public void searchPlace() {

        // Không thể thao tác trên các edittext, button,... của layout khác nên sẽ tạo Dialog
        final Dialog dialogSeracch = new Dialog(this);
        dialogSeracch.setContentView(R.layout.search);
        dialogSeracch.setTitle("Tìm địa điểm");
        dialogSeracch.show();

        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) dialogSeracch.findViewById(R.id.autoCompleteTextView);
        final AutoCompleteAdapter adapter = new AutoCompleteAdapter(main_screen.this, R.layout.custom_item_autocompletetextview, R.id.autoCompleteItem,historySearch);

        autoCompleteTextView.setAdapter(adapter);                                                   // Thiết lập adapter cho autocomplete textview
        autoCompleteTextView.setThreshold(3);                                                       // Thiết lập số ký tự tối thiểu cho việc tìm kiếm
        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {                        // Show Drop Down
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                autoCompleteTextView.showDropDown();
                return false;
            }

        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                autoCompleteTextView.setSelection(0);

                // Tat dialog
                dialogSeracch.dismiss();

                // Xoa marker tren map
                map.getOverlays().clear();
                // Them mapEventsOverlay de xu ly touch
                map.getOverlays().add(0, mapEventsOverlay);

                if (places != null) {
                    places.clear();
                }

                // Lay danh sach cac dia diem tim duoc
                places = adapter.getArrayPlace();

                Place temp;
                if(places.size() != 0) {
                    // Lay ra dia diem duoc chon
                    temp = places.get(position);
                }else{
                    temp = historySearch.get(position);
                }

                // Luu vao history
                if(historySearch.size() == 10) {
                    historySearch.remove(0);
                }
                if(historySearch.contains(temp)) {
                    historySearch.remove(temp);
                }
                historySearch.add(0, temp);

                // Them marker, chinh camera
                temp.addMarker(map);
                map.getController().setCenter(temp.geoPoint);
                map.getController().setZoom(16);

            }
        });

        map.invalidate();
    }

    // Tim duong di
    public void routing() {

        final Dialog dialogRouting = new Dialog(this);
        dialogRouting.setContentView(R.layout.routing);
        dialogRouting.setTitle("Tìm đường đi");
        dialogRouting.show();

        final AutoCompleteTextView startPlace = dialogRouting.findViewById(R.id.start);
        final AutoCompleteTextView endPlace = dialogRouting.findViewById(R.id.end);

        final AutoCompleteAdapter adapterStart = new AutoCompleteAdapter(main_screen.this, R.layout.custom_item_autocompletetextview, R.id.autoCompleteItem,historySearch);
        final AutoCompleteAdapter adapterEnd = new AutoCompleteAdapter(main_screen.this, R.layout.custom_item_autocompletetextview, R.id.autoCompleteItem,historySearch);

        startPlace.setAdapter(adapterStart);
        endPlace.setAdapter(adapterEnd);

        startPlace.setThreshold(3);
        endPlace.setThreshold(3);

        startPlace.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                startPlace.showDropDown();
                return false;
            }
        });
        endPlace.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                endPlace.showDropDown();
                return false;
            }
        });


        startPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Place> placesStart = adapterStart.getArrayPlace();
                if(placesStart.size() != 0) {
                    origin = placesStart.get(position);
                }else{
                    origin = historySearch.get(position);
                }
                startPlace.setText(origin.getName() + ", " + origin.getStreet() + ", " + origin.getCity());

                // Luu vao history
                if(historySearch.size() == 10) {
                    historySearch.remove(0);
                }
                if(historySearch.contains(origin)) {
                    historySearch.remove(origin);
                }
                historySearch.add(0, origin);
            }
        });

        endPlace.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Place> placesEnd = adapterEnd.getArrayPlace();
                if(placesEnd.size() != 0) {
                    destination = placesEnd.get(position);
                }else{
                    destination = historySearch.get(position);
                }
                endPlace.setText(destination.getName() + ", " + destination.getStreet() + ", " + destination.getCity());

                // Luu vao history
                if(historySearch.size() == 10) {
                    historySearch.remove(0);
                }
                if(historySearch.contains(destination)) {
                    historySearch.remove(destination);
                }
                historySearch.add(0, destination);
            }
        });


        Button btnFindpath = dialogRouting.findViewById(R.id.btnFindpath);

        btnFindpath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xóa hết các marker trên bản đồ
                map.getOverlays().clear();
                // Them mapEventsOverlay de xu ly touch
                map.getOverlays().add(0, mapEventsOverlay);

                if (places != null) {
                    places.clear();
                }

                // Tat dialog
                dialogRouting.dismiss();

                //Tim duong
                new routingAsync(main_screen.this).execute(origin, destination);

            }
        });

        map.invalidate();
    }

    // Tim vi tri hien tai
    public void myLocation() {
        Log.d("MY LOCATION: ", "BEGIN");

        map.getOverlays().clear();
        map.getOverlays().add(myLocationNewOverlay);

        // Them mapEventsOverlay de xu ly touch
        map.getOverlays().add(0, mapEventsOverlay);

        myLocationNewOverlay.enableMyLocation();
        myLocationNewOverlay.disableFollowLocation();
        myLocationNewOverlay.setOptionsMenuEnabled(true);

        if (myLocationNewOverlay.getMyLocation() != null) {
            double Longitude = myLocationNewOverlay.getMyLocation().getLongitude();
            double Latitude = myLocationNewOverlay.getMyLocation().getLatitude();

            Log.d("Longitude: ", Double.toString(Longitude));
            Log.d("Latitude: ", Double.toString(Latitude));

        } else {
            Log.e("MY LOCATION: ", "NULL OBJECT");
            if (myLocationNewOverlay.getLastFix() == null) {
                Log.e("GET LAST FIX: ", "NULL OBJECT");
            }
        }

        myLocationNewOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myPoint = myLocationNewOverlay.getMyLocation();
                        map.getController().setZoom(15);
                        map.getController().animateTo(myPoint);
                    }
                });
            }
        });

        map.invalidate();

        Log.d("MY LOCATION: ", "END");
    }

    // Chuyen toa do thanh dia chi
    public Place reverseGeocoding(GeoPoint geoPoint) {
        String spoint = Double.toString(geoPoint.getLatitude()) + "," + Double.toString(geoPoint.getLongitude());
        String url = urlReverseGeocoding + spoint + "&locale=" + locale + "&debug=true&key=" + KEY;
        String jString = BonusPackHelper.requestStringFromUrl(url);
        Place place = null;
        try {
            JSONObject jsonObject = new JSONObject(jString);
            JSONArray jsonArray = jsonObject.optJSONArray("hits");
            if (jsonArray == null || jsonArray.length() == 0) {
                Toast.makeText(this, "No place", Toast.LENGTH_SHORT).show();
                return null;
            }
            JSONObject jPlace = jsonArray.optJSONObject(0);
            JSONObject jGeopoint = jPlace.optJSONObject("point");
            GeoPoint point = new GeoPoint(jGeopoint.optDouble("lat"), jGeopoint.optDouble("lng"));
            String name = jPlace.optString("name");
            String street = jPlace.optString("street");
            String city = jPlace.optString("city");
            String country = jPlace.optString("country");
            String osm_value = jPlace.optString("osm_value");
            String houseNumber = jPlace.optString("housenumber");

            place = new Place(point, name, street, city, osm_value,country,houseNumber);


        } catch (JSONException e) {

        }
        return place;
    }

    public void searchHotelnRestaurant() {
        map.getOverlays().clear();
        // Them mapEventsOverlay de xu ly touch
        map.getOverlays().add(0, mapEventsOverlay);

        new hoResAsync(main_screen.this).execute("Hotel", "Restaurant");
        map.invalidate();
    }

    private class hoResAsync extends AsyncTask<String, Void, Void> {
        private ProgressDialog dialog;
        String key;
        ArrayList<POI> pois = new ArrayList<POI>();

        public hoResAsync(Activity activity) {
            super();
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Finding Hotel & Restaurant");
            dialog.setMessage("Working...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (pois != null && !pois.isEmpty()) {
                if (dialog.isShowing())
                    dialog.dismiss();
                map.invalidate();
            } else {
                dialog.setTitle("WARNING!");
                dialog.setMessage("Not found location...");
                dialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 2000);
            }

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            final FolderOverlay poiMarkers = new FolderOverlay(main_screen.this);
            map.getOverlays().add(poiMarkers);

            // Thêm icon hotel
            Drawable poiIcon = getResources().getDrawable(R.drawable.hotel);
            if (key.equals("Restaurant")) {
                poiIcon = getResources().getDrawable(R.drawable.restaurant);
            }

            final Drawable finalPoiIcon = poiIcon;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (pois != null && !pois.isEmpty()) {
                        // Add Marker các địa điểm
                        for (POI poi : pois) {
                            Marker poiMarker = new Marker(map);
                            poiMarker.setTitle(poi.mType.toUpperCase());
                            poiMarker.setSnippet(poi.mDescription);
                            poiMarker.setPosition(poi.mLocation);
                            poiMarker.setIcon(finalPoiIcon);
                            if(poi.mThumbnail != null){
                                Bitmap b = Bitmap.createScaledBitmap(poi.mThumbnail,50,50,false);
                                poiMarker.setImage(new BitmapDrawable(getResources(),b));
                            }
                            poiMarkers.add(poiMarker);
                        }
                    }
                }
            });

        }

        @Override
        protected Void doInBackground(String... strings) {
            NominatimPOIProvider poiProvider = new NominatimPOIProvider("OsmNavigator/1.0");
            for (int i = 0; i < strings.length; i++) {
                key = strings[i];
                pois = poiProvider.getPOICloseTo(myPoint, key, 20, 0.005);
                onProgressUpdate();
            }
            return null;
        }
    }

    private class routingAsync extends AsyncTask<Place, GeoPoint, Boolean> {

        ProgressDialog dialog;
        Place sPlace;
        Place ePlace;
        Polyline roadOverlay;
        //OSM không cho tìm đường theo tên mà theo tọa độ
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();

        public routingAsync(Activity activity) {
            super();
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setTitle("Routing");
            dialog.setMessage("Working");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if (isSuccess) {
                // Vẽ Polyline lên map
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        map.getOverlays().add(roadOverlay);

                        // Thêm Marker địa điểm bắt đầu và kết thúc
                        sPlace.addMarker(map);
                        ePlace.addMarker(map);

                        // Đưa camera về địa điểm bắt đầu
                        map.getController().setCenter(sPlace.geoPoint);
                    }
                });

                if (dialog.isShowing())
                    dialog.dismiss();
            } else {
                dialog.setTitle("WARNING");
                dialog.setMessage("Not found routing...");
                dialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 2000);
            }

        }

        @Override
        protected void onProgressUpdate(GeoPoint... values) {
            super.onProgressUpdate(values);
            waypoints.add(values[0]);
        }

        @Override
        protected Boolean doInBackground(Place... values) {
            Boolean isSuccess = false;
            try {
                sPlace = values[0];
                ePlace = values[1];

                //1. Tạo RoadManager để thao tác
                RoadManager roadManager = new GraphHopperRoadManager(KEY, true);

                //2. Tìm đường từ startpoint đến endpoint

                waypoints.add(sPlace.getGeoPoint());
                waypoints.add(ePlace.getGeoPoint());

                // Tìm kiếm đường đi
                Road road = roadManager.getRoad(waypoints);
                // Tạo các đường Polyline từ đường đi
                roadOverlay = RoadManager.buildRoadOverlay(road);

                isSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return isSuccess;
        }
    }

}
