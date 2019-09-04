package com.byuwur.onlinecongress;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class Forget extends AppCompatActivity {
    private DefaultValues dv = new DefaultValues();
    //login file to request
    private String URL = dv.urlcuenta + "forgetpass.php";
    private RequestQueue rq;
    //set context
    private Context ctx;
    //create request
    private StringRequest jsrqforget;
    //create edittexts
    private EditText correoforget;
    //buttons
    private Button buttonforget, buttonforgettologin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_forget);

        ctx = Forget.this;
        rq = Volley.newRequestQueue(ctx);

        correoforget = findViewById(R.id.correoforget);
        buttonforget = findViewById(R.id.buttonforget);
        buttonforgettologin = findViewById(R.id.buttonforgettologin);

        buttonforgettologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickOlvidarLogin();
            }
        });
        buttonforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickOlvidar();
            }
        });
    }

    public void onClickOlvidar() {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso1 = new ProgressDialog(ctx);
        progreso1.setMessage("Por favor, espere...");
        progreso1.show();

        jsrqforget = new StringRequest(Request.Method.POST, URL,
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
                                Boolean error = res.getBoolean("error");

                                if (error) {
                                    AlertDialog.Builder dialogoerror = new AlertDialog.Builder(ctx);
                                    dialogoerror.setTitle("OLVIDAR CONTRASEÑA");
                                    dialogoerror.setMessage("\n" + res.getString("mensaje"));
                                    dialogoerror.setCancelable(false);
                                    dialogoerror.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogoerror, int id) {
                                            //Ejecute acciones, deje vacio para solo aceptar
                                            dialogoerror.cancel();
                                        }
                                    });
                                    dialogoerror.show();
                                } else {
                                    AlertDialog.Builder dialogoerror = new AlertDialog.Builder(ctx);
                                    dialogoerror.setTitle("OLVIDAR CONTRASEÑA");
                                    dialogoerror.setMessage("\n" + res.getString("mensaje"));
                                    dialogoerror.setCancelable(false);
                                    dialogoerror.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogoerror, int id) {
                                            //Ejecute acciones, deje vacio para solo aceptar
                                            dialogoerror.cancel();
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

                parametros.put("id", correoforget.getText().toString());

                return parametros;
            }
        };
        rq.add(jsrqforget);
    }

    public void onClickOlvidarLogin() {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}