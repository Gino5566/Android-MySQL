package com.android.example.myprojectmk2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        ImageView addimage=findViewById(R.id.addImage);
        ImageView reNewImage=findViewById(R.id.reNewimage);
        ImageView clearAllImage=findViewById(R.id.clearAllImage);

        listView = findViewById(R.id.listView);

        String url="http://192.168.1.66/ListView_Android/SaveToMySql.php";
        List<ClassItem> classItems = new ArrayList<>();
        String path="http://192.168.1.66/ListView_Android/";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject item = response.getJSONObject(i);
                                String url =  item.getString("path");
                                String imagePath=path+url;
                                String na = item.getString("name");
                                String[] nam=na.split("-");
                                String name=nam[1].replace(".jpg","");

                                ClassItem classItem=new ClassItem(imagePath,name);
                                classItems.add(classItem);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        ItemAdapter adapter=new ItemAdapter(MainActivity.this,R.layout.listview_item,classItems);
                        listView.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue.add(request);

        addimage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(MainActivity.this,UploadImagetoMySQL.class);
                startActivity(intent);
            }
        });

        reNewImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String url="http://192.168.1.66/ListView_Android/SaveToMySql.php";
                List<ClassItem> classItems = new ArrayList<>();
                String path="http://192.168.1.66/ListView_Android/";
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject item = response.getJSONObject(i);
                                        String url =  item.getString("path");
                                        String imagePath=path+url;
                                        String na = item.getString("name");
                                        String[] nam=na.split("-");
                                        String name=nam[1].replace(".jpg","");

                                        ClassItem classItem=new ClassItem(imagePath,name);
                                        classItems.add(classItem);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                ItemAdapter adapter=new ItemAdapter(MainActivity.this,R.layout.listview_item,classItems);
                                listView.setAdapter(adapter);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                            }
                        });

                requestQueue.add(request);

            }
        });

        clearAllImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String url ="http://192.168.1.66/ListView_Android/clearAll.php";
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                                adapter.clear();
                                adapter.notifyDataSetChanged();

                                String resp=response;
                                Toast.makeText(getApplicationContext(),resp,Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                    }
                });
                requestQueue.add(stringRequest);
            }
        });
    }

}