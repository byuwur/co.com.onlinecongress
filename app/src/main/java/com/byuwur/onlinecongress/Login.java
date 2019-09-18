package com.byuwur.onlinecongress;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class Login extends AppCompatActivity {
    private DefaultValues dv = new DefaultValues();
    //login file to request
    private String URL = dv.url + "login.php";
    private RequestQueue rq;
    //set context
    private Context ctx;
    //create request
    private StringRequest jsrqlogin;
    //create edittexts
    private EditText et_id, et_pass;
    //buttons
    private Button buttontoforget, buttontoregister, buttonlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);
        //context is this
        ctx = Login.this;
        //requestqueue set to this
        rq = Volley.newRequestQueue(ctx);
        //get edittexts on xml
        et_id = findViewById(R.id.correologin);
        et_pass = findViewById(R.id.passlogin);
        //get buttons
        buttontoforget = findViewById(R.id.buttontoforget);
        buttontoregister = findViewById(R.id.buttontoregister);
        buttonlogin = findViewById(R.id.buttonlogin);

        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLoginSesion();
            }
        });
        buttontoforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLoginOlvidar();
            }
        });
        buttontoregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLoginRegistrar();
            }
        });

        boolean loginsesion = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("loginsesion", false);
        if (loginsesion) {
            Intent intentiniciar = new Intent(Login.this, Home.class);
            startActivity(intentiniciar);
            finish();
        }
    }

    public void onClickLoginSesion() {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso1 = new ProgressDialog(ctx);
        progreso1.setMessage("Por favor, espere...");
        progreso1.show();

        jsrqlogin = new StringRequest(Request.Method.POST, URL,
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
                                progreso1.dismiss();
                                Boolean sesion = res.getBoolean("sesion");
                                Boolean error = res.getBoolean("error");

                                if (sesion) {
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putBoolean("loginsesion", true).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("id", res.getString("usrid")).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("tipodni", res.getString("usrtipodni")).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("dni", res.getString("usrdni")).apply();
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
                                            .putString("pass", res.getString("usrpass")).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("sexo", res.getString("usrsex")).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("institucion", res.getString("usrinst")).apply();

                                    Intent intentiniciar = new Intent(Login.this, Congresos.class);
                                    startActivity(intentiniciar);
                                    Toast.makeText(ctx, res.getString("mensaje") + "\nBienvenido, " + res.getString("usrname") + " " + res.getString("usrape") + ".", Toast.LENGTH_LONG).show();
                                    finish();
                                } else if (error) {
                                    AlertDialog.Builder dialogoerror = new AlertDialog.Builder(ctx);
                                    dialogoerror.setTitle("INICIAR SESIÓN");
                                    dialogoerror.setMessage("\n" + res.getString("mensaje"));
                                    dialogoerror.setCancelable(false);
                                    dialogoerror.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogoerror, int id) {
                                            //Ejecute acciones, deje vacio para solo aceptar
                                            dialogoerror.cancel();
                                            et_pass.setText(null);
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
                progreso1.dismiss();

                AlertDialog.Builder dialogoerror = new AlertDialog.Builder(ctx);
                dialogoerror.setTitle("ERROR");
                dialogoerror.setMessage("\nNo es posible contactar al servidor. Verifique su conexión a Internet e inténtelo más tarde.");
                dialogoerror.setCancelable(false);
                dialogoerror.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogoerror, int id) {
                        //Ejecute acciones, deje vacio para solo aceptar
                        dialogoerror.cancel();
                        et_pass.setText(null);
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

                parametros.put("email", et_id.getText().toString());
                parametros.put("pass", et_pass.getText().toString());

                return parametros;
            }
        };
        rq.add(jsrqlogin);
    }

    public void onClickLoginRegistrar() {
        Intent intentiniciar = new Intent(Login.this, Register.class);
        startActivity(intentiniciar);
        //finish();
    }

    public void onClickLoginOlvidar() {
        Intent intentiniciar = new Intent(Login.this, Forget.class);
        startActivity(intentiniciar);
        //finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setMessage("¿Desea salir de la aplicación?");
        dialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                finish();
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
}