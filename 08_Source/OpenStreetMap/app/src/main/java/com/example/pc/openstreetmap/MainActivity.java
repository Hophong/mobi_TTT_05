package com.example.pc.openstreetmap;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    String KEY = "87c22c26-36bd-468d-9223-61be31580373";    // Của Graphhooper : https://graphhopper.com/dashboard/#/documentation
    String locale = "vi_VI";    // Khu vực tìm kiếm ở Việt Nam
    ArrayList<Place> places;    // Mảng các địa điểm khi tìm kiếm
    String urlGeocoding = "https://graphhopper.com/api/1/geocode?q=";   // URL để tìm địa điểm

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sử dụng thư viện osmbonuspack
        Context ctx = getApplicationContext();
        org.osmdroid.config.Configuration.getInstance().load(ctx,PreferenceManager.getDefaultSharedPreferences(ctx));

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
        GeoPoint startPoint = new GeoPoint(10.762367, 106.681307);
        mapController.setCenter(startPoint);


        // Add marker
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);
        startMarker.setTitle("Đại học Khoa học tự nhiên - DDHQG tp HCM");

//        //5. Tìm cái địa điểm là "Hotel" ( tìm kiếm trong phạm vị hình vuông ) quanh vị trí startpoint
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

    // Trả về địa điểm đầu tiên trong mảng
    public Place Geocoding(String str){

        // URL theo khi tìm kiếm dựa trên Graphhoper
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
                return null;
            }

            // Thêm các địa điểm vào mảng places
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jPlace = jsonArray.getJSONObject(i);
                Place place = new Place(jPlace);
                places.add(place);

            }

        }catch (JSONException e){
            e.printStackTrace();

        }
        return places.get(0);

    }

    public void anhXa(){
        map = (MapView)findViewById(R.id.map);
    }

    // Bắt sự kiện cho 2 button Search và Routing
    public void callFunction(View view){
//        final FragmentManager fragmentManager = getSupportFragmentManager();
//        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Fragment fragment = null;
        switch (view.getId()){
            case R.id.btnSearch: searchPlace();
            break;
            case R.id.btnRouting: routing();
            break;
        }
//        fragmentTransaction.replace(R.id.fragmentContent,fragment);
//        fragmentTransaction.commit();
    }

    public void searchPlace(){
        // Không thể thao tác trên các edittext, button,... của layout khác nên sẽ tạo Dialog
        final Dialog dialogSeracch = new Dialog(this);
        dialogSeracch.setContentView(R.layout.search);
        dialogSeracch.setTitle("Tìm địa điểm");
        dialogSeracch.show();
        final EditText autoTv = (EditText)dialogSeracch.findViewById(R.id.autoTV);
        Button btnPlace = (Button)dialogSeracch.findViewById(R.id.btnPlace);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Geocoding(autoTv.getText().toString());

                // Thêm các Marker vào mỗi địa điểm tìm được
                for(int i=0;i<places.size();i++){
                    places.get(i).addMarker(map);
                }

                // Nếu tìm được thì đưa Camera vào địa điểm đầu tiên trong mảng
                if(!places.isEmpty()) {
                    map.getController().setCenter(places.get(0).geoPoint);
                    map.getController().setZoom(16);
                }
                // tắt dialog
                dialogSeracch.dismiss();
            }
        });
    }

    public void routing(){

        final Dialog dialogRouting = new Dialog(this);
        dialogRouting.setContentView(R.layout.routing);
        dialogRouting.setTitle("Tìm đường đi");
        dialogRouting.show();

        final TextView tvDuration = (TextView)dialogRouting.findViewById(R.id.tvDuration);
        final TextView tvDistance = (TextView)dialogRouting.findViewById(R.id.tvDistance);

        final AutoCompleteTextView startPlace = (AutoCompleteTextView)dialogRouting.findViewById(R.id.start);
        final AutoCompleteTextView endPlace = (AutoCompleteTextView)dialogRouting.findViewById(R.id.end);
        Button btnFindpath = (Button)dialogRouting.findViewById(R.id.btnFindpath);

        btnFindpath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển từ tên thành tọa độ
                // Lấy cặp tọa độ đầu tiên trong mảng tọa độ của mỗi địa điểm
                Place sPlace = Geocoding(startPlace.getText().toString());
                Place ePlace = Geocoding(endPlace.getText().toString());

                //1. Tạo RoadManager để thao tác
                RoadManager roadManager = new GraphHopperRoadManager(KEY,true);

                //2. Tìm đường từ startpoint đến endpoint
                //OSM không cho tìm đường theo tên mà theo tọa độ
                ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
                waypoints.add(sPlace.geoPoint);
                waypoints.add(ePlace.geoPoint);

                // Tìm kiếm đường đi
                Road road = roadManager.getRoad(waypoints);
                // Tạo các đường Polyline từ đường đi
                Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                // Vẽ Polyline lên map
                map.getOverlays().add(roadOverlay);

                // Thêm Marker địa điểm bắt đầu và kết thúc
                sPlace.addMarker(map);
                ePlace.addMarker(map);

                // Đưa camera về địa điểm bắt đầu
                map.getController().setCenter(sPlace.geoPoint);

                tvDistance.setText(String.format("%.2f",road.mLength));   // km
                tvDuration.setText(String.format("%.1f",road.mDuration)); // s

                //
                Toast.makeText(MainActivity.this, tvDistance.getText() + " km" + " - " + tvDuration.getText() +" s" , Toast.LENGTH_LONG).show();
                // Tắt dialog
                dialogRouting.dismiss();


                }
        });

    }
}
