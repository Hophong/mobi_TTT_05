package com.ui.g5.hores;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ui.g5.hores.main_screen.likedList;
import static com.ui.g5.hores.main_screen.user;
// Lưu thông tin cần thiết của một địa điểm và các thao tác
public class Place extends AppCompatActivity {

    GeoPoint geoPoint;
    String name;
    String street;
    String city;
    String osm_value;
    String country;
    String houseNumber;
    String descriptions;

    public Place(GeoPoint geoPoint, String name, String street, String city) {
        this.geoPoint = geoPoint;
        this.name = name;
        this.street = street;
        this.city = city;
    }

    public Place(GeoPoint geoPoint, String name, String street, String city, String osm_value, String country, String houseNumber) {
        this.geoPoint = geoPoint;
        this.name = name;
        this.street = street;
        this.city = city;
        this.osm_value = osm_value;
        this.country = country;
        this.houseNumber = houseNumber;
    }

    public Place(GeoPoint geoPoint, String descriptions) {
        this.geoPoint = geoPoint;
        this.descriptions = descriptions;
    }

    public String getOsm_value() {
        return osm_value;
    }

    public void setOsm_value(String osm_value) {
        this.osm_value = osm_value;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass())
            return false;
        Place place = (Place)obj;
        return this.getGeoPoint() == place.getGeoPoint();
    }


    public Place(JSONObject jPlace) {
        try {
            JSONObject geometry = jPlace.optJSONObject("geometry");

            JSONArray coordinates = geometry.optJSONArray("coordinates");

            double lng = coordinates.getDouble(0);
            double lat = coordinates.optDouble(1);

            GeoPoint point = new GeoPoint(lat,lng);
            this.geoPoint = point;

            JSONObject jsonProperty = jPlace.optJSONObject("properties");
            this.name = jsonProperty.getString("name");
            this.city = jsonProperty.optString("city");
            this.street = jsonProperty.optString("street");

//            JSONObject jGeopoint = jPlace.getJSONObject("point");
//            GeoPoint point = new GeoPoint(jGeopoint.getDouble("lat"), jGeopoint.getDouble("lng"));
//            this.geoPoint = point;
//            this.name = jPlace.getString("name");
//            this.street = jPlace.getString("street");
//            this.city = jPlace.getString("city");

        } catch (JSONException e) {

        }
    }

    public void addMarker(MapView map){
        Marker startMarker = new Marker(map);
        startMarker.setPosition(this.getGeoPoint());
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);
        startMarker.setTitle(this.getName().toUpperCase());

        String content = this.getName();
        if(houseNumber != null && !houseNumber.equals(""))
            content += ", " + this.getHouseNumber();
        if(street != null && !street.equals(""))
            content += ", " + this.getStreet();
        if(city != null && !city.equals(""))
            content += ", " + this.getCity();
        if(country != null && !country.equals(""))
            content += ", " + this.getCountry();
        if(osm_value != null && !osm_value.equals(""))
            content += ", " + this.getOsm_value();

        startMarker.setSnippet(content);
        startMarker.setInfoWindow(new CustomInfoWindow(map));
    }
    static public class CustomInfoWindow extends MarkerInfoWindow {
        POI mSelectedPoi;

        public CustomInfoWindow(org.osmdroid.views.MapView mapView) {
            super(R.layout.custom_marker, mapView);
            Button btn = (Button) (mView.findViewById(R.id.bubble_moreinfo2));
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    //String temp = getMarkerReference().getSnippet();
                    Place temp = new Place(getMarkerReference().getPosition(),getMarkerReference().getSnippet());
                    if(likedList.contains(temp)){
                        Toast.makeText(view.getContext(), "Đã có trong danh sách yêu thích " , Toast.LENGTH_LONG).show();

                    }else {
                        likedList.add(temp);
                        RequestQueue requestqueue = Volley.newRequestQueue(view.getContext());
                        StringRequest stringRequest=new StringRequest(Request.Method.POST, "https://nqphu1998.000webhostapp.com/insertplace.php",
                                new Response.Listener<String>()
                                {
                                    @Override
                                    public void onResponse(String response) {
                                        if(response.trim().equals("success"))
                                        {
                                            Toast.makeText(getView().getContext(), "Đã thêm vào danh sách yêu thích " , Toast.LENGTH_LONG).show();
                                        }
                                        else Toast.makeText(getView().getContext(), "error", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getView().getContext(), "error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        ){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> params=new HashMap<>();
                                params.put("username",user);
                                params.put("lat",String.valueOf(likedList.get(likedList.size()-1).geoPoint.getLatitude()));
                                params.put("long",String.valueOf(likedList.get(likedList.size()-1).geoPoint.getLongitude()));
                                params.put("name",likedList.get(likedList.size()-1).name);
                                params.put("street",likedList.get(likedList.size()-1).street);
                                params.put("city",likedList.get(likedList.size()-1).city);
                                params.put("osm_value",likedList.get(likedList.size()-1).osm_value);
                                params.put("country",likedList.get(likedList.size()-1).country);
                                params.put("housenumber",likedList.get(likedList.size()-1).houseNumber);
                                params.put("descriptions",likedList.get(likedList.size()-1).descriptions);
                                return params;
                            }
                        };
                        requestqueue.add(stringRequest);
                    }
                }
            });
        }

        @Override
        public void onOpen(Object item) {
            super.onOpen(item);
            mView.findViewById(R.id.bubble_moreinfo2).setVisibility(View.VISIBLE);
            Marker marker = (Marker) item;
            mSelectedPoi = (POI) marker.getRelatedObject();
        }
    }
}
