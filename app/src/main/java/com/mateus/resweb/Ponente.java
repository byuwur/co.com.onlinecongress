package com.mateus.resweb;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Ponente extends AppCompatActivity {
    private DefaultValues dv = new DefaultValues();
    //register file to request
    private String URLdatosreserva=dv.urlcanchas+"datosreserva.php", URLcancelreserva=dv.urlcanchas+"reserva/rmreserv.php";
    private Toolbar toolbar;
    private static String usrid, idc, idr;
    private TextView nombrecancha, diar, horainicio, horafin, tarifacancha, telefonocancha, direccioncancha, ciudadcancha;
    //array to present photos
    private String[] urlimages = new String[]{
            dv.imgcanchasurl+idc+"/1.jpg", dv.imgcanchasurl+idc+"/2.jpg", dv.imgcanchasurl+idc+"/3.jpg"
    };
    //viewflipper de las fotos de la cancha
    private ViewPager pagerfotos;
    //imageviews to click on preview
    private ImageView canchaimg1, canchaimg2, canchaimg3;
    //BUTTONS
    private Button buttoninfo, buttoncancelar;
    //set context
    private Context ctx;
    //
    private RequestQueue rq;
    //create request
    private StringRequest jsrqcancelreserva, jsrqdatosreserva;
    //autoscroll values
    private int currentPage = 0;
    private Timer timer;
    private final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    private final long PERIOD_MS = 5000; // time in milliseconds between successive task executions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_ponente);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(idc);

        ctx= Ponente.this;
        rq = Volley.newRequestQueue(ctx);

        usrid = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("id", null);

        nombrecancha = findViewById(R.id.nombrecancha);
        diar = findViewById(R.id.diascancha);
        horainicio = findViewById(R.id.horainicio);
        horafin = findViewById(R.id.horafin);
        tarifacancha = findViewById(R.id.tarifacancha);
        telefonocancha = findViewById(R.id.telefonocancha);
        direccioncancha = findViewById(R.id.ubicacioncancha);
        ciudadcancha = findViewById(R.id.ciudadcancha);

        buttoninfo = findViewById(R.id.buttoninfo);
        buttoncancelar = findViewById(R.id.buttoncancelar);

        canchaimg1 = findViewById(R.id.canchaimg1);
        canchaimg2 = findViewById(R.id.canchaimg2);
        canchaimg3 = findViewById(R.id.canchaimg3);

        buttoninfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ponencia ponencia = new Ponencia();
                ponencia.setid(idc);

                Intent intent1 = new Intent(ctx, Ponencia.class);
                startActivity(intent1);
            }
        });

        buttoncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ctx); dialog.setCancelable(false);
                dialog.setTitle("CANCELAR");
                dialog.setMessage("¿Desea cancelar esta reserva?");
                dialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", then he is allowed to exit from application
                        cancelreserva();
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user select "No", just cancel this dialog and continue with app
                        dialog.cancel();
                    }
                });
                dialog.create();
                dialog.show();
            }
        });

        pagerfotos = findViewById(R.id.viewpagerfotos);
        AdaptadorFotos adfotos = new AdaptadorFotos(ctx, urlimages);
        pagerfotos.setAdapter(adfotos);

        //USING TIMER FOR AUTO SCROLLING
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == urlimages.length) {
                    currentPage = 0;
                }
                pagerfotos.setCurrentItem(currentPage++, true);
            }
        };
        timer = new Timer(); // This will create a new Thread
        timer .schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
        //END OF AUTO SCROLLING

        loadimg();
        datosreserva();
        //
    }

    private void datosreserva(){
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();

        jsrqdatosreserva = new StringRequest(Request.Method.POST, URLdatosreserva,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray resp = null;
                        try {
                            resp = new JSONArray( response );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        assert resp != null;
                        for (int i = 0; i < resp.length(); i++) {
                            try {
                                progreso.dismiss();
                                JSONObject res = resp.getJSONObject(i);
                                if (res.has("error")) {
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
                                }
                                else {
                                    nombrecancha.setText(res.getString("NOMBRECANCHA"));
                                    diar.setText(res.getString("FECHA"));
                                    horainicio.setText(res.getString("HORAINICIO"));
                                    horafin.setText(res.getString("HORAFIN"));
                                    tarifacancha.setText("COP$" + res.getString("TARIFA") + "/hr");
                                    telefonocancha.setText(res.getString("TELEFONOCANCHA"));
                                    direccioncancha.setText(res.getString("DIRCANCHA"));
                                    ciudadcancha.setText(res.getString("NOMBRECIUDAD"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.dismiss();

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
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();

                parametros.put("id", Ponente.idr);

                return parametros;
            }
        };
        rq.add(jsrqdatosreserva);
    }

    private void cancelreserva(){
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();

        jsrqcancelreserva = new StringRequest(Request.Method.POST, URLcancelreserva,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray resp = null;
                        try {
                            resp = new JSONArray( response );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        assert resp != null;
                        for (int i = 0; i < resp.length(); i++) {
                            try {
                                progreso.dismiss();
                                JSONObject res = resp.getJSONObject(i);

                                    Boolean error = res.getBoolean("error");
                                    if (error) {
                                        AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                        dialogo.setTitle("CANCELAR");
                                        dialogo.setMessage("\n" + res.getString("mensaje"));
                                        dialogo.setCancelable(false);
                                        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogo, int id) {
                                                //Ejecute acciones, deje vacio para solo aceptar
                                                dialogo.cancel();
                                            }
                                        });
                                        dialogo.show();
                                    } else {
                                        AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                        dialogo.setTitle("CANCELAR");
                                        dialogo.setMessage("\n" + res.getString("mensaje"));
                                        dialogo.setCancelable(false);
                                        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogo, int id) {
                                                //Ejecute acciones, deje vacio para solo aceptar
                                                finish();
                                                dialogo.cancel();
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
                progreso.dismiss();

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
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();

                parametros.put("idu", Ponente.usrid);
                parametros.put("idr", Ponente.idr);

                return parametros;
            }
        };
        rq.add(jsrqcancelreserva);
    }

    private void loadimg(){
        Picasso.get().load(urlimages[0])
                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                .fit().centerCrop()
                .into(canchaimg1, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Carga","Cargada");
                    }
                    @Override
                    public void onError(Exception e) {
                        canchaimg1.setImageResource(R.drawable.no_image);
                    }
                });

        Picasso.get().load(urlimages[1])
                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                .fit().centerCrop()
                .into(canchaimg2, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Carga","Cargada");
                    }
                    @Override
                    public void onError(Exception e) {
                        canchaimg2.setImageResource(R.drawable.no_image);
                    }
                });

        Picasso.get().load(urlimages[2])
                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                .fit().centerCrop()
                .into(canchaimg3, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Carga","Cargada");
                    }
                    @Override
                    public void onError(Exception e) {
                        canchaimg3.setImageResource(R.drawable.no_image);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id==android.R.id.home) {
            resetdata();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setid(String idc, String idr){
        Ponente.idc =idc;
        Ponente.idr =idr;
    }
    private void resetdata(){
        idc=null;
        idr=null;
    }

    @Override
    public void onBackPressed() {
        resetdata();
        finish();
    }
}
