package com.byuwur.onlinecongress;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Congresos extends AppCompatActivity {
    DefaultValues dv = new DefaultValues();
    private ArrayList<String> congreso = new ArrayList<>(), idcongreso = new ArrayList<>(), colorcongreso = new ArrayList<>(), logocongreso = new ArrayList<>();
    private String usrid, buscaridcongreso = "", buscarcolorcongreso = "", buscarnombrecongreso = "", URLcon = dv.url + "congresos.php", URLfotoperfil = dv.imgfotoperfil, URLimgcon = dv.urlraiz + "img-main/logo.png";
    private Context ctx;
    private TextView nombrecongreso;
    private JsonArrayRequest jsrqpais;
    private Button buttoncongreso;
    private RequestQueue rq;
    private ImageView imgcongreso;
    private View congresos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congresos);

        ctx = Congresos.this;
        rq = Volley.newRequestQueue(ctx);
        final Spinner spinnercongreso = findViewById(R.id.spinnercongreso);
        nombrecongreso = findViewById(R.id.nombrecongreso);
        buttoncongreso = findViewById(R.id.buttoncongreso);
        imgcongreso = findViewById(R.id.imgcongreso);
        congresos = findViewById(R.id.congresos);

        usrid = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("id", null);

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("congreso", "").apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("nombrecongreso", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("color", "0277bd").apply();

        //initial values
        congreso.add("[--- Seleccionar congreso ---]");
        idcongreso.add("0");
        colorcongreso.add("0277bd");
        logocongreso.add("");
        llenarcongresos();
        //set the spinner value from Arraylist
        ArrayAdapter<String> adaptercon = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, congreso);
        adaptercon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnercongreso.setAdapter(adaptercon);
        spinnercongreso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                //Toast.makeText(ctx, adapterView.getItemAtPosition(pos)+". "+idciudad.get(pos), Toast.LENGTH_SHORT).show();
                buscaridcongreso = idcongreso.get(pos);
                buscarcolorcongreso = colorcongreso.get(pos);
                buscarnombrecongreso = congreso.get(pos);
                nombrecongreso.setText(buscarnombrecongreso);
                congresos.setBackgroundColor(Color.parseColor("#" + buscarcolorcongreso));
                //LOAD IMAGE
                Picasso.get().load(URLfotoperfil + usrid + "/1.jpg")
                        .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                        .fit().centerInside()
                        .into(imgcongreso, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("Carga", "Cargada");
                            }
                            @Override
                            public void onError(Exception e) { imgcongreso.setImageResource(R.drawable.logo); }
                        });
                if (pos == 0) {
                    buscaridcongreso = ""; buscarcolorcongreso = ""; buscarnombrecongreso = "";
                    nombrecongreso.setText("CONGRESO VIRTUAL");
                    congresos.setBackgroundColor(Color.parseColor("#" + colorcongreso.get(pos)));
                    Picasso.get().load(URLimgcon)
                            .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                            .fit().centerInside()
                            .into(imgcongreso, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Log.d("Carga", "Cargada");
                                }
                                @Override
                                public void onError(Exception e) { imgcongreso.setImageResource(R.drawable.logo); }
                            });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        buttoncongreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickCongreso();
            }
        });
    }

    private void llenarcongresos() {
        jsrqpais = new JsonArrayRequest(Request.Method.GET, URLcon,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject res = response.getJSONObject(i);
                                Log.d("Response: ", "ID:"+res.getString("Id")+". Color: "+res.getString("Color")+". Logo: "+res.getString("Logo"));
                                congreso.add(res.getString("Nombre") + " (" + res.getString("Año") + ")");
                                idcongreso.add(res.getString("Id"));
                                colorcongreso.add(res.getString("Color"));
                                logocongreso.add(res.getString("Logo"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.toString());
            }
        });
        rq.add(jsrqpais);
    }

    private void onclickCongreso(){
        if (buscaridcongreso.equals("") || buscaridcongreso == null) {
            Toast.makeText(ctx, "Por favor, seleccione un congreso.", Toast.LENGTH_LONG).show();
        } else {
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putString("congreso", buscaridcongreso).apply();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putString("nombrecongreso", buscarnombrecongreso).apply();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putString("color", buscarcolorcongreso).apply();
            Log.d("ParamsCONGRESO", "Congreso: " + buscaridcongreso + ". Nombre congreso: " + buscarnombrecongreso + ". Color congreso: " + buscarcolorcongreso + ".");
            Intent intentiniciar = new Intent(Congresos.this, Home.class);
            startActivity(intentiniciar);
            finish();
        }
    }

    @Override
    public void onBackPressed() { Toast.makeText(ctx, "Por favor, seleccione un congreso.", Toast.LENGTH_LONG).show();}
}
