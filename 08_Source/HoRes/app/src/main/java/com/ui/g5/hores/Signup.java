package com.ui.g5.hores;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Signup extends Fragment{
    View view;

    EditText edtUsernam_reg,edtEmail_reg,edtPassword_reg,edtPhone_reg;
    TextView tvsignin;
    Button btnregiter;
    ArrayList<User> arrayUser=new ArrayList<>();
    String url_insertdata="https://nqphu1998.000webhostapp.com/insertdata.php";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String phonePatten="^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.signup,container,false);
        Bundle bundle=getArguments();
        if(bundle!=null)
        {
            arrayUser=(ArrayList) bundle.getCharSequenceArrayList("User");
        }
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
                }
                else if(email.matches(emailPattern)!=true) {
                    Toast.makeText(getActivity(), "invalid email", Toast.LENGTH_SHORT).show();
                }
                else if (phone.matches(phonePatten)!=true)
                {
                    Toast.makeText(getActivity(), "invalid phone", Toast.LENGTH_SHORT).show();
                }
                else if(checkUserName_Email(user,email,arrayUser)!=true)
                {
                    Toast.makeText(getActivity(), "username or email da ton tai", Toast.LENGTH_SHORT).show();
                }
                else {
                    Insert(url_insertdata);
                }
            }
        });

         tvsignin.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //Intent intent=new Intent(getActivity(),Signin.class);
                 //startActivity(intent);

                 Signin signin=new Signin();
                 FragmentTransaction ft= getFragmentManager().beginTransaction();
                 ft.replace(R.id.emptyLayout, signin);
                 ft.commit();
             }
         });


        return view;
    }
    private  boolean checkUserName_Email(String UserName,String Email,ArrayList<User> users)
    {
        for (int i=0;i<users.size();i++)
        {
            if(users.get(i).getUserName().equals(UserName)||users.get(i).getEmail().equals(Email)) return false;
        }
        return true;
    }
    private void Anhxa() {
        btnregiter      =   (Button) view.findViewById(R.id.btnregiter);
        edtUsernam_reg  =   (EditText) view.findViewById(R.id.edtUsername_reg);
        edtEmail_reg    =   (EditText) view.findViewById(R.id.edtEmail_reg);
        edtPassword_reg =   (EditText) view.findViewById((R.id.edtPassword_reg));
        edtPhone_reg    =   (EditText) view.findViewById((R.id.edtphone_reg));
        tvsignin      =   (TextView) view.findViewById(R.id.signin);
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
