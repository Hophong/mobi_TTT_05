package com.ui.g5.hores;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

// Lưu thông tin cần thiết của một địa điểm và các thao tác

public class Place {
    GeoPoint geoPoint;
    String name;
    String street;
    String city;

    public Place(GeoPoint geoPoint, String name, String street, String city) {
        this.geoPoint = geoPoint;
        this.name = name;
        this.street = street;
        this.city = city;
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
        startMarker.setTitle(this.getName());
    }
}
