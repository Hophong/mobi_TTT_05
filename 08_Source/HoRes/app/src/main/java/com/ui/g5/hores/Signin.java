package com.ui.g5.hores;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("ValidFragment")
public class Signin extends Fragment {

    View view;
    ArrayList<User> arrayUser;

    Button btnSignin;
    EditText edtUsername,edtPassword;
    TextView tvSignup, tvHaveAccount;

    String url_getdata = "https://nqphu1998.000webhostapp.com/getdata.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Signup signup =(Signup)getFragmentManager().findFragmentById(R.id.Layoutsignup);
        view = inflater.inflate(R.layout.signin,container,false);

        ReadJson(url_getdata);

        anhxa();

       btnSignin.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String user=edtUsername.getText().toString();
                   String pass=edtPassword.getText().toString();
                   int i=0;
                   int check=0;
                   int size=arrayUser.size();
                   for(i=0;i<size;i++)
                   {
                       if(arrayUser.get(i).getUserName().equals(user) && arrayUser.get(i).getPassword().equals(pass))
                       {
                           Toast.makeText(getActivity(), "dang nhap thanh cong", Toast.LENGTH_SHORT).show();
                           check=1;
                           Intent intent=new Intent(getActivity(),main_screen.class);
                            intent.putExtra("username",user);
                            String email=arrayUser.get(i).getEmail().toString();
                           intent.putExtra("email",email);



                           startActivity(intent);
                       }
                   }
                   if(check==0) Toast.makeText(getActivity(), "TÀI KHOẢN KHÔNG TỒN TẠI", Toast.LENGTH_SHORT).show();
               }

        });



        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Signup signup=new Signup();
                FragmentTransaction ft= getFragmentManager().beginTransaction();
                Bundle bundle=new Bundle();
                bundle.putCharSequenceArrayList("User", (ArrayList) arrayUser);
                signup.setArguments(bundle);
                ft.replace(R.id.emptyLayout, signup);
                ft.commit();
            }
        });

        tvHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft= getFragmentManager().beginTransaction();
                ft.replace(R.id.emptyLayout, new Signup());
                ft.commit();
            }
        });


        return view;
    }

    private void anhxa()
    {
        arrayUser       = new ArrayList<>();
        btnSignin       =   (Button) view.findViewById(R.id.btnSignin);
        edtUsername     =   (EditText) view.findViewById(R.id.edtUsername);
        edtPassword     =   (EditText) view.findViewById(R.id.edtPassword);
        tvSignup        =   (TextView) view.findViewById(R.id.tvSignup);
        tvHaveAccount   =   (TextView) view.findViewById(R.id.tvhaveAcount);
    }



    public void ReadJson(String url)
    {
        RequestQueue requestqueue=Volley.newRequestQueue(getActivity());

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
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestqueue.add(jsonArrayRequest);
    }
}
