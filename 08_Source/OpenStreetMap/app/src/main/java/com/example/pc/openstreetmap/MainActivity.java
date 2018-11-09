package com.example.pc.openstreetmap;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.bonuspack.utils.BonusPackHelper;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import org.osmdroid.views.overlay.Marker.OnMarkerDragListener;

public class MainActivity extends AppCompatActivity {

    MapView map = null;
    Button btnPlace;
    EditText edtPlace;
    String KEY = "87c22c26-36bd-468d-9223-61be31580373";    // Của Graphhooper : https://graphhopper.com/dashboard/#/documentation
    String locale = "vi_VI";
    ArrayList<Place> places;    // Mảng các địa điểm khi tìm kiếm

    AutoCompleteTextView autoTv;

    // URL để tìm địa điểm
    String urlGeocoding = "https://graphhopper.com/api/1/geocode?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        org.osmdroid.config.Configuration.getInstance().load(ctx,PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main);

        // Điều kiện để dùng thư viện osmbonuspack
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        anhXa();

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        final IMapController mapController = map.getController();
        mapController.setZoom(15);
        // startpoint : Đại học khoa học tự nhiên HCM
        GeoPoint startPoint = new GeoPoint(10.762367, 106.681307);
        mapController.setCenter(startPoint);


        //0. Using the Marker overlay
//        Marker startMarker = new Marker(map);
//        startMarker.setPosition(startPoint);
//        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//        startMarker.setDraggable(true);
//        startMarker.setOnMarkerDragListener(new OnMarkerDragListenerDrawer());
//        map.getOverlays().add(startMarker);
//        startMarker.setTitle("Đại học Khoa học tự nhiên - DDHQG tp HCM");



        //1. Tạo RoadManager để thao tác
        //RoadManager roadManager = new GraphHopperRoadManager(KEY,true);

        //2. Tìm đường từ startpoint đến endpoint
        // OSM không cho tìm đường theo tên mà theo điểm

//        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
//        waypoints.add(startPoint);
//        GeoPoint endPoint = new GeoPoint(10.773556, 106.659350);
//        waypoints.add(endPoint);
//
//        Road road = roadManager.getRoad(waypoints);
//        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
//        map.getOverlays().add(roadOverlay);


//        //5. Tìm cái địa điểm là "Hotel" ( tìm kiếm trong phạm vị hình vuông )
//        NominatimPOIProvider poiProvider = new NominatimPOIProvider("OsmNavigator/1.0");

          // Tham số thứ ba trong getPOICloseTo là khoảng cách so với startpoint ( đơn vị là độ)

//        ArrayList<POI> pois = poiProvider.getPOICloseTo(startPoint, "Hotel", 20, 0.05);
//
//        FolderOverlay poiMarkers = new FolderOverlay(this);
//        map.getOverlays().add(poiMarkers);
        // Thêm icon hotel
//        Drawable poiIcon = getResources().getDrawable(R.drawable.hotel);
        // Add Marker các địa điểm
//        for (POI poi:pois){
//            Marker poiMarker = new Marker(map);
//            poiMarker.setTitle(poi.mType);
//            poiMarker.setSnippet(poi.mDescription);
//            poiMarker.setPosition(poi.mLocation);
//            poiMarker.setIcon(poiIcon);
////            if (poi.mThumbnail != null){
////                poiItem.setImage(new BitmapDrawable(poi.mThumbnail));
////            }
//            poiMarkers.add(poiMarker);
//        }
//
//
//        map.invalidate();

        // Geocoding: Tìm kiếm địa điểm theo tên

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Geocoding(autoTv.getText().toString());
                if(!places.isEmpty()) {
                    mapController.setCenter(places.get(0).geoPoint);
                    mapController.setZoom(16);
                }

            }
        });

        map.invalidate();

    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up

    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    public void Geocoding(String str){

        String url = urlGeocoding + str + "&locale=" + locale + "&debug=true&key=" + KEY;
        // Lấy thông tin dạng JSON về
        String jString = BonusPackHelper.requestStringFromUrl(url);
        // Xóa hết các marker trên bản đồ
        map.getOverlays().clear();
        if(places != null) {
            places.clear();
        }
        // Danh sách các địa điểm
        places = new ArrayList<Place>();
        // Phân tích mảng JSON
        try{
            JSONObject jsonObject = new JSONObject(jString);
            JSONArray jsonArray = jsonObject.optJSONArray("hits");
            if(jsonArray == null || jsonArray.length() == 0){
                Toast.makeText(this, "No place", Toast.LENGTH_SHORT).show();
                return ;
            }

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jPlace = jsonArray.getJSONObject(i);
                Place place = new Place(jPlace);
                place.addMarker(map);
                places.add(place);

            }

        }catch (JSONException e){
            e.printStackTrace();

        }

    }

//    public void autocompleteTV(){
//        autoArrayList.clear();
//        autoArrayList = new ArrayList<String>();
//        for(int i = 0; i < places.size();i++){
//            Place place = places.get(i);
//            String tmp = place.getName() + " - " + place.getStreet() + ", "+ place.getCity();
//            autoArrayList.add(tmp);
//        }
//    }
//
//    public void searchPlace(){
//
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,
//                autoArrayList);
//        autoTv.setAdapter(arrayAdapter);
//
//        //while(!autoTv.getText().toString().equals("")){
//            Geocoding(autoTv.getText().toString());
//            arrayAdapter.notifyDataSetChanged();
//        //}
//
//    }

    public void anhXa(){
        map = (MapView)findViewById(R.id.map);
        btnPlace = (Button)findViewById(R.id.btnPlace);
        autoTv = (AutoCompleteTextView)findViewById(R.id.autoTV);
        //edtPlace = (EditText)findViewById(R.id.edtPlace) ;
    }
}
