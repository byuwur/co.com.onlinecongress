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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    DefaultValues dv = new DefaultValues();
    //register file to request
    private String URL = dv.urlinicio + "registrar.php";
    private String URLdep = dv.urllistar + "departamentos.php";
    private String URLciu = dv.urllistar + "ciudades.php";
    private RequestQueue rq;
    //set context
    private Context ctx;
    //create request
    private JsonArrayRequest jsrqdep, jsrqciu;
    private StringRequest jsrqregistrar;
    //register fields declarations
    private EditText et_nombre, et_correo, et_pass, et_phone;
    private CheckBox terms;
    //list of each array
    private ArrayList<String> dep = new ArrayList<>(), iddep = new ArrayList<>();
    private ArrayList<String> ciudad = new ArrayList<>(), idciudad = new ArrayList<>();
    private String buscariddepar = "", buscaridciudad = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_register);

        ctx = Register.this;
        rq = Volley.newRequestQueue(ctx);

        et_nombre = findViewById(R.id.nombreregistrar);
        et_correo = findViewById(R.id.correoregistrar);
        et_pass = findViewById(R.id.passregistrar);
        et_phone = findViewById(R.id.telefonoregistrar);
        terms = findViewById(R.id.checkboxregistrar);

        //initial values
        dep.add("[--- Departamentos ---]");
        iddep.add("0");
        ciudad.add("[--- Ciudades ---]");
        idciudad.add("0");
        //fill dep
        llenardepartamentos();
        //SPINNER STUFF
        final Spinner spinnerdep = findViewById(R.id.spinnerdepartamento);
        final Spinner spinnerciu = findViewById(R.id.spinnerciudad);
        //set the spinner value from Arraylist
        ArrayAdapter<String> adapterdep = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dep);
        adapterdep.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerdep.setAdapter(adapterdep);
        spinnerdep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                spinnerciu.setEnabled(true);
                spinnerciu.setClickable(true);
                //Toast.makeText(ctx,adapterView.getItemAtPosition(pos)+". "+iddep.get(pos), Toast.LENGTH_SHORT).show();
                buscariddepar = iddep.get(pos);
                //RESET CIUDAD ARRAYLIST
                ciudad.clear();
                idciudad.clear();
                ciudad.add("[--- Ciudades ---]");
                idciudad.add("0");
                spinnerciu.setSelection(0);
                //fill ciudad arraylist
                llenarciudad();
                if (pos == 0) {
                    spinnerciu.setEnabled(false);
                    spinnerciu.setClickable(false);
                    buscaridciudad = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //set the spinner value from Arraylist
        ArrayAdapter<String> adapterciu = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ciudad);
        adapterciu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerciu.setAdapter(adapterciu);
        spinnerciu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                //Toast.makeText(ctx, adapterView.getItemAtPosition(pos)+". "+idciudad.get(pos), Toast.LENGTH_SHORT).show();
                buscaridciudad = idciudad.get(pos);
                if (pos == 0) {
                    buscaridciudad = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void llenardepartamentos() {
        jsrqdep = new JsonArrayRequest(Request.Method.GET, URLdep,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject res = response.getJSONObject(i);
                                //Log.d("Response: ", "ID:"+res.getString("IDDEPARTAMENTOS")+". Nombre: "+res.getString("NOMBREDEPARTAMENTO"));
                                dep.add(res.getString("NOMBREDEPARTAMENTO"));
                                iddep.add(res.getString("IDDEPARTAMENTOS"));
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
        rq.add(jsrqdep);
    }

    private void llenarciudad() {
        jsrqciu = new JsonArrayRequest(Request.Method.GET, URLciu + "?dep=" + buscariddepar,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject res = response.getJSONObject(i);
                                //Log.d("Response: ", "ID:"+res.getString("IDCIUDADES")+". Nombre: "+res.getString("NOMBRECIUDAD"));
                                ciudad.add(res.getString("NOMBRECIUDAD"));
                                idciudad.add(res.getString("IDCIUDADES"));
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
        rq.add(jsrqciu);
    }

    public void onClickRegistrar(View view) {
        //verify if terms are checked
        if (terms.isChecked()) {
            // Showing progress dialog at user registration time.
            final ProgressDialog progreso1 = new ProgressDialog(Register.this);
            progreso1.setMessage("Por favor, espere...");
            progreso1.show();

            jsrqregistrar = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
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

                                    progreso1.dismiss();

                                    if (success) {
                                        AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                        dialogo.setTitle("REGISTRAR");
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
                                    } else if (error) {
                                        AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                        dialogo.setTitle("REGISTRAR");
                                        dialogo.setMessage("\n" + res.getString("mensaje"));
                                        dialogo.setCancelable(false);
                                        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogo, int id) {
                                                //Ejecute acciones, deje vacio para solo aceptar
                                                dialogo.cancel();
                                                et_pass.setText(null);
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

                    parametros.put("name", et_nombre.getText().toString());
                    parametros.put("email", et_correo.getText().toString());
                    parametros.put("pass", et_pass.getText().toString());
                    parametros.put("phone", et_phone.getText().toString());
                    parametros.put("ciudad", buscaridciudad);

                    return parametros;
                }
            };
            rq.add(jsrqregistrar);
        } else {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Register.this);
            dialogo1.setTitle("REGISTRAR");
            dialogo1.setMessage("\nPor favor, acepte términos y condiciones si desea realizar su registro.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    //Ejecute acciones, deje vacio para solo aceptar
                    dialogo1.cancel();
                    et_pass.setText(null);
                }
            });
            dialogo1.show();
        }
    }

    public void onClickRegistrarLogin(View view) {
        //Intent intentiniciar = new Intent(Register.this, Login.class);
        //startActivity(intentiniciar);
        finish();
    }

    @Override
    public void onBackPressed() {
        //Intent intentiniciar = new Intent(Register.this, Login.class);
        //startActivity(intentiniciar);
        finish();
    }
}