package com.byuwur.onlinecongress;

import android.content.Context;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Congresos extends AppCompatActivity {
    DefaultValues dv = new DefaultValues();
    private ArrayList<String> congreso = new ArrayList<>(), idcongreso = new ArrayList<>(), colorcongreso = new ArrayList<>(), logocongreso = new ArrayList<>();
    private String buscaridcongreso = "", URLcon = dv.url + "congresos.php";
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
        nombrecongreso = findViewById(R.id.textcongreso);
        buttoncongreso = findViewById(R.id.buttoncongreso);
        imgcongreso = findViewById(R.id.imgcongreso);
        congresos = findViewById(R.id.congresos);

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("congreso", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("nombrecongreso", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("logo", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("color", null).apply();

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
                congresos.setBackgroundColor(Color.parseColor("#" + colorcongreso.get(pos)));
                if (pos == 0) {
                    buscaridcongreso = "";
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
                                //Log.d("Response: ", "ID:"+res.getString("IDDEPARTAMENTOS")+". Nombre: "+res.getString("NOMBREDEPARTAMENTO"));
                                congreso.add(res.getString("Nombre") + " (" + res.getString("Año") + ")");
                                idcongreso.add(res.getString("Id"));
                                colorcongreso.add("Color");
                                logocongreso.add("Logo");
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

    }
}
