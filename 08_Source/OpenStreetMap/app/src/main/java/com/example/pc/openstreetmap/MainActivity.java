package com.example.pc.openstreetmap;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.overlays.GroundOverlay;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.bonuspack.utils.BonusPackHelper;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

import org.osmdroid.views.overlay.Marker.OnMarkerDragListener;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MainActivity extends AppCompatActivity implements MapEventsReceiver{

    MapView map = null;
    String KEY = "87c22c26-36bd-468d-9223-61be31580373";    // Graphhooper : https://graphhopper.com/dashboard/#/documentation
    String locale = "vi_VI";    // Khu vực tìm kiếm ở Việt Nam
    ArrayList<Place> places;    // Mảng các địa điểm khi tìm kiếm
    String urlGeocoding = "https://graphhopper.com/api/1/geocode?q=";   // URL để tìm địa điểm
    MyLocationNewOverlay myLocationNewOverlay;  // Dùng để tìm vị trị hiện tại
    String urlReverseGeocoding = "https://graphhopper.com/api/1/geocode?&reverse=true&point=";
    GeoPoint myPoint;  // Toa do cua nguoi dung


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sử dụng thư viện osmbonuspack
        Context ctx = getApplicationContext();
        org.osmdroid.config.Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main);

        // Điều kiện để dùng thư viện osmbonuspack
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        anhXa();

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
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);
        startMarker.setTitle("Đại học Khoa học tự nhiên - ĐHQG tp HCM");

        // Khi touch vao mot vi tri tren ban do
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this);
        map.getOverlays().add(0, mapEventsOverlay); //inserted at the "bottom" of all overlays

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
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up

    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    // Tìm địa điểm dựa vào tên và trả về địa điểm đầu tiên trong mảng
    public Place Geocoding(String str) {

        // URL theo khi tìm kiếm dựa trên Graphhoper
        String url = urlGeocoding + str + "&locale=" + locale + "&debug=true&key=" + KEY;
        // Lấy thông tin dạng JSON về
        String jString = BonusPackHelper.requestStringFromUrl(url);
        // Xóa hết các marker trên bản đồ
        map.getOverlays().clear();
        if (places != null) {
            places.clear();
        }
        // Danh sách các địa điểm
        places = new ArrayList<Place>();
        // Phân tích mảng JSON
        try {
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

        } catch (JSONException e) {
            e.printStackTrace();

        }
        return places.get(0);

    }

    public void anhXa() {
        map = (MapView) findViewById(R.id.map);
    }

    // Bắt sự kiện cho cac button
    public void callFunction(View view) {
        switch (view.getId()) {
            case R.id.btnSearch:
                searchPlace();
                break;
            case R.id.btnRouting:

                routing();
                break;
            case R.id.btnMyLocation:
                myLocation();
                break;
            case R.id.btnHoRes:
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
        final EditText autoTv = (EditText) dialogSeracch.findViewById(R.id.autoTV);
        Button btnPlace = (Button) dialogSeracch.findViewById(R.id.btnPlace);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tắt dialog
                dialogSeracch.dismiss();


                map.getOverlays().clear();
                if (places != null) {
                    places.clear();
                }
                //Geocoding(autoTv.getText().toString());
                new searchAsync(MainActivity.this).execute(autoTv.getText().toString());

//                // Thêm các Marker vào mỗi địa điểm tìm được
//                for (int i = 0; i < places.size(); i++) {
//                    places.get(i).addMarker(map);
//                }
//
//                // Nếu tìm được thì đưa Camera vào địa điểm đầu tiên trong mảng
//                if (!places.isEmpty()) {
//                    map.getController().setCenter(places.get(0).geoPoint);
//                    map.getController().setZoom(16);
//                }
////                // tắt dialog
////                dialogSeracch.dismiss();
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

        final TextView tvDuration = (TextView) dialogRouting.findViewById(R.id.tvDuration);
        final TextView tvDistance = (TextView) dialogRouting.findViewById(R.id.tvDistance);

        final AutoCompleteTextView startPlace = (AutoCompleteTextView) dialogRouting.findViewById(R.id.start);
        final AutoCompleteTextView endPlace = (AutoCompleteTextView) dialogRouting.findViewById(R.id.end);
        Button btnFindpath = (Button) dialogRouting.findViewById(R.id.btnFindpath);

        btnFindpath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogRouting.dismiss();
                new routinhAsync(MainActivity.this).execute(startPlace.getText().toString(),endPlace.getText().toString());
            }
        });

        map.invalidate();
    }

    // Tim vi tri hien tai
    public void myLocation() {

        myLocationNewOverlay = new MyLocationNewOverlay(map);
        myLocationNewOverlay.enableMyLocation();
        map.getOverlays().clear();
        map.getOverlays().add(myLocationNewOverlay);
        myLocationNewOverlay.enableFollowLocation();
        myLocationNewOverlay.setDrawAccuracyEnabled(true);


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
    public Place reverseGeocoding(GeoPoint geoPoint){
        String point = Double.toString(geoPoint.getLatitude()) + ","+Double.toString(geoPoint.getLongitude());
        String url = urlReverseGeocoding + point + "&locale=" + locale + "&debug=true&key=" + KEY;
        String jString = BonusPackHelper.requestStringFromUrl(url);
        Place place = null;
        try{
            JSONObject jsonObject = new JSONObject(jString);
            JSONArray jsonArray = jsonObject.optJSONArray("hits");
            if (jsonArray == null || jsonArray.length() == 0) {
                Toast.makeText(this, "No place", Toast.LENGTH_SHORT).show();
                return null;
            }
            JSONObject jPlace = jsonArray.getJSONObject(0);
            place = new Place(jPlace);
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jPlace = jsonArray.getJSONObject(i);
//                place = new Place(jPlace);
//
//            }
        }catch (JSONException e){

        }
        return place;
    }

    public void searchHotelnRestaurant(){
//        hoRes(map,"Hotel");
//        hoRes(map,"Restaurant");
        new hoResAsync(MainActivity.this).execute("Hotel","Restaurant");
        map.invalidate();
    }

    // Tim cac dia diam "key" va in ra map
    public void hoRes(final MapView map, final String key){
        //5. Tìm cái địa điểm là "key" ( tìm kiếm trong phạm vị hình vuông ) quanh vị trí myPoint
        NominatimPOIProvider poiProvider = new NominatimPOIProvider("OsmNavigator/1.0");

        //Tham số thứ ba trong getPOICloseTo là khoảng cách so với startpoint ( đơn vị là độ)

        //* @param maxDistance to the position, in degrees.
        //* Note that it is used to build a bounding box around the position, not a circle.

        final ArrayList<POI> pois = poiProvider.getPOICloseTo(myPoint, key, 20, 0.005);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FolderOverlay poiMarkers = new FolderOverlay(MainActivity.this);
                map.getOverlays().add(poiMarkers);
                // Thêm icon hotel
                Drawable poiIcon = getResources().getDrawable(R.drawable.hotel);

                if(key.equals("Restaurant")){
                    poiIcon = getResources().getDrawable(R.drawable.restaurant);
                }

                // Add Marker các địa điểm
                for (POI poi:pois){
                    Marker poiMarker = new Marker(map);
                    poiMarker.setTitle(poi.mType);
                    poiMarker.setSnippet(poi.mDescription);
                    poiMarker.setPosition(poi.mLocation);
                    poiMarker.setIcon(poiIcon);
//            if (poi.mThumbnail != null){
//                poiItem.setImage(new BitmapDrawable(poi.mThumbnail));
//            }
                    poiMarkers.add(poiMarker);
                }
            }
        });


    }

    private class hoResAsync extends AsyncTask<String,Void,Void>{
        private ProgressDialog dialog;
        String key;
        ArrayList<POI> pois = new ArrayList<>();

        public hoResAsync(MainActivity activity) {
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
            if(dialog.isShowing())
                dialog.dismiss();
            map.invalidate();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            FolderOverlay poiMarkers = new FolderOverlay(MainActivity.this);
            map.getOverlays().add(poiMarkers);
            // Thêm icon hotel
            Drawable poiIcon = getResources().getDrawable(R.drawable.hotel);
            if(key.equals("Restaurant")){
                poiIcon = getResources().getDrawable(R.drawable.restaurant);
            }

            // Add Marker các địa điểm
            for (POI poi:pois){
                Marker poiMarker = new Marker(map);
                poiMarker.setTitle(poi.mType);
                poiMarker.setSnippet(poi.mDescription);
                poiMarker.setPosition(poi.mLocation);
                poiMarker.setIcon(poiIcon);
//            if (poi.mThumbnail != null){
//                poiItem.setImage(new BitmapDrawable(poi.mThumbnail));
//            }
                poiMarkers.add(poiMarker);
            }
        }

        @Override
        protected Void doInBackground(String... strings) {
            NominatimPOIProvider poiProvider = new NominatimPOIProvider("OsmNavigator/1.0");
            for(int i = 0 ; i < strings.length; i++) {
                key = strings[i];
                pois = poiProvider.getPOICloseTo(myPoint, key, 20, 0.005);
                onProgressUpdate();
            }
            return null;
        }
    }

