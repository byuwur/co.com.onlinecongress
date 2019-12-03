package com.byuwur.onlinecongress;

import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

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

public class MainActivity extends AppCompatActivity {
    private DefaultValues dv = new DefaultValues();
    private String URLconex = dv.url + "ver_con.php", URLpass = dv.url + "ver_pass.php", URLdatos = dv.url + "ver_datos.php";
    private RequestQueue rq;
    private Context ctx;
    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //context is this
        ctx = MainActivity.this;
        //requestqueue set to this
        rq = Volley.newRequestQueue(ctx);

        boolean loginsesion = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("loginsesion", false);
        String usrid = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("id", null);
        String usrpass = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("pass", null);
        String congreso = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("congreso", null);

        if (congreso != null) {
            loadcongress();
            vercon(loginsesion, usrid, usrpass);
        } else {
            vercon(loginsesion, usrid, usrpass);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        // Get token
        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        // [END retrieve_current_token]
    }

    private void vercon(final boolean sesion, final String usrid, final String usrpass) {
        StringRequest jsrqconn = new StringRequest(Request.Method.POST, URLconex,
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
                                boolean conexion = res.getBoolean("conexion");

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
        StringRequest jsrqpass = new StringRequest(Request.Method.POST, URLpass,
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
                                boolean success = res.getBoolean("success");
                                boolean error = res.getBoolean("error");

                                if (error) {
                                    logout();
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
        StringRequest jsrqdatos = new StringRequest(Request.Method.POST, URLdatos,
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
                                boolean success = res.getBoolean("success");
                                boolean error = res.getBoolean("error");

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
                                            .putString("pais", res.getString("usrpais")).apply();
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
        int DURACION_SPLASH = 2000;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(ctx, Firsttime.class);
                startActivity(intent);
                finish();
            }
        }, DURACION_SPLASH);
    }

    private void loadcongress() {
        int DURACION_SPLASH = 1500;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                String idcongreso = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .getString("congreso", null);
                View main = findViewById(R.id.main);
                ObjectAnimator.ofObject(main, "backgroundColor", new ArgbEvaluator(), Color.parseColor("#0277bd"), Color.parseColor(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("color", ""))).setDuration(500).start();
                final ImageView logo = findViewById(R.id.logo);
                Picasso.get().load(dv.urlraiz + "congreso/Fotografias/Logos_Congresos/" + idcongreso + "/1.jpg")
                        .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                        .fit().centerInside()
                        .into(logo, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("Carga", "Cargada");
                            }

                            @Override
                            public void onError(Exception e) {
                                logo.setImageResource(R.drawable.logo);
                            }
                        });
            }
        }, DURACION_SPLASH);
    }

    private void logout() {
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
                .putString("pais", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("phone", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("pass", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("sexo", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("institucion", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("congreso", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("nombrecongreso", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("color", "0277bd").apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putInt("notif", 0).apply();
    }

    @Override
    public void onBackPressed() {
    }


}
