package com.example.jsonlist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.example.jsonlist.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ArrayList<String> userList;
    ArrayAdapter<String> listAdapter;
    Handler mainHandler = new Handler();
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intializeUserList();
        Button button = findViewById(R.id.button);

binding.button.setOnClickListener(
        new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        }
);
        binding.fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new fetchData().start();
            }
        });
    }

    private void intializeUserList() {

        userList = new ArrayList<>();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userList);
        binding.listview.setAdapter(listAdapter);

    }


    class fetchData extends Thread {
        String data = "";

        @Override

        public void run() {


            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Fatching data");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });


            URL url;

            {
                try {
                    url = new URL("https://api.npoint.io/54f0149005dfcbd2965f");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        data = data + line;
                    }

                    if (!data.isEmpty()) {
                        JSONObject jsonObject = new JSONObject(data);
                        JSONArray users = jsonObject.getJSONArray("Users");
                        userList.clear();

                        for (int i = 0; i < users.length(); i++) {
                            JSONObject names = users.getJSONObject(i);
                            String name = names.getString("name");
                            userList.add(name);
                        }
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        listAdapter.notifyDataSetChanged();
                    }
                });

            }

        }
    }
}