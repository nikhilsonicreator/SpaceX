package com.entwickler.spacex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.entwickler.spacex.Adapter.PersonAdapter;
import com.entwickler.spacex.Model.PersonClass;
import com.entwickler.spacex.Room.MyDatabase;
import com.entwickler.spacex.Room.PersonRoom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static ProgressDialog progressDialog;
    private static List<PersonClass> person_list;
    private RecyclerView recyclerView;
    private static PersonAdapter personAdapter;
    private static Context my_context;
    private Button delete_room_btn, refresh_btn;
    private String api="https://api.spacexdata.com/v4/crew";
    private static boolean del =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        person_list=new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        refresh_btn = findViewById(R.id.refesh_btn);
        delete_room_btn = findViewById(R.id.delete_room_btn);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        personAdapter = new PersonAdapter(this, person_list);
        recyclerView.setAdapter(personAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        my_context = this;

        del = false;

        delete_room_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MyDatabase myDatabase = Room.databaseBuilder(my_context, MyDatabase.class, "PersonDB").allowMainThreadQueries().build();
                List<PersonRoom> countries_data = myDatabase.dao().getPerson();
                if (countries_data==null || countries_data.isEmpty()){
                    Toast.makeText(MainActivity.this, "Database Already Empty", Toast.LENGTH_SHORT).show();
                }

                else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("You want to delete local database");
                    builder.setTitle("Are you sure ?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            myDatabase.dao().deleteAll();
                            Toast.makeText(MainActivity.this, "Database Deleted Successfully", Toast.LENGTH_SHORT).show();
                            if (del){
                                person_list.clear();
                                personAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {

                        }
                    });

                    builder.show();
                }


            }
        });

        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                person_list.clear();
                personAdapter.notifyDataSetChanged();
                start_api_call();
            }
        });

        start_api_call();

    }

    private void start_api_call(){
        DownloadTask dt = new DownloadTask();
        try {
            progressDialog.show();
            person_list.clear();
            dt.execute(api);
        } catch (Exception e) {
            progressDialog.dismiss();
            Log.i("error", e.getMessage() + "");
            Toast.makeText(this, "Unable to Load Results", Toast.LENGTH_SHORT).show();
        }
    }

    public static class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... Params) {
            URL url;
            HttpURLConnection urlConnection;
            String result = "";
            try {
                url = new URL(Params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();

                while (data != -1) {
                    char c = (char) data;
                    result += c;
                    data = reader.read();
                }

                JSONArray jsonArray = new JSONArray(result);

                if (jsonArray.length() == 0) {
                    return "no result";
                }

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject obj = jsonArray.getJSONObject(i);

                    String name = obj.getString("name");
                    String agency = obj.getString("agency");
                    String image = obj.getString("image");
                    String wikipedia = obj.getString("wikipedia");
                    String status = obj.getString("status");


                    byte[] img = recoverImageFromUrl(image);


                    PersonClass personClass = new PersonClass(name,agency,wikipedia,image,img,status);

                    person_list.add(personClass);

                    MyDatabase myDatabase = Room.databaseBuilder(my_context, MyDatabase.class, "PersonDB").build();

                    List<PersonRoom> person_data = myDatabase.dao().getPerson();
                    if (person_data.size() != 13) {

                        PersonRoom country = new PersonRoom(name, agency, wikipedia,image,img, status);
                        myDatabase.dao().personInsertion(country);
                    }

                    myDatabase.close();

                }

                return " ";

            } catch (IOException e) {
                Log.i("error", e.getMessage() + "");
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s!=null){
                if (s.equals("no result")){
                    Toast.makeText(my_context, "Unable to load results", Toast.LENGTH_SHORT).show();
                }
                personAdapter.notifyDataSetChanged();
                Log.i("ayay","yaha"+person_list);
            }

            if (person_list.isEmpty()) {

                MyDatabase myDatabase = Room.databaseBuilder(my_context, MyDatabase.class, "PersonDB").allowMainThreadQueries().build();
                List<PersonRoom> person_data = myDatabase.dao().getPerson();
                if (person_data.size() == 13) {

                    for (int k = 0; k < person_data.size(); k++) {

                        PersonClass countryClass = new PersonClass(person_data.get(k).getName(),
                                person_data.get(k).getAgency(), person_data.get(k).getWikipedia(),
                                person_data.get(k).getImage(),person_data.get(k).getImg(), person_data.get(k).getStatus());

                        person_list.add(countryClass);

                    }
                    del = true;
                    personAdapter.notifyDataSetChanged();

                    myDatabase.close();

                } else {
                    Toast.makeText(my_context, "No Data In Database", Toast.LENGTH_SHORT).show();
                }

            }

            progressDialog.dismiss();
        }
    }


    public static byte[] recoverImageFromUrl(String urlText) throws Exception {
        URL url = new URL(urlText);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (InputStream inputStream = url.openStream()) {
            int n = 0;
            byte [] buffer = new byte[ 1024 ];
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
        }

        return output.toByteArray();
    }

}