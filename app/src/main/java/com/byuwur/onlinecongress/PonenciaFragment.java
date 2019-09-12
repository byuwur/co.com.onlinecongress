package com.byuwur.onlinecongress;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
    private static boolean ifponencia, ifconferencia, ifcategoria, ifagenda, ifsobre;
    private static String snombre, sbarrio, sciu, usrid, usrciudad;
    private DefaultValues dv = new DefaultValues();
    //register file to request
    private String IMGURL = dv.imgcanchasurl, URLid = dv.url + "buscarid.php", URLactciudad = dv.url + "anadirciudad.php",
            URLlistarstring = dv.url + "buscarnombre.php", URLlistarciudad = dv.url + "listarciudad.php";
    //set context, requestqueue
    private Context ctx;
    private RequestQueue rq;
    //create request
    private JsonArrayRequest jsrqdep, jsrqciu;
    private StringRequest jsrqciudad, jsrqid, jsrqnombre, jsrqactciudad;
    //
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

        View viewsobre = inflater.inflate(R.layout.fragment_sobre, container, false);
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
        if (ifponencia || ifconferencia || ifcategoria || ifagenda) {
            return viewsi;
        } else if (ifsobre) {
            final ProgressDialog[] prDialog = new ProgressDialog[1];
            final WebView webviewsobre = viewsobre.findViewById(R.id.webviewsobre);
            webviewsobre.setWebChromeClient(new WebChromeClient());
            webviewsobre.getSettings().setJavaScriptEnabled(true);
            webviewsobre.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url){
                    view.loadUrl(url);
                    return true;
                }
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    prDialog[0] = new ProgressDialog(ctx);
                    prDialog[0].setMessage("Por favor, espere...");
                    prDialog[0].show();
                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if(prDialog[0] !=null){
                        prDialog[0].dismiss();
                    }
                }
            });
            webviewsobre.loadUrl("http://covaite.com");
            return viewsobre;
        } else {
            return viewno;
        }
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

    public void setfragment(boolean ponencia, boolean conferencia, boolean categoria, boolean agenda, boolean sobre) {
        ifponencia = ponencia;
        ifconferencia = conferencia;
        ifcategoria = categoria;
        ifagenda = agenda;
        ifsobre = sobre;
    }

    private void resetfragment() {
        ifponencia = false;
        ifconferencia = false;
        ifcategoria = false;
        ifagenda = false;
        ifsobre = false;
    }

    private void onclickself(boolean ponencia, boolean conferencia, boolean categoria, boolean agenda, boolean sobre) {
        setfragment(ponencia, conferencia, categoria, agenda, sobre);
        Fragment fragmentponencia = new PonenciaFragment();
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().replace(R.id.home, fragmentponencia).commit();
    }

    private void onclickself() {
        Fragment fragmentponencia = new PonenciaFragment();
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().replace(R.id.home, fragmentponencia).commit();
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