//    private class locatingAsync extends AsyncTask<Void,Overlay,Void> {
//        private ProgressDialog dialog;
//        MyLocationNewOverlay LocationNewOverlay;  // Dùng để tìm vị trị hiện tại
//        public locatingAsync(MainActivity activity) {
//            dialog = new ProgressDialog(activity);
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            map.getOverlays().clear();
//            dialog.setTitle("Locating");
//            dialog.setMessage("Loading...");
//            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            dialog.show();
//        }
//
//        @Override
//        protected void onProgressUpdate(Overlay... values) {
//            super.onProgressUpdate(values);
//            map.getOverlays().add(values[0]);
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            LocationNewOverlay = new MyLocationNewOverlay(map);
//
//            LocationNewOverlay.enableMyLocation();
//            onProgressUpdate(LocationNewOverlay);
//            LocationNewOverlay.enableFollowLocation();
//            LocationNewOverlay.setDrawAccuracyEnabled(true);
//
//            LocationNewOverlay.runOnFirstFix(new Runnable() {
//                @Override
//                public void run() {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            myPoint = LocationNewOverlay.getMyLocation();
//                            map.getController().setZoom(15);
//                            map.getController().animateTo(myPoint);
//                        }
//                    });
//                }
//            });
//
//            if (dialog.isShowing())
//                dialog.dismiss();
//        }
//
//
//        @Override
//        protected Void doInBackground(Void... voids) {
////            LocationNewOverlay = new MyLocationNewOverlay(map);
////            LocationNewOverlay.enableMyLocation();
////            onProgressUpdate(LocationNewOverlay);
////            LocationNewOverlay.enableFollowLocation();
////            LocationNewOverlay.setDrawAccuracyEnabled(true);
//            return  null;
//        }
//    }

    private class searchAsync extends AsyncTask<String,Place,Void>{
        private ProgressDialog dialog;
        String jString;
        public searchAsync(MainActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            map.getOverlays().clear();
//            if (places != null) {
//                places.clear();
//            }
            places = new ArrayList<Place>();
            dialog.setTitle("Finding place");
            dialog.setMessage("Working...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Thêm các Marker vào mỗi địa điểm tìm được
            for (int i = 0; i < places.size(); i++) {
                places.get(i).addMarker(map);
            }

            // Nếu tìm được thì đưa Camera vào địa điểm đầu tiên trong mảng
            if (!places.isEmpty()) {
                map.getController().setCenter(places.get(0).geoPoint);
                map.getController().setZoom(16);
            }
            if(dialog.isShowing())  dialog.dismiss();
            return ;
        }

        @Override
        protected void onProgressUpdate(Place... values) {
            super.onProgressUpdate(values);
            places.add(values[0]);

        }

        @Override
        protected Void doInBackground(String... voids) {
            // URL theo khi tìm kiếm dựa trên Graphhoper
            String url = urlGeocoding + voids[0] + "&locale=" + locale + "&debug=true&key=" + KEY;
            // Lấy thông tin dạng JSON về
            jString = BonusPackHelper.requestStringFromUrl(url);
            try {
                JSONObject jsonObject = new JSONObject(jString);
                JSONArray jsonArray = jsonObject.optJSONArray("hits");
                if (jsonArray == null || jsonArray.length() == 0) {
                    //Toast.makeText(this, "No place", Toast.LENGTH_SHORT).show();
                    //return null;
                }

                // Thêm các địa điểm vào mảng places
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jPlace = jsonArray.getJSONObject(i);
                    Place place = new Place(jPlace);
                    //places.add(place);
                    onProgressUpdate(place);

                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
            return null;
        }
    }

    private class routinhAsync extends AsyncTask<String,GeoPoint,Void>{

        ProgressDialog dialog;
        Place sPlace;
        Place ePlace ;
        Polyline roadOverlay;
        //OSM không cho tìm đường theo tên mà theo tọa độ
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();

        public routinhAsync(MainActivity activity) {
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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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
            if(dialog.isShowing())
                dialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(GeoPoint... values) {
            super.onProgressUpdate(values);
            waypoints.add(values[0]);
        }

        @Override
        protected Void doInBackground(String... strings) {
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

            //tvDistance.setText(String.format("%.2f", road.mLength));   // km
            //tvDuration.setText(String.format("%.1f", road.mDuration/60)); // s

            return null;
        }
    }

}

