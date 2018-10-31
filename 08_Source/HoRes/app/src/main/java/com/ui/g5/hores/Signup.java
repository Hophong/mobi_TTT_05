package com.ui.g5.hores;

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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Signup extends Fragment{
    View view;

    EditText edtUsernam_reg,edtEmail_reg,edtPassword_reg,edtPhone_reg;
    Button btnregiter;

    String url_insertdata="https://nqphu1998.000webhostapp.com/insertdata.php";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.signup,container,false);

        Anhxa();

         btnregiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edtUsernam_reg.getText().toString().trim();
                String email = edtEmail_reg.getText().toString().trim();
                String pass = edtPassword_reg.getText().toString().trim();
                String phone = edtPhone_reg.getText().toString().trim();

                if (user.isEmpty() || email.isEmpty() || pass.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(getActivity(), "nhap lai", Toast.LENGTH_SHORT).show();
                } else {
                    Insert(url_insertdata);
                }
            }
        });


        return view;
    }

    private void Anhxa() {
        btnregiter      =   (Button) view.findViewById(R.id.btnregiter);
        edtUsernam_reg  =   (EditText) view.findViewById(R.id.edtUsername_reg);
        edtEmail_reg    =   (EditText) view.findViewById(R.id.edtEmail_reg);
        edtPassword_reg =   (EditText) view.findViewById((R.id.edtPassword_reg));
        edtPhone_reg    =   (EditText) view.findViewById((R.id.edtphone_reg));
    }

    private void Insert(String url)
    {
        RequestQueue requestqueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                @Override
                    public void onResponse(String response) {
                    if(response.trim().equals("success"))
                    {
                        Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                        FragmentTransaction ft= getFragmentManager().beginTransaction();
                        ft.replace(R.id.emptyLayout, new Signin());
                        ft.commit();
                    }
                    else Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("username",edtUsernam_reg.getText().toString().trim());
                params.put("email",edtEmail_reg.getText().toString().trim());
                params.put("password",edtPassword_reg.getText().toString().trim());
                params.put("phone",edtPassword_reg.getText().toString().trim());
                return params;
            }
        };
        requestqueue.add(stringRequest);
    }
}
