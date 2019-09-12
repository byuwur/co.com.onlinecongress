package com.byuwur.onlinecongress;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
    private static String usrid, usrciudad, scategoria = "", congreso;
    private DefaultValues dv = new DefaultValues();
    //register file to request
    private String IMGURL = dv.imgcanchasurl, URLcat = dv.url + "categorias.php", URLponcat = dv.url + "poncat.php",
            URLpon = dv.url + "ponencias.php", URLcon = dv.url + "conferencias.php", URLage = dv.url + "agenda.php", URLinfo = dv.urlraiz + "";
    //set context, requestqueue
    private Context ctx;
    private RequestQueue rq;
    //create request
    private StringRequest jsrqllenar;
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
        congreso = getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("congreso", null);

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
            if (ifponencia) {
                llenarponencia();
            } else if (ifconferencia) {
                llenarconferencia();
            } else if (ifcategoria) {
                adapter.setonItemClickListener(new AdaptadorPonencia.onItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String id = ((TextView) recyclerPonencia.findViewHolderForAdapterPosition(position)
                                .itemView.findViewById(R.id.ponenciaid)).getText().toString();
                        scategoria = id.replaceAll("Categoría: ", "");
                        //Toast.makeText(ctx, "CAT: "+scategoria, Toast.LENGTH_SHORT).show();
                        onclickself(false, false, true, false, false);
                    }
                });
                if (scategoria == null || scategoria.equalsIgnoreCase("")) {
                    llenarcategoria();
                } else {
                    llenarponcat(scategoria);
                    scategoria = "";
                }
            } else if (ifagenda) {
                llenaragenda();
            } else {
                return viewno;
            }
            return viewsi;
        } else if (ifsobre) {
            final ProgressDialog[] prDialog = new ProgressDialog[1];
            final WebView webviewsobre = viewsobre.findViewById(R.id.webviewsobre);
            webviewsobre.setWebChromeClient(new WebChromeClient());
            webviewsobre.getSettings().setJavaScriptEnabled(true);
            webviewsobre.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
                    if (prDialog[0] != null) {
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

    private void llenarcategoria() {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();
        // Showing progress dialog at user registration time.
        jsrqllenar = new StringRequest(Request.Method.GET, URLcat + "?congreso=" + congreso ,
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
                                            "" + res.getString("Categoria"),
                                            "Categoría: " + res.getString("Id"),
                                            "Congreso: " + res.getString("IdCongreso"),
                                            "", "", "",
                                            IMGURL + res.getString("ImgCategoria") + "/1.jpg"));
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
        });
        rq.add(jsrqllenar);
    }

    private void llenarponcat(final String categoria) {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();
        // Showing progress dialog at user registration time.
        jsrqllenar = new StringRequest(Request.Method.GET, URLponcat + "?congreso=" + congreso + "&categoria=" + categoria,
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
                                            "" + res.getString("Titulo"),
                                            "Id: " + res.getString("IdPonencia"),
                                            "Institución: " + res.getString("InstitucionPatrocinadora"),
                                            "Idioma: " + res.getString("Idioma"),
                                            "Categoría: " + res.getString("Categoria"),
                                            "Fecha: " + res.getString("Fecha"),
                                            IMGURL + "/1.jpg"));
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
        });
        rq.add(jsrqllenar);
    }

    private void llenarponencia() {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();
        // Showing progress dialog at user registration time.
        jsrqllenar = new StringRequest(Request.Method.GET, URLpon + "?congreso=" + congreso,
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
                                            "" + res.getString("Titulo"),
                                            "Id: " + res.getString("IdPonencia"),
                                            "Institución: " + res.getString("InstitucionPatrocinadora"),
                                            "Idioma: " + res.getString("Idioma"),
                                            "Categoría: " + res.getString("Categoria"),
                                            "Fecha: " + res.getString("Fecha"),
                                            IMGURL + "/1.jpg"));
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
        });
        rq.add(jsrqllenar);
    }

    private void llenarconferencia() {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();
        // Showing progress dialog at user registration time.
        jsrqllenar = new StringRequest(Request.Method.GET, URLcon + "?congreso=" + congreso,
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
                                            "" + res.getString("Titulo"),
                                            "Id: " + res.getString("IdPonencia"),
                                            "Institución: " + res.getString("InstitucionPatrocinadora"),
                                            "Idioma: " + res.getString("Idioma"),
                                            "Categoría: " + res.getString("Categoria"),
                                            "Fecha: " + res.getString("Fecha"),
                                            IMGURL + "/1.jpg"));
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
        });
        rq.add(jsrqllenar);
    }

    private void llenaragenda() {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();
        // Showing progress dialog at user registration time.
        jsrqllenar = new StringRequest(Request.Method.GET, URLage + "?congreso=" + congreso,
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
                                            "" + res.getString("Titulo"),
                                            "Id: " + res.getString("IdPonencia"),
                                            "Institución: " + res.getString("InstitucionPatrocinadora"),
                                            "Idioma: " + res.getString("Idioma"),
                                            "Categoría: " + res.getString("Categoria"),
                                            "Fecha: " + res.getString("Fecha"),
                                            IMGURL + "/1.jpg"));
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
        });
        rq.add(jsrqllenar);
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