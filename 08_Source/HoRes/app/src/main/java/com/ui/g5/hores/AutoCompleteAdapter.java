<<<<<<< Updated upstream
package com.ui.g5.hores;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    ArrayList<String> data ;
    ArrayList<Place> arrayPlace;
    String link = "http://photon.komoot.de/api/?q=";

    AutoCompleteAdapter(@NonNull Context context, @LayoutRes int resource){
        super(context,resource);
        this.data = new ArrayList<>();
        arrayPlace = new ArrayList<Place>();
    }

    public AutoCompleteAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.data = new ArrayList<>();
        arrayPlace = new ArrayList<Place>();
    }

    @Override
    public int getCount(){
        if(data != null)
            return data.size();
        return 0;
    }

    @Nullable
    @Override
    public String getItem(int pos){
        return data.get(pos);
    }

    @NonNull
    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if(constraint != null) {
                    try {

                        String url = link + URLEncoder.encode(constraint.toString(),"UTF-8");
                        arrayPlace = new JsonTask().execute(url).get();
                        data.clear();
                        for(int i=0; i < arrayPlace.size(); i++){
                            Place place = arrayPlace.get(i);
                            String text = place.getName() + ", " + place.getStreet() + ", " + place.getCity();
                            data.add(text);
                        }

                    }catch (Exception e){
                        Log.e("My app","Exception",e);
                    }
                    results.values = data;
                    results.count = data.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count >0){

                    notifyDataSetChanged();
                }else{
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    public ArrayList<Place> getArrayPlace() {
        return arrayPlace;
    }

    private class JsonTask extends AsyncTask<String, Place, ArrayList<Place>> {
        ProgressDialog pd;
        ArrayList<Place> places;

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(getContext());
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            places = new ArrayList<Place>();
            //pd.show();
        }

        @Override
        protected void onProgressUpdate(Place... values) {
            super.onProgressUpdate(values);
            places.add(values[0]);
        }

        protected ArrayList<Place> doInBackground(String... params) {


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

                //ArrayList arrayList = new ArrayList();


                JSONObject jsonData = new JSONObject(buffer.toString());
                JSONArray arrayAddress = jsonData.optJSONArray("features");
                for (int i = 0; i < arrayAddress.length(); i++) {
                    JSONObject jsonAddress = arrayAddress.optJSONObject(i);
                    Place place = new Place(jsonAddress);
                    onProgressUpdate(place);

                }
                return places;
            }catch(Exception e){
                    e.printStackTrace();
                    return null;

                } finally{
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
        }

        @Override
        protected void onPostExecute(ArrayList result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }

        }
    }
}
=======
package com.ui.g5.hores;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    ArrayList<String> data ;
    ArrayList<Place> arrayPlace;
    String link = "http://photon.komoot.de/api/?q=";

    AutoCompleteAdapter(@NonNull Context context, @LayoutRes int resource){
        super(context,resource);
        this.data = new ArrayList<>();
        arrayPlace = new ArrayList<Place>();
    }

    public AutoCompleteAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
        this.data = new ArrayList<>();
        arrayPlace = new ArrayList<Place>();
    }

    @Override
    public int getCount(){
        if(data != null)
            return data.size();
        return 0;
    }

    @Nullable
    @Override
    public String getItem(int pos){
        return data.get(pos);
    }

    @NonNull
    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if(constraint != null) {
                    try {

                        String url = link + URLEncoder.encode(constraint.toString(),"UTF-8");
                        arrayPlace = new JsonTask().execute(url).get();
                        data.clear();
                        for(int i=0; i < arrayPlace.size(); i++){
                            Place place = arrayPlace.get(i);
                            String text = place.getName() + ", " + place.getStreet() + ", " + place.getCity();
                            data.add(text);
                        }

                    }catch (Exception e){
                        Log.e("My app","Exception",e);
                    }
                    results.values = data;
                    results.count = data.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results != null && results.count >0){

                    notifyDataSetChanged();
                }else{
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    public ArrayList<Place> getArrayPlace() {
        return arrayPlace;
    }

    private class JsonTask extends AsyncTask<String, Place, ArrayList<Place>> {
        ProgressDialog pd;
        ArrayList<Place> places;

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(getContext());
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            places = new ArrayList<Place>();
            //pd.show();
        }

        @Override
        protected void onProgressUpdate(Place... values) {
            super.onProgressUpdate(values);
            places.add(values[0]);
        }

        protected ArrayList<Place> doInBackground(String... params) {


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

                //ArrayList arrayList = new ArrayList();


                JSONObject jsonData = new JSONObject(buffer.toString());
                JSONArray arrayAddress = jsonData.optJSONArray("features");
                for (int i = 0; i < arrayAddress.length(); i++) {
                    JSONObject jsonAddress = arrayAddress.optJSONObject(i);
                    Place place = new Place(jsonAddress);
                    onProgressUpdate(place);

                }
                return places;
            }catch(Exception e){
                    e.printStackTrace();
                    return null;

                } finally{
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
        }

        @Override
        protected void onPostExecute(ArrayList result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }

        }
    }
}
>>>>>>> Stashed changes
