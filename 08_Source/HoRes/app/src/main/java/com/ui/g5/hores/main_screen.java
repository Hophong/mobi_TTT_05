package com.ui.g5.hores;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class main_screen extends AppCompatActivity {
    Button btnmenu;
    String url_getdata = "https://nqphu1998.000webhostapp.com/getdata.php";
    ArrayList<User> arrayList;
    String user="",email="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("username")!= null)
        {
            user=(String)  bundle.getString("username");
            email=(String) bundle.getString("email");

        }

        btnmenu=(Button) findViewById(R.id.btnmenu);
        btnmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showmenu();
            }
        });
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
}
