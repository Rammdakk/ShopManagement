package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Product> productArrayList;
    private ProductRVAdapter productRVAdapter;
    private RecyclerView userRV;
    private ProgressBar loadingPB;
    public static String Link;
    public static int pageNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // creating a new array list.
        productArrayList = new ArrayList<>();

        // initializing our views.
        userRV = findViewById(R.id.idRVUsers);
        loadingPB = findViewById(R.id.idPBLoading);
        EditText search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userRV.setMinimumHeight(100);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userRV.setMinimumHeight(100);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
        getDataFromAPI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        int val = Integer.parseInt(prefs.getString(
                getString(R.string.number_key), "1"));
        if (val > 0) {
            pageNumber = val;
        }
        String def ="https://docs.google.com/spreadsheets/d/1HvXfgK2VJBIvJEWVHD4jy4ClPLzfh_l-CUDX0AxiEnA/1";// "https://docs.google.com/spreadsheets/d/1ouJLVVoD9wKNjjMi5FC2Xmf8MmvTSfq5dyTCfi933ak/edit#gid=683048307";
        String sheet_link = prefs.getString(getString(R.string.link_key), def);
        if (sheet_link.contains("https://docs.google.com/spreadsheets/d")) {
            Link = sheet_link;
        } else {
            Link = def;
        }
        Update();
    }

    private void filter(String data) {
        ArrayList<Product> filtered = new ArrayList<>();
        for (Product prod : productArrayList) {
            if (prod.getProduct_name().toLowerCase(Locale.ROOT).contains(data.toLowerCase(Locale.ROOT))) {
                filtered.add(prod);
            }
        }
        productRVAdapter = new ProductRVAdapter(filtered, MainActivity.this);

        userRV.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        userRV.setAdapter(productRVAdapter);
    }

    private void getDataFromAPI() {
        loadingPB.setVisibility(View.GONE);
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = getURL();
            String val = URLReader(url, StandardCharsets.UTF_8);
            JSONArray entryArray = new JSONArray(val);
            for (int i = 0; i < entryArray.length(); i++) {
                JSONObject entryObj = entryArray.getJSONObject(i);
                try {

                    String productName = parseJSONString(entryObj, "Наименование");// entryObj.getString("Наименование");
                    String sellingPrice = parseJSONString(entryObj, "Продажа") + " ₽";
                    String check = parseJSONString(entryObj,"Чек");
                    String photo;
                    try {
                        photo = entryObj.getString("Фото");
                    } catch (JSONException ex){
                        Log.d("Tovar Ex", ex.getMessage());
                        photo="https://ventil34.ru/upload/no_image.jpg";
                    }
                    if (productName.length() > 0 && sellingPrice.length() > 2 && check.length() > 0) {
                        productArrayList.add(new Product(productName, sellingPrice, check, photo));

                        // passing array list to our adapter class.
                        productRVAdapter = new ProductRVAdapter(productArrayList, MainActivity.this);

                        // setting layout manager to our recycler view.
                        userRV.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                        // setting adapter to our recycler view.
                        userRV.setAdapter(productRVAdapter);
                    }
                } catch (Exception ex) {
                    Log.d("Tovar Ex", ex.getMessage());
                }
            }
        } catch (Exception ex) {
            Log.d("EXXXX", ex.getMessage());
        }
    }

    private String parseJSONString(JSONObject entryObj, String name){
        try {
            String val = entryObj.getString(name);
            if (val.length()<=0){
                return "No data";
            }
            return val;
        } catch (JSONException ex){
            Log.d("Tovar Ex", "\n\n\n\n"+ex.getMessage());
            return "No data";
        }
    }

    private URL getURL() throws MalformedURLException {
        try {
            if (Link != null && Link.contains("https://docs.google.com/spreadsheets/d")) {
                Link = Link.replace("https://docs.google.com/spreadsheets/d/", "https://opensheet.elk.sh/");
                Link = Link.substring(0, Link.indexOf("/edit"));
                Link += "/";
                Link += String.valueOf(pageNumber);
//                Toast.makeText(getApplicationContext(), Link,Toast.LENGTH_LONG).show();
                return new URL(Link);
            }
        } catch (Exception ex) {
            return new URL("https://opensheet.elk.sh/1HvXfgK2VJBIvJEWVHD4jy4ClPLzfh_l-CUDX0AxiEnA/1");
        }
        return new URL("https://opensheet.elk.sh/1HvXfgK2VJBIvJEWVHD4jy4ClPLzfh_l-CUDX0AxiEnA/1");
    }

    public static String URLReader(URL url, Charset encoding) {
        String content = "";
        try (Scanner scanner = new Scanner(url.openStream(), String.valueOf(encoding))) {
            content = scanner.useDelimiter("\\A").next();
        } catch (IOException ex) {
            Log.d("Exx", ex.getMessage());
        }
        return content;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.info) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            try {
                dialog.setMessage(getTitle().toString() + " версия " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName +
                        "\r\n\n Автор - Зиганшин Рамиль, гр. БПИ202");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            dialog.setTitle("О программе");
            dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.setIcon(R.mipmap.ic_launcher_round);
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        } else if (id == R.id.update) {
            Update();
        } else if (id == R.id.settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void Update() {
        ArrayList<Product> temp = new ArrayList<>();
        for (Product pro : productArrayList) {
            temp.add(pro);
        }
        productArrayList.clear();
        getDataFromAPI();
        if (productArrayList.size() <= 0) {
            Toast.makeText(getApplicationContext(), "Не удалось обновить", Toast.LENGTH_LONG);
            for (Product pro : temp) {
                productArrayList.add(pro);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}

