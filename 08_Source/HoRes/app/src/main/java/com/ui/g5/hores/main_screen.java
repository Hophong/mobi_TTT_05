package com.ui.g5.hores;

<<<<<<< HEAD
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
=======
>>>>>>> master
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


public class main_screen extends AppCompatActivity implements MapEventsReceiver{


    ImageView btnmenu;

    FloatingActionButton location, floatBtn;

    String url_getdata = "https://nqphu1998.000webhostapp.com/getdata.php";
    ArrayList<User> arrayList;
    String user="",email="";
    org.osmdroid.views.MapView map = null;                                                          // Map
    String KEY = "87c22c26-36bd-468d-9223-61be31580373";                                            // Graphhooper : https://graphhopper.com/dashboard/#/documentation
    String locale = "vi_VI";                                                                        // Khu vực tìm kiếm ở Việt Nam
    ArrayList<Place> places;                                                                        // Mảng các địa điểm khi tìm kiếm
    String urlGeocoding = "https://graphhopper.com/api/1/geocode?q=";                               // URL Geocodinh
    String urlReverseGeocoding = "https://graphhopper.com/api/1/geocode?&reverse=true&point=";      // Url Reverse Geocodinh
    GeoPoint myPoint;                                                                               // Toa do cua nguoi dung
    MyLocationNewOverlay myLocationNewOverlay;                                                      // Quan ly dinh vi
    FloatingActionButton location, floatBtn, btnTimkiem, btnChiduong, btnTimkiemxungquanh;

    boolean HideFloatBtn = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

<<<<<<< HEAD
        // Sử dụng Mapview của osmdroid
        Context ctx = getApplicationContext();
        org.osmdroid.config.Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
=======
        location = (FloatingActionButton)findViewById(R.id.locateBtn);
        floatBtn = (FloatingActionButton)findViewById(R.id.floatingBtn);
        btnmenu=(ImageView) findViewById(R.id.btnmenu);
>>>>>>> master

        setContentView(R.layout.activity_main_screen);
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("username")!= null)
        {
            user=(String)  bundle.getString("username");
            email=(String) bundle.getString("email");

        }


        btnmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showmenu();
            }
        });

<<<<<<< HEAD
        // Điều kiện để dùng thư viện osmbonuspack
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Ánh xạ
        map = findViewById(R.id.map);
        myLocationNewOverlay = new MyLocationNewOverlay(map);
        location = (FloatingActionButton)findViewById(R.id.locateBtn);
        floatBtn = (FloatingActionButton)findViewById(R.id.floatingBtn);
        btnChiduong = (FloatingActionButton)findViewById(R.id.btnChiduong);
        btnTimkiem = (FloatingActionButton)findViewById(R.id.btnTimkiem);
        btnTimkiemxungquanh = (FloatingActionButton)findViewById(R.id.btnTimkiemxungquanh);
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
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this);
        map.getOverlays().add(0, mapEventsOverlay); //inserted at the "bottom" of all overlays

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocation();
            }
        });

        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HideFloatBtn == true) {
                    HideFloatBtn = false;
                    ShowFloatingBtn();
                }else{
                    HideFloatingBtn();
                    HideFloatBtn=true;
                }
            }
        });

=======
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to do
            }
        });

        
