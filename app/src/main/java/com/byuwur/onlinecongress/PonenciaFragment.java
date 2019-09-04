package com.byuwur.onlinecongress;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PonenciaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PonenciaFragment extends Fragment {
    //PUBLIC STATIC PARAMS OF SEARCH
    private static boolean ifsearch, ifid;
    private static String snombre, sbarrio, sciu, usrid, usrciudad;
    private DefaultValues dv = new DefaultValues();
    //register file to request
    private String IMGURL = dv.imgcanchasurl, URLid = dv.url + "buscarid.php", URLactciudad = dv.url + "anadirciudad.php",
            URLlistarstring = dv.url + "buscarnombre.php", URLlistarciudad = dv.url + "listarciudad.php",
            URLpais = dv.url + "paises.php", URLdep = dv.url + "departamentos.php", URLciu = dv.url + "ciudades.php";
    //set context
    private Context ctx;
    //
    private RequestQueue rq;
    //create request
    private JsonArrayRequest jsrqdep, jsrqciu;
    private StringRequest jsrqciudad, jsrqid, jsrqnombre, jsrqactciudad;
    //ARRAYS FOR VIEWNO
    //list of each array
    private ArrayList<String> dep = new ArrayList<>(), iddep = new ArrayList<>();
    private ArrayList<String> ciudad = new ArrayList<>(), idciudad = new ArrayList<>();
    private String buscariddepar = "", buscaridciudad = "";
    private OnFragmentInteractionListener mListener;

    private ArrayList<HolderPonencia> listaPonencia;
    private RecyclerView recyclerPonencia;
    private AdaptadorPonencia adapter;
    private boolean shouldRefreshOnResume = false;

    public PonenciaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewsi = inflater.inflate(R.layout.fragment_ponencia, container, false);
        View viewno = inflater.inflate(R.layout.fragment_noponencia, container, false);
        //
        ctx = getActivity();
        assert ctx != null;
        rq = Volley.newRequestQueue(ctx);
        // Showing progress dialog at user registration time.

        //GET VALUES FROM USER
        usrid = getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("id", null);
        usrciudad = getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("ciudad", null);

        //VIEWNO ELEMENTS
        //initial values
        dep.add("[--- Departamentos ---]");
        iddep.add("0");
        ciudad.add("[--- Ciudades ---]");
        idciudad.add("0");
        //fill dep
        llenardepartamentos();
        //SPINNER STUFF
        final Spinner spinnerdep = viewno.findViewById(R.id.spinnerdepartamento);
        final Spinner spinnerciu = viewno.findViewById(R.id.spinnerciudad);
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
        //search button script
        Button buttonactualizar = viewno.findViewById(R.id.buttonactualizarciudad);
        buttonactualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ctx,buscaridciudad,Toast.LENGTH_SHORT).show();
                actualizarciudad(usrid, buscaridciudad);
            }
        });

        //VIEWSI ELEMENTS
        listaPonencia = new ArrayList<>();
        recyclerPonencia = viewsi.findViewById(R.id.recyclerPonencia);
        recyclerPonencia.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdaptadorPonencia(listaPonencia);
        recyclerPonencia.setAdapter(adapter);
        //when it click an specific frame, let's see if we can display its id
        adapter.setonItemClickListener(new AdaptadorPonencia.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String id = ((TextView) recyclerPonencia.findViewHolderForAdapterPosition(position)
                        .itemView.findViewById(R.id.ponenciaid)).getText().toString();
                String idcancha = id.replaceAll("#", "");
                //Toast.makeText(ctx, "ID: "+idcancha, Toast.LENGTH_SHORT).show();
                Ponencia ponencia = new Ponencia();
                ponencia.setid(idcancha);

                Intent intent1 = new Intent(ctx, Ponencia.class);
                startActivity(intent1);
            }
        });
        //AND VERIFY IF THERE'S ANYTHING,
        //if it isn't display an specific layout design
        if ((usrciudad.equalsIgnoreCase("null") || usrciudad.equalsIgnoreCase("")
                || usrciudad == null) && !ifsearch) {
            return viewno;
        } else {
            LlenarListaPonencia(ifsearch, ifid, snombre, sbarrio, sciu);
            resetsearchvalues();
            return viewsi;
        }
    }

    private void actualizarciudad(final String id, final String ciudad) {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso1 = new ProgressDialog(ctx);
        progreso1.setMessage("Por favor, espere...");
        progreso1.show();

        jsrqactciudad = new StringRequest(Request.Method.POST, URLactciudad,
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
                                progreso1.dismiss();
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
                                } else if (success) {
                                    AlertDialog.Builder dialogo = new AlertDialog.Builder(ctx);
                                    dialogo.setTitle("ACTUALIZAR");
                                    dialogo.setMessage("\n" + res.getString("mensaje"));
                                    dialogo.setCancelable(false);
                                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo, int id) {
                                            //Ejecute acciones, deje vacio para solo aceptar
                                            getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                                    .putString("ciudad", ciudad).apply();
                                            dialogo.cancel();
                                            onclickself();
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
        rq.add(jsrqactciudad);
    }

    private void LlenarListaPonencia(boolean search, boolean id, String nombre, String barrio, String ciu) {
        Log.d("Data enviada", "" + search + ", " + id + ", " + nombre + ", " + barrio + ", " + ciu);
        //let's see the params to list
        if (search) {
            if (id) {
                buscarlistarid(nombre);
            } else {
                buscarlistarnombre(nombre, barrio, ciu);
            }
        } else {
            listarciudad();
        }
    }

    private void listarciudad() {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();

        usrciudad = getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("ciudad", null);
        // Showing progress dialog at user registration time.
        jsrqciudad = new StringRequest(Request.Method.POST, URLlistarciudad,
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
                                recyclerPonencia.setAdapter(adapter);

                                if (res.has("error")) {
                                    Boolean error = res.getBoolean("error");
                                    if (error) {
                                        AlertDialog.Builder dialogoerror = new AlertDialog.Builder(ctx);
                                        dialogoerror.setTitle("BUSCAR");
                                        dialogoerror.setMessage("\n" + res.getString("mensaje"));
                                        dialogoerror.setCancelable(false);
                                        dialogoerror.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogoerror, int id) {
                                                //Ejecute acciones, deje vacio para solo aceptar
                                                dialogoerror.cancel();
                                                onclickbuscar();
                                            }
                                        });
                                        dialogoerror.show();
                                    }
                                } else {
                                    listaPonencia.add(new HolderPonencia(
                                            "" + res.getString("NOMBRECANCHA"), res.getString("IDCANCHAS"),
                                            "$COP " + res.getString("TARIFA") + "/hora", "" + res.getString("UBICACION"),
                                            "" + res.getString("NOMBRECIUDAD"), "" + res.getString("DIASDISPONIBLE"),
                                            "De " + res.getString("HORAABRIR") + " a " + res.getString("HORACERRAR"),
                                            IMGURL + res.getString("IDCANCHAS") + "/1.jpg"));
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

                parametros.put("ciudad", usrciudad);

                return parametros;
            }
        };
        rq.add(jsrqciudad);
    }

    private void buscarlistarid(final String id) {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();

        // Showing progress dialog at user registration time.
        jsrqid = new StringRequest(Request.Method.POST, URLid,
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
                                recyclerPonencia.setAdapter(adapter);

                                if (res.has("error")) {
                                    Boolean error = res.getBoolean("error");
                                    if (error) {
                                        AlertDialog.Builder dialogoerror = new AlertDialog.Builder(ctx);
                                        dialogoerror.setTitle("BUSCAR");
                                        dialogoerror.setMessage("\n" + res.getString("mensaje"));
                                        dialogoerror.setCancelable(false);
                                        dialogoerror.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogoerror, int id) {
                                                //Ejecute acciones, deje vacio para solo aceptar
                                                dialogoerror.cancel();
                                                onclickbuscar();
                                            }
                                        });
                                        dialogoerror.show();
                                    }
                                } else {
                                    listaPonencia.add(new HolderPonencia(
                                            "" + res.getString("NOMBRECANCHA"), res.getString("IDCANCHAS"),
                                            "$COP " + res.getString("TARIFA") + "/hora", "" + res.getString("UBICACION"),
                                            "" + res.getString("NOMBRECIUDAD"), "" + res.getString("DIASDISPONIBLE"),
                                            "De " + res.getString("HORAABRIR") + " a " + res.getString("HORACERRAR"),
                                            IMGURL + res.getString("IDCANCHAS") + "/1.jpg"));
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

                return parametros;
            }
        };
        rq.add(jsrqid);
    }

    private void buscarlistarnombre(final String nombre, final String barrio, final String ciu) {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();

        // Showing progress dialog at user registration time.
        jsrqnombre = new StringRequest(Request.Method.POST, URLlistarstring,
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
                                recyclerPonencia.setAdapter(adapter);

                                if (res.has("error")) {
                                    Boolean error = res.getBoolean("error");
                                    if (error) {
                                        AlertDialog.Builder dialogoerror = new AlertDialog.Builder(ctx);
                                        dialogoerror.setTitle("BUSCAR");
                                        dialogoerror.setMessage("\n" + res.getString("mensaje"));
                                        dialogoerror.setCancelable(false);
                                        dialogoerror.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogoerror, int id) {
                                                //Ejecute acciones, deje vacio para solo aceptar
                                                dialogoerror.cancel();
                                                onclickbuscar();
                                            }
                                        });
                                        dialogoerror.show();
                                    }
                                } else {
                                    listaPonencia.add(new HolderPonencia(
                                            "" + res.getString("NOMBRECANCHA"), res.getString("IDCANCHAS"),
                                            "$COP " + res.getString("TARIFA") + "/hora", "" + res.getString("UBICACION"),
                                            "" + res.getString("NOMBRECIUDAD"), "" + res.getString("DIASDISPONIBLE"),
                                            "De " + res.getString("HORAABRIR") + " a " + res.getString("HORACERRAR"),
                                            IMGURL + res.getString("IDCANCHAS") + "/1.jpg"));
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

                parametros.put("nombre", nombre);
                parametros.put("barrio", barrio);
                parametros.put("ciudad", ciu);

                return parametros;
            }
        };
        rq.add(jsrqnombre);
    }

    public void setsearchvalues(boolean search, boolean id, String nombre, String barrio, String ciu) {
        ifsearch = search;
        ifid = id;
        snombre = nombre;
        sbarrio = barrio;
        sciu = ciu;
    }

    private void resetsearchvalues() {
        ifsearch = false;
        ifid = false;
        snombre = null;
        sbarrio = null;
        sciu = null;
    }

    private void onclickbuscar() {
        //title toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Buscar");
        //switch fragment
        Fragment fragmentbuscar = new SearchFragment();
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().replace(R.id.home, fragmentbuscar).commit();
        // hide fab
        FloatingActionButton fabsearch = getActivity().findViewById(R.id.search);
        fabsearch.setVisibility(View.GONE);
    }

    private void onclickself() {
        Fragment fragmentponencia = new PonenciaFragment();
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().replace(R.id.home, fragmentponencia).commit();
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
    public void onResume() {
        super.onResume();
        // Check should we need to refresh the fragment
        if (shouldRefreshOnResume) {
            onclickself();
            shouldRefreshOnResume = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }
    */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}