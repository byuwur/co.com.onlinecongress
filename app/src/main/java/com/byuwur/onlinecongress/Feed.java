package com.byuwur.onlinecongress;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Feed extends AppCompatActivity {

    private static String usrcorreo, usrnombre;
    private DefaultValues dv = new DefaultValues();
    //login file to request
    private String URLfeed = dv.url + "submitfeed.php", stringasunto="";
    //
    private RequestQueue rq;
    //set context|
    private Context ctx;
    //create request
    private StringRequest jsrqfeed;
    private Spinner spinnerasunto;
    private EditText feedtext;
    private TextView feedenviarcomo;
    private ArrayList<String> asunto = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ayuda y comentarios");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ctx = Feed.this;
        rq = Volley.newRequestQueue(ctx);

        feedtext = findViewById(R.id.feedtext);
        spinnerasunto = findViewById(R.id.spinnerasunto);

        usrcorreo = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("email", null);
        usrnombre = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("nombre", null) + " " +
                getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("apellido", null);

        feedenviarcomo = findViewById(R.id.feedenviarcomo);
        feedenviarcomo.setText("Enviar como: " + usrnombre + "\nCorreo: " + usrcorreo);

        asunto.add("Soporte técnico y ayuda");
        asunto.add("Comentarios y sugerencias");
        ArrayAdapter<String> adapterasunto = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, asunto);
        adapterasunto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerasunto.setAdapter(adapterasunto);
        spinnerasunto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                stringasunto = asunto.get(pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void onClickEnviarFeed(View view) {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();

        jsrqfeed = new StringRequest(Request.Method.POST, URLfeed,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("Response", response.toString());
                        JSONArray resp = null;
                        try {
                            resp = new JSONArray(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        assert resp != null;
                        for (int i = 0; i < resp.length(); i++) {
                            try {
                                progreso.dismiss();
                                JSONObject res = resp.getJSONObject(i);
                                Boolean success = res.getBoolean("success");
                                Boolean error = res.getBoolean("error");

                                if (error) {
                                    AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                    dialogo.setTitle("ERROR");
                                    dialogo.setMessage("\n" + res.getString("mensaje"));
                                    dialogo.setCancelable(false);
                                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo, int id) {
                                            //Ejecute acciones, deje vacio para solo aceptar
                                            dialogo.cancel();
                                        }
                                    });
                                    dialogo.show();
                                }
                                if (success) {
                                    AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                    dialogo.setTitle("COMENTARIO");
                                    dialogo.setMessage("\n" + res.getString("mensaje"));
                                    dialogo.setCancelable(false);
                                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo, int id) {
                                            //Ejecute acciones, deje vacio para solo aceptar
                                            dialogo.cancel();
                                            finish();
                                        }
                                    });
                                    dialogo.show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder dialogoerror = new AlertDialog.Builder(ctx);
                dialogoerror.setTitle("ERROR");
                dialogoerror.setMessage("\nNo es posible contactar al servidor. Verifique su conexión a Internet e inténtelo más tarde.");
                dialogoerror.setCancelable(false);
                dialogoerror.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogoerror, int id) {
                        //Ejecute acciones, deje vacio para solo aceptar
                        dialogoerror.cancel();
                    }
                });
                dialogoerror.show();

                Log.d("Error", error.toString());
                //Toast.makeText(ctx, "Unable to fetch data: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();

                parametros.put("correo", usrcorreo);
                parametros.put("nombre", usrnombre);
                parametros.put("asunto", stringasunto);
                parametros.put("feed", feedtext.getText().toString());

                return parametros;
            }
        };
        rq.add(jsrqfeed);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.action_versioninfo) {
            //Alert de la versión
            AlertDialog.Builder dialogversion = new AlertDialog.Builder(ctx);
            @SuppressLint("InflateParams") View layoutversion = LayoutInflater.from(ctx).inflate(R.layout.dialog_version, null);
            dialogversion.setView(layoutversion);
            dialogversion.show();

        } else if (id == R.id.action_terms) {
            Toast.makeText(ctx, "Términos aún no publicados.",
                    Toast.LENGTH_LONG).show();
        } else if (id == R.id.action_contrib) {
            //Alert de la versión
            AlertDialog.Builder dialogversion = new AlertDialog.Builder(ctx);
            @SuppressLint("InflateParams") View layoutversion = LayoutInflater.from(ctx).inflate(R.layout.dialog_contrib, null);
            dialogversion.setView(layoutversion);
            dialogversion.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}