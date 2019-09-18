package com.byuwur.onlinecongress;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private int DURACION_SPLASH = 1500;

    private DefaultValues dv = new DefaultValues();
    private String URLconex = dv.url + "ver_con.php", URLpass = dv.url + "ver_pass.php", URLdatos = dv.url + "ver_datos.php";
    private RequestQueue rq;
    //set context
    private Context ctx;
    //create request
    private StringRequest jsrqconn, jsrqpass, jsrqdatos;
    private String usrid, usrpass;
    private Boolean loginsesion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //context is this
        ctx = MainActivity.this;
        //requestqueue set to this
        rq = Volley.newRequestQueue(ctx);

        loginsesion = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("loginsesion", false);
        usrid = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("id", null);
        usrpass = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("pass", null);

        vercon(loginsesion, usrid, usrpass);

    }


    private void vercon(final Boolean sesion, final String usrid, final String usrpass) {
        //verify connection
        jsrqconn = new StringRequest(Request.Method.POST, URLconex,
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
                                JSONObject res = resp.getJSONObject(i);
                                Boolean conexion = res.getBoolean("conexion");

                                if (conexion) {
                                    Log.d("Res", "Estamos dentro");
                                    if (sesion && usrpass != null) {
                                        verpass(usrid, usrpass);
                                    } else {
                                        jumpnext();
                                    }
                                } else {
                                    AlertDialog.Builder dialogoerror = new AlertDialog.Builder(ctx);
                                    dialogoerror.setTitle("ERROR");
                                    dialogoerror.setMessage("\nNo es posible contactar al servidor. Verifique su conexión a Internet e inténtelo más tarde.");
                                    dialogoerror.setCancelable(false);
                                    dialogoerror.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogoerror, int id) {
                                            //Ejecute acciones, deje vacio para solo aceptar
                                            dialogoerror.cancel();
                                            finish();
                                        }
                                    });
                                    dialogoerror.show();
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
                        finish();
                    }
                });
                dialogoerror.show();

                Log.d("Error", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return null;
            }
        };
        rq.add(jsrqconn);
    }

    private void verpass(final String id, final String pass) {

        jsrqpass = new StringRequest(Request.Method.POST, URLpass,
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
                                JSONObject res = resp.getJSONObject(i);
                                Boolean success = res.getBoolean("success");
                                Boolean error = res.getBoolean("error");

                                if (error) {
                                    //LOGOUT ACTION
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putBoolean("loginsesion", false).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("id", null).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("nombre", null).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("apellido", null).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("email", null).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("ciudad", null).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("phone", null).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("pass", null).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("sexo", null).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("institucion", null).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("congreso", "").apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("nombrecongreso", null).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("color", "0277bd").apply();

                                    Home.deleteCache(ctx);

                                    AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                    dialogo.setTitle("INICIAR SESIÓN");
                                    dialogo.setMessage("\n" + res.getString("mensaje"));
                                    dialogo.setCancelable(false);
                                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo, int id) {
                                            //Ejecute acciones, deje vacio para solo aceptar
                                            dialogo.cancel();
                                            jumpnext();
                                        }
                                    });
                                    dialogo.show();
                                }
                                if (success) {
                                    Log.d("Res", res.getString("mensaje"));
                                    verdatos(id, pass);
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

                parametros.put("id", id);
                parametros.put("pass", pass);

                return parametros;
            }
        };
        rq.add(jsrqpass);
    }

    private void verdatos(final String id, final String pass) {

        jsrqdatos = new StringRequest(Request.Method.POST, URLdatos,
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
                                JSONObject res = resp.getJSONObject(i);
                                Boolean success = res.getBoolean("success");
                                Boolean error = res.getBoolean("error");

                                if (error) {
                                    AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                    dialogo.setTitle("INICIAR SESIÓN");
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
                                if (success) {
                                    Log.d("Res", res.getString("mensaje"));

                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("nombre", res.getString("usrname")).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("apellido", res.getString("usrape")).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("email", res.getString("usremail")).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("ciudad", res.getString("usrciudad")).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("phone", res.getString("usrcel")).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("sexo", res.getString("usrsex")).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("institucion", res.getString("usrinst")).apply();

                                    jumpnext();
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

                parametros.put("id", id);
                parametros.put("pass", pass);

                return parametros;
            }
        };
        rq.add(jsrqdatos);
    }


    private void jumpnext() {
        //from splash screen to next screen
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(ctx, Firsttime.class);
                startActivity(intent);
                finish();
            }
        }, DURACION_SPLASH);
    }

    @Override
    public void onBackPressed() {
    }

}
