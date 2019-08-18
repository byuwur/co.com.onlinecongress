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
    private String URLnombreciudad = dv.urlcuenta + "nombreciudad.php",
            URLactciudad = dv.urlcuenta + "ciudad.php", URLactnombre = dv.urlcuenta + "nombre.php",
            URLactcorreo = dv.urlcuenta + "correo.php", URLactphone = dv.urlcuenta + "phone.php",
            URLactpass = dv.urlcuenta + "pass.php", URLactfoto = dv.urlcuenta + "fotoperfil/fotos.php",
            URLdep = dv.urllistar + "departamentos.php", URLciu = dv.urllistar + "ciudades.php";
    //
    private RequestQueue rq;
    //set context
    private Context ctx;
    //
    private JsonArrayRequest jsrqnombreciudad, jsrqdep, jsrqciu;
    private StringRequest jsrqactualizar;
    //list of each array
    private ArrayList<String> dep = new ArrayList<>(), iddep = new ArrayList<>();
    private ArrayList<String> ciudad = new ArrayList<>(), idciudad = new ArrayList<>();
    private String buscariddepar = "", buscaridciudad = "";
    private TextView textnombre, textcorreo, textphone, textciudad;
    private Button editarnombre, editarcorreo, editarphone, editarpass, editarciudad, editarfoto;
    private String usrnombre, usrcorreo, usrphone, usrciudad, usrid;
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
        textcorreo = findViewById(R.id.textcorreo);
        textphone = findViewById(R.id.textphone);
        textciudad = findViewById(R.id.textciudad);

        settexts();

        editarnombre = findViewById(R.id.editarnombre);
        editarcorreo = findViewById(R.id.editarcorreo);
        editarphone = findViewById(R.id.editarphone);
        editarpass = findViewById(R.id.editarpass);
        editarciudad = findViewById(R.id.editarciudad);
        editarfoto = findViewById(R.id.editarfoto);

        editarnombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vieweditarnombre();
            }
        });

        editarcorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vieweditarcorreo();
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

    private void vieweditarnombre() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        @SuppressLint("InflateParams") View layout = LayoutInflater.from(ctx).inflate(R.layout.dialog_cambiarname, null);
        dialog.setView(layout);

        final EditText nombrenuevo = layout.findViewById(R.id.nombrenuevo);

        dialog.setCancelable(false);
        dialog.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                actualizarnombre(usrid, nombrenuevo.getText().toString());
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

    private void actualizarnombre(final String id, final String nombre) {
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

        dialog.setCancelable(false);
        dialog.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                actualizarpass(usrid, passactual.getText().toString(), passnueva.getText().toString());
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

                                            Intent intent = new Intent(Cuenta.this, Home.class);
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
        dep.add("[--- Departamentos ---]");
        iddep.add("0");
        ciudad.add("[--- Ciudades ---]");
        idciudad.add("0");
        //fill dep
        llenardepartamentos();
        //SPINNER STUFF
        final Spinner spinnerdep = layout.findViewById(R.id.spinnerdepartamento);
        final Spinner spinnerciu = layout.findViewById(R.id.spinnerciudad);
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
                .getString("nombre", null);
        usrcorreo = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("email", null);
        usrphone = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("phone", null);

        textnombre.setText(usrnombre);
        textcorreo.setText(usrcorreo);
        textphone.setText(usrphone);

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
                                if (res.has("nombre")) {
                                    textciudad.setText(res.getString("nombre"));
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