>>>>>>> master
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

    private void showmenu()
    {
        PopupMenu popupMenu =new PopupMenu(main_screen.this,btnmenu);
        popupMenu.getMenuInflater().inflate(R.menu.menu,popupMenu.getMenu());
        popupMenu.getMenu().getItem(0).setTitle("Username:"+user);
        popupMenu.getMenu().getItem(1).setTitle("Email:"+email);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
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
        Place place = reverseGeocoding(p);
        place.addMarker(map);
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

    // Tìm địa điểm dựa vào tên và trả về địa điểm đầu tiên trong mảng
    public Place Geocoding(String str) throws JSONException {
        String url = urlGeocoding + str + "&locale=" + locale + "&debug=true&key=" + KEY;           // URL theo khi tìm kiếm dựa trên Graphhoper
        String jString = BonusPackHelper.requestStringFromUrl(url);                                 // Lấy thông tin dạng JSON về

        // Xóa hết các marker trên bản đồ
        map.getOverlays().clear();
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

        final EditText autoTv = dialogSeracch.findViewById(R.id.autoTV);
        Button btnPlace = dialogSeracch.findViewById(R.id.btnPlace);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tắt dialog
                dialogSeracch.dismiss();

                map.getOverlays().clear();
                if (places != null) {
                    places.clear();
                }

                new searchAsync(main_screen.this).execute(autoTv.getText().toString());

            }
        });

        map.invalidate();
    }

    // Tim quang duong
    public void routing() {

        final Dialog dialogRouting = new Dialog(this);
        dialogRouting.setContentView(R.layout.routing);
        dialogRouting.setTitle("Tìm đường đi");
        dialogRouting.show();

        final AutoCompleteTextView startPlace = dialogRouting.findViewById(R.id.start);
        final AutoCompleteTextView endPlace = dialogRouting.findViewById(R.id.end);
        Button btnFindpath = dialogRouting.findViewById(R.id.btnFindpath);

        btnFindpath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogRouting.dismiss();
                new routingAsync(main_screen.this).execute(startPlace.getText().toString(), endPlace.getText().toString());
            }
        });

        map.invalidate();
    }

    // Tim vi tri hien tai
    public void myLocation() {
        map.getOverlays().clear();
        map.getOverlays().add(myLocationNewOverlay);
        myLocationNewOverlay.enableMyLocation();
        myLocationNewOverlay.disableFollowLocation();
        myLocationNewOverlay.setOptionsMenuEnabled(true);

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
    }

    // Chuyen toa do thanh dia chi
    public Place reverseGeocoding(GeoPoint geoPoint) {
        String point = Double.toString(geoPoint.getLatitude()) + "," + Double.toString(geoPoint.getLongitude());
        String url = urlReverseGeocoding + point + "&locale=" + locale + "&debug=true&key=" + KEY;
        String jString = BonusPackHelper.requestStringFromUrl(url);
        Place place = null;
        try {
            JSONObject jsonObject = new JSONObject(jString);
            JSONArray jsonArray = jsonObject.optJSONArray("hits");
            if (jsonArray == null || jsonArray.length() == 0) {
                Toast.makeText(this, "No place", Toast.LENGTH_SHORT).show();
                return null;
            }
            JSONObject jPlace = jsonArray.getJSONObject(0);
            place = new Place(jPlace);
        } catch (JSONException e) {

        }
        return place;
    }

    public void searchHotelnRestaurant() {
        map.getOverlays().clear();
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
                            poiMarker.setTitle(poi.mType);
                            poiMarker.setSnippet(poi.mDescription);
                            poiMarker.setPosition(poi.mLocation);
                            poiMarker.setIcon(finalPoiIcon);

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

    private class searchAsync extends AsyncTask<String, Place, Boolean> {
        private ProgressDialog dialog;
        String jString;

        public searchAsync(Activity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            places = new ArrayList<>();
            dialog.setTitle("Finding place");
            dialog.setMessage("Working...");
            dialog.show();
        }


        @Override
        protected void onProgressUpdate(Place... values) {
            super.onProgressUpdate(values);
            places.add(values[0]);

        }

        @Override
        protected Boolean doInBackground(String... voids) {
            // URL theo khi tìm kiếm dựa trên Graphhoper
            if (voids[0].equals("")) {
                return false;
            }

            String url = urlGeocoding + voids[0] + "&locale=" + locale + "&debug=true&key=" + KEY;
            jString = BonusPackHelper.requestStringFromUrl(url);

            try {
                JSONObject jsonObject = new JSONObject(jString);
                JSONArray jsonArray = jsonObject.optJSONArray("hits");

                // Thêm các địa điểm vào mảng places
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jPlace = jsonArray.getJSONObject(i);
                        Place place = new Place(jPlace);
                        onProgressUpdate(place);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                return (!places.isEmpty());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);

            if (isSuccess) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        places.get(0).addMarker(map);

                        // Nếu tìm được thì đưa Camera vào địa điểm đầu tiên trong mảng
                        if (!places.isEmpty()) {
                            map.getController().setCenter(places.get(0).geoPoint);
                            map.getController().setZoom(16);
                        }

                        if (dialog.isShowing()) dialog.dismiss();
                    }
                });
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


    }

    private class routingAsync extends AsyncTask<String, GeoPoint, Boolean> {

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
        protected Boolean doInBackground(String... strings) {
            Boolean isSuccess = false;
            try {
                sPlace = Geocoding(strings[0]);
                ePlace = Geocoding(strings[1]);

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
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return isSuccess;
        }
    }

}
