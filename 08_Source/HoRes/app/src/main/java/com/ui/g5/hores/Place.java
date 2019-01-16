package com.ui.g5.hores;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import org.osmdroid.bonuspack.utils.BonusPackHelper;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.ui.g5.hores.main_screen.likedList;

// Lưu thông tin cần thiết của một địa điểm và các thao tác

public class Place {
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
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Place place = (Place) obj;
        return this.getGeoPoint() == place.getGeoPoint();
    }


    public Place(JSONObject jPlace) {
        try {
            JSONObject geometry = jPlace.optJSONObject("geometry");

            JSONArray coordinates = geometry.optJSONArray("coordinates");

            double lng = coordinates.getDouble(0);
            double lat = coordinates.optDouble(1);

            GeoPoint point = new GeoPoint(lat, lng);
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

    public void addMarker(MapView map) {
        Marker startMarker = new Marker(map);
        startMarker.setPosition(this.getGeoPoint());
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);
        startMarker.setTitle(this.getName().toUpperCase());

        String content = this.getName();
        if (houseNumber != null && !houseNumber.equals(""))
            content += ", " + this.getHouseNumber();
        if (street != null && !street.equals(""))
            content += ", " + this.getStreet();
        if (city != null && !city.equals(""))
            content += ", " + this.getCity();
        if (country != null && !country.equals(""))
            content += ", " + this.getCountry();
        if (osm_value != null && !osm_value.equals(""))
            content += ", " + this.getOsm_value();

        startMarker.setSnippet(content);
        startMarker.setInfoWindow(new CustomInfoWindow(map));

    }

    static public class CustomInfoWindow extends MarkerInfoWindow {
        POI mSelectedPoi;

        public CustomInfoWindow(final org.osmdroid.views.MapView mapView) {
            super(R.layout.custom_marker, mapView);
            Button btn = (Button) (mView.findViewById(R.id.bubble_moreinfo2));
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    showDialog(view);
                }
            });
        }

        void showDialog(View view) {
            Dialog dialog = new Dialog(view.getContext());
            dialog.setTitle("MENU");
            dialog.setContentView(R.layout.custom_node);
            dialog.show();

            Button btnLike = (Button) dialog.findViewById(R.id.btnThich);
            Button btnViewComment = (Button) dialog.findViewById(R.id.btnViewComment);
            Button btnComment = (Button) dialog.findViewById(R.id.btnComment);

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Place temp = new Place(getMarkerReference().getPosition(), getMarkerReference().getSnippet());
                    if (likedList.contains(temp)) {
                        Toast.makeText(view.getContext(), "Đã có trong danh sách yêu thích ", Toast.LENGTH_LONG).show();

                    } else {
                        likedList.add(temp);
                        Toast.makeText(view.getContext(), "Đã thêm vào danh sách yêu thích ", Toast.LENGTH_LONG).show();
                    }
                }
            });

            btnViewComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Place temp = new Place(getMarkerReference().getPosition(), getMarkerReference().getSnippet());

                    String result = "";
                    JsonTask jsonTask = new JsonTask();
                    jsonTask.execute("https://horesapp.herokuapp.com/api/comment");
                    try {
                        result = jsonTask.get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        // Danh sach comments
                        JSONArray comments = jsonObject.getJSONArray("comments");
                    } catch (Throwable t) {
                        Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");
                    }

                }
            });

            btnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String username = "Tran Van C";
                    final String content = "Ổn!";
                    final String latitude = "11.213132";
                    final String longitude = "12.312312";

                    RequestQueue requestqueue = Volley.newRequestQueue(v.getContext());
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://horesapp.herokuapp.com/api/comment",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("RESPONSE", response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
//                                    Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                                    Log.d("RESPONSE", "ERROR");
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("username", username.trim());
                            params.put("content", content.trim());
                            params.put("latitude", latitude.trim());
                            params.put("longitude", longitude.trim());
                            return params;
                        }
                    };
                    requestqueue.add(stringRequest);
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

    private static class JsonTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}



