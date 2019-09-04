package com.byuwur.onlinecongress;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Cuenta extends AppCompatActivity {
    //ACTUALIZAR FOTO DE PERFIL
    private static final String TAG = Cuenta.class.getSimpleName();
    private final static int FCR = 1;
    DefaultValues dv = new DefaultValues();
    //login file to request
    private String URLnombreciudad = dv.url + "nombreciudad.php",
            URLactciudad = dv.urlcuenta + "ciudad.php", URLactnombre = dv.urlcuenta + "nombre.php",
            URLactinst = dv.urlcuenta + "inst.php", URLactsexo = dv.urlcuenta + "sexo.php", URLactdni = dv.urlcuenta + "dni.php",
            URLactcorreo = dv.urlcuenta + "correo.php", URLactphone = dv.urlcuenta + "phone.php",
            URLactpass = dv.urlcuenta + "pass.php", URLactfoto = dv.urlcuenta + "fotoperfil/fotos.php",
            URLpais= dv.url + "paises.php", URLdep = dv.url + "provincias.php", URLciu = dv.url + "ciudades.php";
    //
    private RequestQueue rq;
    //set context
    private Context ctx;
    //
    private JsonArrayRequest jsrqnombreciudad, jsrqpais, jsrqdep, jsrqciu;
    private StringRequest jsrqactualizar;
    //list of each array
    private ArrayList<String> tipodni = new ArrayList<>(),sexo = new ArrayList<>();
    private ArrayList<String> pais = new ArrayList<>(), idpais = new ArrayList<>();
    private ArrayList<String> dep = new ArrayList<>(), iddep = new ArrayList<>();
    private ArrayList<String> ciudad = new ArrayList<>(), idciudad = new ArrayList<>();
    private String buscaridpais = "", buscariddepar = "", buscaridciudad = "";
    private TextView textnombre, textdni, texttipodni, textcorreo, textcongreso, textinstitucion, textphone, textsexo, textciudad;
    private Button editarnombre, editardni, editarcorreo, editarcongreso, editarinstitucion, editarphone, editarpass, editarsexo, editarciudad, editarfoto;
    private String usrnombre, usrtipodni, usrdni, usrcorreo, usrphone, usrciudad, usrsexo, usrinst, usrid;
    private String mCM;
    private ValueCallback mUM;
    private ValueCallback<Uri[]> mUMA;
    private WebView webviewfoto;
    //select whether you want to upload multiple files
    private boolean multiple_files = false;
    private boolean shouldRefreshOnResume = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            //Check if response is positive
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return;
                    }
                    if (intent == null || intent.getData() == null) {
                        //Capture Photo if no image available
                        if (mCM != null) {
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        } else {
                            if (multiple_files) {
                                if (intent.getClipData() != null) {
                                    final int numSelectedFiles = intent.getClipData().getItemCount();
                                    results = new Uri[numSelectedFiles];
                                    for (int i = 0; i < numSelectedFiles; i++) {
                                        results[i] = intent.getClipData().getItemAt(i).getUri();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        } else {
            if (requestCode == FCR) {
                if (null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Modificar Cuenta");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //
        ctx = Cuenta.this;
        rq = Volley.newRequestQueue(ctx);

        usrid = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("id", null);

        textnombre = findViewById(R.id.textnombre);
        textdni = findViewById(R.id.textdni);
        texttipodni = findViewById(R.id.texttipodni);
        textcorreo = findViewById(R.id.textcorreo);
        textcongreso = findViewById(R.id.textcongreso);
        textinstitucion = findViewById(R.id.textinstitucion);
        textsexo = findViewById(R.id.textsexo);
        textphone = findViewById(R.id.textphone);
        textciudad = findViewById(R.id.textciudad);

        settexts();

        editarnombre = findViewById(R.id.editarnombre);
        editardni = findViewById(R.id.editardni);
        editarcorreo = findViewById(R.id.editarcorreo);
        editarcongreso = findViewById(R.id.editarcongreso);
        editarinstitucion = findViewById(R.id.editarinstitucion);
        editarphone = findViewById(R.id.editarphone);
        editarpass = findViewById(R.id.editarpass);
        editarsexo = findViewById(R.id.editarsexo);
        editarciudad = findViewById(R.id.editarciudad);
        editarfoto = findViewById(R.id.editarfoto);

        editarnombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vieweditarnombre();
            }
        });
        editardni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vieweditardni();
            }
        });
        editarcorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vieweditarcorreo();
            }
        });
        editarcongreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vieweditarcongreso();
            }
        });
        editarinstitucion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vieweditarinst();
            }
        });
        editarphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vieweditarphone();
            }
        });
        editarpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vieweditarpass();
            }
        });
        editarsexo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vieweditarsexo();
            }
        });
        editarciudad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vieweditarciudad();
            }
        });
        editarfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vieweditarfoto();
            }
        });
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    private void vieweditarcongreso() {
        Intent intentiniciar = new Intent(Cuenta.this, Congresos.class);
        startActivity(intentiniciar);
    }

    private void vieweditarnombre() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        @SuppressLint("InflateParams") View layout = LayoutInflater.from(ctx).inflate(R.layout.dialog_cambiarname, null);
        dialog.setView(layout);

        final EditText nombrenuevo = layout.findViewById(R.id.nombrenuevo);
        final EditText apellidonuevo = layout.findViewById(R.id.apellidonuevo);

        dialog.setCancelable(false);
        dialog.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                actualizarnombre(usrid, nombrenuevo.getText().toString(), apellidonuevo.getText().toString());
            }
        });
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        dialog.create();
        dialog.show();
    }

    private void actualizarnombre(final String id, final String nombre, final String apellido) {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();

        jsrqactualizar = new StringRequest(Request.Method.POST, URLactnombre,
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
                                            vieweditarnombre();
                                        }
                                    });
                                    dialogo.show();
                                }
                                if (success) {
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("nombre", nombre).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("apellido", apellido).apply();

                                    AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                    dialogo.setTitle("ACTUALIZAR");
                                    dialogo.setMessage("\n" + res.getString("mensaje"));
                                    dialogo.setCancelable(false);
                                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo, int id) {
                                            //Ejecute acciones, deje vacio para solo aceptar
                                            dialogo.cancel();
                                            //onclickself();
                                            settexts();
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
                //Toast.makeText(ctx, "Unable to fetch data: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("id", id);
                parametros.put("nombre", nombre);
                parametros.put("apellido", apellido);
                return parametros;
            }
        };
        rq.add(jsrqactualizar);
    }

    private void vieweditardni() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        @SuppressLint("InflateParams") View layout = LayoutInflater.from(ctx).inflate(R.layout.dialog_cambiardni, null);
        dialog.setView(layout);

        final Spinner spinnertipodni = layout.findViewById(R.id.spinnerdninuevo);
        final String[] stringtipodninuevo = new String[1];
        final EditText dninuevo = layout.findViewById(R.id.dninuevo);

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
                stringtipodninuevo[0] = tipodni.get(pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dialog.setCancelable(false);
        dialog.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                actualizardni(usrid, dninuevo.getText().toString(), stringtipodninuevo[0]);
            }
        });
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        dialog.create();
        dialog.show();
    }

    private void actualizardni(final String id, final String dni, final String tipodni) {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();

        jsrqactualizar = new StringRequest(Request.Method.POST, URLactdni,
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
                                            vieweditarnombre();
                                        }
                                    });
                                    dialogo.show();
                                }
                                if (success) {
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("tipodni", tipodni).apply();
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("dni", dni).apply();

                                    AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                    dialogo.setTitle("ACTUALIZAR");
                                    dialogo.setMessage("\n" + res.getString("mensaje"));
                                    dialogo.setCancelable(false);
                                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo, int id) {
                                            //Ejecute acciones, deje vacio para solo aceptar
                                            dialogo.cancel();
                                            //onclickself();
                                            settexts();
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
                //Toast.makeText(ctx, "Unable to fetch data: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("id", id);
                parametros.put("tipodni", tipodni);
                parametros.put("dni", dni);
                return parametros;
            }
        };
        rq.add(jsrqactualizar);
    }

    private void vieweditarcorreo() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        @SuppressLint("InflateParams") View layout = LayoutInflater.from(ctx).inflate(R.layout.dialog_cambiaremail, null);
        dialog.setView(layout);

        final EditText correonuevo = layout.findViewById(R.id.correonuevo);

        dialog.setCancelable(false);
        dialog.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                actualizarcorreo(usrid, correonuevo.getText().toString());
            }
        });
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        dialog.create();
        dialog.show();
    }

    private void actualizarcorreo(final String id, final String correo) {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();

        jsrqactualizar = new StringRequest(Request.Method.POST, URLactcorreo,
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
                                            vieweditarcorreo();
                                        }
                                    });
                                    dialogo.show();
                                }
                                if (success) {
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("email", correo).apply();

                                    AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                    dialogo.setTitle("ACTUALIZAR");
                                    dialogo.setMessage("\n" + res.getString("mensaje"));
                                    dialogo.setCancelable(false);
                                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo, int id) {
                                            //Ejecute acciones, deje vacio para solo aceptar
                                            dialogo.cancel();
                                            //onclickself();
                                            settexts();
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

                parametros.put("id", id);
                parametros.put("correo", correo);

                return parametros;
            }
        };
        rq.add(jsrqactualizar);
    }

    private void vieweditarsexo() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        @SuppressLint("InflateParams") View layout = LayoutInflater.from(ctx).inflate(R.layout.dialog_cambiarsexo, null);
        dialog.setView(layout);

        final Spinner spinnersexo  = layout.findViewById(R.id.spinnersexonuevo);
        final String[] stringsexo = new String[1];

        sexo.add("Masculino");
        sexo.add("Femenino");
        ArrayAdapter<String> adaptersexo = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, sexo);
        adaptersexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnersexo.setAdapter(adaptersexo);
        spinnersexo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                stringsexo[0] = sexo.get(pos);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dialog.setCancelable(false);
        dialog.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                actualizarsexo(usrid, stringsexo[0]);
            }
        });
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        dialog.create();
        dialog.show();
    }

    private void actualizarsexo(final String id, final String sexo) {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();

        jsrqactualizar = new StringRequest(Request.Method.POST, URLactsexo,
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
                                            vieweditarcorreo();
                                        }
                                    });
                                    dialogo.show();
                                }
                                if (success) {
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("sexo", sexo).apply();

                                    AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                    dialogo.setTitle("ACTUALIZAR");
                                    dialogo.setMessage("\n" + res.getString("mensaje"));
                                    dialogo.setCancelable(false);
                                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo, int id) {
                                            //Ejecute acciones, deje vacio para solo aceptar
                                            dialogo.cancel();
                                            //onclickself();
                                            settexts();
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

                parametros.put("id", id);
                parametros.put("sexo", sexo);

                return parametros;
            }
        };
        rq.add(jsrqactualizar);
    }

    private void vieweditarinst() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        @SuppressLint("InflateParams") View layout = LayoutInflater.from(ctx).inflate(R.layout.dialog_cambiarinst, null);
        dialog.setView(layout);

        final EditText instnueva = layout.findViewById(R.id.instnuevo);

        dialog.setCancelable(false);
        dialog.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                actualizarinst(usrid, instnueva.getText().toString());
            }
        });
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        dialog.create();
        dialog.show();
    }

    private void actualizarinst(final String id, final String inst) {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();

        jsrqactualizar = new StringRequest(Request.Method.POST, URLactinst,
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
                                            vieweditarcorreo();
                                        }
                                    });
                                    dialogo.show();
                                }
                                if (success) {
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("institucion", inst).apply();

                                    AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                    dialogo.setTitle("ACTUALIZAR");
                                    dialogo.setMessage("\n" + res.getString("mensaje"));
                                    dialogo.setCancelable(false);
                                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo, int id) {
                                            //Ejecute acciones, deje vacio para solo aceptar
                                            dialogo.cancel();
                                            //onclickself();
                                            settexts();
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

                parametros.put("id", id);
                parametros.put("inst", inst);

                return parametros;
            }
        };
        rq.add(jsrqactualizar);
    }

    private void vieweditarphone() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        @SuppressLint("InflateParams") View layout = LayoutInflater.from(ctx).inflate(R.layout.dialog_cambiarphone, null);
        dialog.setView(layout);

        final EditText telefononuevo = layout.findViewById(R.id.telefononuevo);

        dialog.setCancelable(false);
        dialog.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                actualizarphone(usrid, telefononuevo.getText().toString());
            }
        });
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        dialog.create();
        dialog.show();
    }

    private void actualizarphone(final String id, final String phone) {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();

        jsrqactualizar = new StringRequest(Request.Method.POST, URLactphone,
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
                                            vieweditarphone();
                                        }
                                    });
                                    dialogo.show();
                                }
                                if (success) {
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("phone", phone).apply();

                                    AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                    dialogo.setTitle("ACTUALIZAR");
                                    dialogo.setMessage("\n" + res.getString("mensaje"));
                                    dialogo.setCancelable(false);
                                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo, int id) {
                                            //Ejecute acciones, deje vacio para solo aceptar
                                            dialogo.cancel();
                                            //onclickself();
                                            settexts();
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

                parametros.put("id", id);
                parametros.put("phone", phone);

                return parametros;
            }
        };
        rq.add(jsrqactualizar);
    }

    private void vieweditarpass() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        @SuppressLint("InflateParams") View layout = LayoutInflater.from(ctx).inflate(R.layout.dialog_cambiarpass, null);
        dialog.setView(layout);

        final EditText passactual = layout.findViewById(R.id.passactual);
        final EditText passnueva = layout.findViewById(R.id.passnueva);
        final EditText verifpassnueva = layout.findViewById(R.id.verifpassnueva);

        dialog.setCancelable(false);
        dialog.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                if (passnueva.getText().toString().equals(verifpassnueva.getText().toString()))
                    actualizarpass(usrid, passactual.getText().toString(), passnueva.getText().toString());
                else {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Cuenta.this);
                    dialogo1.setTitle("REGISTRAR");
                    dialogo1.setMessage("\nPor favor, verifique sus contraseñas.");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            //Ejecute acciones, deje vacio para solo aceptar
                            dialogo1.cancel();
                            vieweditarpass();
                        }
                    });
                    dialogo1.show();
                }

            }
        });
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        dialog.create();
        dialog.show();
    }

    private void actualizarpass(final String id, final String passactual, final String passnueva) {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();

        jsrqactualizar = new StringRequest(Request.Method.POST, URLactpass,
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
                                            vieweditarpass();
                                        }
                                    });
                                    dialogo.show();
                                }
                                if (success) {
                                    AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                    dialogo.setTitle("ACTUALIZAR");
                                    dialogo.setMessage("\n" + res.getString("mensaje"));
                                    dialogo.setCancelable(false);
                                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo, int id) {
                                            //Ejecute acciones, deje vacio para solo aceptar
                                            dialogo.cancel();
                                            //LOGOUT ACTION
                                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                                    .putBoolean("loginsesion", false).apply();
                                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                                    .putString("id", null).apply();
                                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                                    .putString("nombre", null).apply();
                                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                                    .putString("email", null).apply();
                                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                                    .putString("ciudad", null).apply();
                                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                                    .putString("phone", null).apply();
                                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                                    .putString("pass", null).apply();

                                            Intent intent = new Intent(Cuenta.this, Login.class);
                                            startActivity(intent);
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

                parametros.put("id", id);
                parametros.put("passactual", passactual);
                parametros.put("passnueva", passnueva);

                return parametros;
            }
        };
        rq.add(jsrqactualizar);
    }

    private void vieweditarciudad() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        @SuppressLint("InflateParams") View layout = LayoutInflater.from(ctx).inflate(R.layout.dialog_cambiarciudad, null);
        dialog.setView(layout);

        //initial values
        pais.add("[--- Países (+cód. país) ---]");
        idpais.add("0");
        dep.add("[--- Departamentos ---]");
        iddep.add("0");
        ciudad.add("[--- Ciudades ---]");
        idciudad.add("0");
        //fill dep
        llenarpaises();
        //SPINNER STUFF
        final Spinner spinnerpais = layout.findViewById(R.id.spinnerpais);
        final Spinner spinnerdep = layout.findViewById(R.id.spinnerprovincia);
        final Spinner spinnerciu = layout.findViewById(R.id.spinnerciudad);
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

        dialog.setCancelable(false);
        dialog.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                actualizarciudad(usrid, buscaridciudad);
                dialog.cancel();
            }
        });
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        dialog.create();
        dialog.show();
    }

    private void actualizarciudad(final String id, final String ciudad) {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();
        jsrqactualizar = new StringRequest(Request.Method.POST, URLactciudad,
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
                                            vieweditarciudad();
                                        }
                                    });
                                    dialogo.show();
                                }
                                if (success) {
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                            .putString("ciudad", ciudad).apply();

                                    AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                    dialogo.setTitle("ACTUALIZAR");
                                    dialogo.setMessage("\n" + res.getString("mensaje"));
                                    dialogo.setCancelable(false);
                                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo, int id) {
                                            //Ejecute acciones, deje vacio para solo aceptar
                                            dialogo.cancel();
                                            //onclickself();
                                            settexts();
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

                parametros.put("id", id);
                parametros.put("ciudad", ciudad);

                return parametros;
            }
        };
        rq.add(jsrqactualizar);
    }

    //PROFILE PHOTO WEBVIEW START
    @SuppressLint("SetJavaScriptEnabled")
    private void vieweditarfoto() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        @SuppressLint("InflateParams") View layout = LayoutInflater.from(ctx).inflate(R.layout.dialog_cambiarfoto, null);

        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(Cuenta.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        }

        webviewfoto = layout.findViewById(R.id.webviewfoto);
        WebSettings webSettings = webviewfoto.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);

        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(0);
            webviewfoto.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            webviewfoto.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webviewfoto.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        String postdata = "id=" + usrid;
        webviewfoto.setWebViewClient(new Callback());
        webviewfoto.postUrl(URLactfoto, postdata.getBytes());
        webviewfoto.setWebChromeClient(new WebChromeClient() {
            //For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                Cuenta.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), FCR);
            }

            // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                Cuenta.this.startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FCR);
            }

            //For Android 4.1+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                Cuenta.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), Cuenta.FCR);
            }

            //For Android 5.0+
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {
                if (mUMA != null) {
                    mUMA.onReceiveValue(null);
                }
                mUMA = filePathCallback;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(Cuenta.this.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCM);
                    } catch (IOException ex) {
                        Log.e(TAG, "Image file creation failed", ex);
                    }
                    if (photoFile != null) {
                        mCM = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");
                if (multiple_files) {
                    contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                if (multiple_files && Build.VERSION.SDK_INT >= 18) {
                    chooserIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                startActivityForResult(chooserIntent, FCR);
                return true;
            }
        });

        dialog.setView(layout);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                dialog.cancel();
            }
        });
        dialog.create();
        dialog.show();
    }

    //WEBVIEW CREATE IMAGE
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
    //PROFILE PHOTO WEBVIEW END

    private void settexts() {
        usrnombre = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("nombre", null) + " " + getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("apellido", null);
        usrtipodni = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("tipodni", null);
        usrdni = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("dni", null);
        usrcorreo = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("email", null);
        usrphone = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("phone", null);
        usrsexo = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("sexo", null);
        usrinst = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("institucion", null);

        textnombre.setText(usrnombre);
        texttipodni.setText(usrtipodni);
        textdni.setText(usrdni);
        textcorreo.setText(usrcorreo);
        textphone.setText(usrphone);
        textsexo.setText(usrsexo);
        textinstitucion.setText(usrinst);

        setnombreciudad();
    }

    private void setnombreciudad() {
        usrciudad = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("ciudad", null);
        jsrqnombreciudad = new JsonArrayRequest(Request.Method.GET, URLnombreciudad + "?ciudad=" + usrciudad,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject res = response.getJSONObject(i);
                                if (res.has("name_ciu")) {
                                    textciudad.setText(res.getString("name_ciu"));
                                } else {
                                    textciudad.setText(usrciudad);
                                }
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
        rq.add(jsrqnombreciudad);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check should we need to refresh the fragment
        if (shouldRefreshOnResume) {
            settexts();
            shouldRefreshOnResume = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    //WEBVIEW CALLBACK
    public class Callback extends WebViewClient {
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }
    }
}
