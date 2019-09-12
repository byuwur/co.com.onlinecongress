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
import android.widget.Button;
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
    private String URL = dv.url + "registrar.php";
    private String URLpais = dv.url + "paises.php";
    private String URLdep = dv.url + "provincias.php";
    private String URLciu = dv.url + "ciudades.php";
    private RequestQueue rq;
    //set context
    private Context ctx;
    //buttons
    private Button buttonregistrar, button4;
    //create request
    private JsonArrayRequest jsrqpais, jsrqdep, jsrqciu;
    private StringRequest jsrqregistrar;
    //register fields declarations
    private EditText et_dni, et_nombre, et_apellido, et_correo, et_pass, et_confpass, et_phone, et_inst;
    private CheckBox terms;
    //list of each array
    private ArrayList<String> tipodni = new ArrayList<>(), sexo = new ArrayList<>();
    private ArrayList<String> pais = new ArrayList<>(), idpais = new ArrayList<>();
    private ArrayList<String> dep = new ArrayList<>(), iddep = new ArrayList<>();
    private ArrayList<String> ciudad = new ArrayList<>(), idciudad = new ArrayList<>();
    private String buscaridpais = "", buscariddepar = "", buscaridciudad = "", stringtipodni = "", stringsexo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_register);

        ctx = Register.this;
        rq = Volley.newRequestQueue(ctx);

        buttonregistrar = findViewById(R.id.botonregistrar);
        buttonregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRegistrar();
            }
        });
        button4 = findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRegistrarLogin();
            }
        });

        et_dni = findViewById(R.id.dniregistrar);
        et_nombre = findViewById(R.id.nombreregistrar);
        et_apellido = findViewById(R.id.apellidoregistrar);
        et_correo = findViewById(R.id.correoregistrar);
        et_pass = findViewById(R.id.passregistrar);
        et_confpass = findViewById(R.id.verifpassregistrar);
        et_phone = findViewById(R.id.telefonoregistrar);
        et_inst = findViewById(R.id.institucionregistrar);
        terms = findViewById(R.id.checkboxregistrar);

        //initial values
        pais.add("[--- Países (+cód. país) ---]");
        idpais.add("0");
        dep.add("[--- Provincias ---]");
        iddep.add("0");
        ciudad.add("[--- Ciudades ---]");
        idciudad.add("0");
        //fill dep
        llenarpaises();
        //SPINNER STUFF
        final Spinner spinnertipodni = findViewById(R.id.tipodni);
        final Spinner spinnersexo = findViewById(R.id.sexoregistrar);
        final Spinner spinnerpais = findViewById(R.id.spinnerpais);
        final Spinner spinnerdep = findViewById(R.id.spinnerprovincia);
        final Spinner spinnerciu = findViewById(R.id.spinnerciudad);

        tipodni.add("Cédula de ciudadanía");
        tipodni.add("Cédula de extranjería");
        tipodni.add("Tarjeta de identidad");
        tipodni.add("Pasaporte");
        ArrayAdapter<String> adaptertipodni = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, tipodni);
        adaptertipodni.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnertipodni.setAdapter(adaptertipodni);
        spinnertipodni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                stringtipodni = tipodni.get(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        sexo.add("Masculino");
        sexo.add("Femenino");
        sexo.add("Otro");
        ArrayAdapter<String> adaptersexo = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, sexo);
        adaptersexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnersexo.setAdapter(adaptersexo);
        spinnersexo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                stringsexo = sexo.get(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //set the spinner value from Arraylist
        ArrayAdapter<String> adapterpais = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, pais);
        adapterpais.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerpais.setAdapter(adapterpais);
        spinnerpais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                spinnerdep.setEnabled(true);
                spinnerdep.setClickable(true);
                //Toast.makeText(ctx,adapterView.getItemAtPosition(pos)+". "+iddep.get(pos), Toast.LENGTH_SHORT).show();
                buscaridpais = idpais.get(pos);
                //RESET CIUDAD ARRAYLIST
                dep.clear();
                iddep.clear();
                dep.add("[--- Provincias ---]");
                iddep.add("0");
                spinnerdep.setSelection(0);
                //fill ciudad arraylist
                llenardepartamentos();
                if (pos == 0) {
                    spinnerdep.setEnabled(false);
                    spinnerdep.setClickable(false);
                    buscariddepar = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //set the spinner value from Arraylist
        ArrayAdapter<String> adapterdep = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, dep);
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
        ArrayAdapter<String> adapterciu = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, ciudad);
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

    private void llenarpaises() {
        jsrqpais = new JsonArrayRequest(Request.Method.GET, URLpais,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject res = response.getJSONObject(i);
                                //Log.d("Response: ", "ID:"+res.getString("IDDEPARTAMENTOS")+". Nombre: "+res.getString("NOMBREDEPARTAMENTO"));
                                pais.add(res.getString("name_pais") + " (+" + res.getString("phonecode") + ")");
                                idpais.add(res.getString("id"));
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

    private void llenardepartamentos() {
        jsrqdep = new JsonArrayRequest(Request.Method.GET, URLdep + "?pais=" + buscaridpais,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject res = response.getJSONObject(i);
                                //Log.d("Response: ", "ID:"+res.getString("IDDEPARTAMENTOS")+". Nombre: "+res.getString("NOMBREDEPARTAMENTO"));
                                dep.add(res.getString("name_pro"));
                                iddep.add(res.getString("id"));
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
        jsrqciu = new JsonArrayRequest(Request.Method.GET, URLciu + "?provincia=" + buscariddepar,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject res = response.getJSONObject(i);
                                //Log.d("Response: ", "ID:"+res.getString("IDCIUDADES")+". Nombre: "+res.getString("NOMBRECIUDAD"));
                                ciudad.add(res.getString("name_ciu"));
                                idciudad.add(res.getString("id"));
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

    public void onClickRegistrar() {
        //verify if terms are checked
        if (terms.isChecked() && (et_pass.getText().toString().equals(et_confpass.getText().toString()))) {
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
                                                et_confpass.setText(null);
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
                            et_confpass.setText(null);
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

                    parametros.put("tipodni", stringtipodni);
                    parametros.put("dni", et_dni.getText().toString());
                    parametros.put("name", et_nombre.getText().toString());
                    parametros.put("last", et_apellido.getText().toString());
                    parametros.put("sexo", stringsexo);
                    parametros.put("email", et_correo.getText().toString());
                    parametros.put("pass", et_pass.getText().toString());
                    parametros.put("phone", et_phone.getText().toString());
                    parametros.put("ciudad", buscaridciudad);
                    parametros.put("inst", et_inst.getText().toString());

                    return parametros;
                }
            };
            rq.add(jsrqregistrar);
        } else if (!terms.isChecked()) {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Register.this);
            dialogo1.setTitle("REGISTRAR");
            dialogo1.setMessage("\nPor favor, acepte términos y condiciones si desea realizar su registro.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    //Ejecute acciones, deje vacio para solo aceptar
                    dialogo1.cancel();
                    et_pass.setText(null);
                    et_confpass.setText(null);
                }
            });
            dialogo1.show();
        } else if (!et_pass.getText().toString().equals(et_confpass.getText().toString())) {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Register.this);
            dialogo1.setTitle("REGISTRAR");
            dialogo1.setMessage("\nPor favor, verifique sus contraseñas.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    //Ejecute acciones, deje vacio para solo aceptar
                    dialogo1.cancel();
                    et_pass.setText(null);
                    et_confpass.setText(null);
                }
            });
            dialogo1.show();
        }
    }

    public void onClickRegistrarLogin() {
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