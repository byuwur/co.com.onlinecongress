package com.byuwur.onlinecongress;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import static android.content.Context.MODE_PRIVATE;

public class NotifFragment extends Fragment {
    private DefaultValues dv = new DefaultValues();
    //register file to request
    private String URLnotif = dv.url + "notificaciones.php", congreso;
    //set context, requestqueue
    private Context ctx;
    private RequestQueue rq;
    //create request
    private StringRequest jsrqllenar;
    //
    private OnFragmentInteractionListener mListener;

    private ArrayList<HolderNotif> listaNotif;
    private RecyclerView recyclerNotif;
    private AdaptadorNotif adapter;
    private boolean shouldRefreshOnResume = false;

    public NotifFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notif, container, false);
        ctx = getActivity();
        assert ctx != null;
        rq = Volley.newRequestQueue(ctx);

        //GET VALUES FROM USER
        congreso = getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("congreso", null);

        //VIEWSI ELEMENTS
        listaNotif = new ArrayList<>();
        recyclerNotif = view.findViewById(R.id.recyclerNotif);
        recyclerNotif.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdaptadorNotif(listaNotif);
        recyclerNotif.setAdapter(adapter);

        llenarnotif();

        AlertDialog.Builder dialogoerror = new AlertDialog.Builder(ctx);
        dialogoerror.setTitle("Para tener en cuenta");
        dialogoerror.setMessage("\nLas novedades, anuncios y notificaciones que se presenten hasta la culminación del congreso se presentarán aquí.\nRecuerde revisarlas a menudo para mantenerse informado.");
        dialogoerror.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogoerror, int id) {
                //Ejecute acciones, deje vacio para solo aceptar
                dialogoerror.cancel();
            }
        });
        //dialogoerror.show();

        return view;
    }

    private void llenarnotif() {
        // Showing progress dialog at user registration time.
        final ProgressDialog progreso = new ProgressDialog(ctx);
        progreso.setMessage("Por favor, espere...");
        progreso.show();
        // Showing progress dialog at user registration time.
        jsrqllenar = new StringRequest(Request.Method.GET, URLnotif + "?congreso=" + congreso ,
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
                                recyclerNotif.setAdapter(adapter);

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
                                    listaNotif.add(new HolderNotif(
                                            ">> " + res.getString("Notificacion"),
                                            "Fecha: " + res.getString("FechaNotificacion")));
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
            }
        });
        rq.add(jsrqllenar);
    }

    private void onclickself() {
        Fragment fragmentponencia = new NotifFragment();
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
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}