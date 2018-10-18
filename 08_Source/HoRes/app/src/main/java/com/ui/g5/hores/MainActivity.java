package com.ui.g5.hores;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    Button btnSignin;
    ArrayList<User> arrayUser;
    EditText edtUsername;
    EditText edtPassword;
    String url_getdata="http://192.168.1.58:8080/androidwebservice/getdata.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        ReadJson(url_getdata);
        btnSignin=(Button) findViewById(R.id.btnSignin);
        edtUsername=(EditText) findViewById(R.id.edtUsername);
        edtPassword=(EditText) findViewById(R.id.edtPassword);
        arrayUser=new ArrayList<>();
        // ánh xạ map
        //MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragmentmap);
        //mapFragment.getMapAsync(this);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=edtUsername.getText().toString();
                String pass=edtPassword.getText().toString();
                int i;
                int size=arrayUser.size();
                for(i=0;i<size;i++)
                {
                    if(arrayUser.get(i).getUserName()==user && arrayUser.get(i).getPassword()==pass)
                    {
                        Toast.makeText(MainActivity.this, "dang nhap thanh cong", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if(i==size) Toast.makeText(MainActivity.this, "sai", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void ReadJson(String url)
    {
        RequestQueue requestqueue=Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i=0;i<response.length();i++)
                        {
                            try {
                                JSONObject object =response.getJSONObject(i);
                                arrayUser.add(new User(
                                        object.getInt("Id"),
                                        object.getString("UserName"),
                                        object.getString("Email"),
                                        object.getString("Password"),
                                        object.getString("Phone")
                                ));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestqueue.add(jsonArrayRequest);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // show current position
        LatLng startPos = new LatLng(10.805184, 106.721174);
        map.addMarker(new MarkerOptions().position(startPos).title("Vị trí của bạn"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(startPos, 18));

       /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);*/
    }




}
