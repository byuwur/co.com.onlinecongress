package com.byuwur.onlinecongress;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DefaultValues dv = new DefaultValues();
    //register file to request
    private String URLpais = dv.url + "paises.php", URLdep = dv.url + "departamentos.php", URLciu = dv.url + "ciudades.php";
    //set context
    private Context ctx;
    //
    private RequestQueue rq;
    //create request
    private JsonArrayRequest jsrqdep, jsrqciu;
    //list of each array
    private ArrayList<String> dep = new ArrayList<>(), iddep = new ArrayList<>();
    private ArrayList<String> ciudad = new ArrayList<>(), idciudad = new ArrayList<>();
    private ArrayList<String> op = new ArrayList<>();
    private String buscariddepar = "", buscaridciudad = "";
    private boolean buscarid;
    private EditText barriosearch, textsearch;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        //BUT TWICE BECAUSE OF BUGS
        final Snackbar snackbar = Snackbar.make(view, "Filtre las ponencias o conferencias según su categoria.",
                Snackbar.LENGTH_INDEFINITE).setAction("Action", null);
        snackbar.show();
        //
        ctx = getActivity();
        assert ctx != null;
        rq = Volley.newRequestQueue(ctx);

        textsearch = view.findViewById(R.id.textsearch);
        //option values
        op.add("Nombre");
        op.add("ID");
        //initial values
        dep.add("[--- Departamentos ---]");
        iddep.add("0");
        ciudad.add("[--- Ciudades ---]");
        idciudad.add("0");
        //fill dep
        llenardepartamentos();
        //SPINNER STUFF
        final Spinner spinnerciu = view.findViewById(R.id.spinnerciudad);
        //set the spinner value from Arraylist CIUDAD
        ArrayAdapter<String> adapterciu = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, ciudad);
        adapterciu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerciu.setAdapter(adapterciu);
        spinnerciu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                //Toast.makeText(ctx, adapterView.getItemAtPosition(pos)+". "+idciudad.get(pos), Toast.LENGTH_SHORT).show();
                buscaridciudad = idciudad.get(pos);
                barriosearch.setEnabled(true);
                barriosearch.setClickable(true);
                barriosearch.setText(null);
                barriosearch.setHint("Barrio (Opcional)");
                if (pos == 0) {
                    buscaridciudad = "";
                    barriosearch.setEnabled(false);
                    barriosearch.setClickable(false);
                    barriosearch.setText(null);
                    barriosearch.setHint("Barrio (ingrese ciudad)");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //search button script
        Button buttontoreserv = view.findViewById(R.id.buttonbuscar);
        buttontoreserv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PonenciaFragment rf = new PonenciaFragment();
                rf.setsearchvalues(true, true, textsearch.getText().toString(), "", "");
                snackbar.dismiss();
                onclickbuscar();
            }
        });

        return view;
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

    private void onclickbuscar() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Ponencia");
        Fragment fragmentreservar = new PonenciaFragment();
        /*switch fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.home, fragmentreservar);
        fragmentTransaction.commit();
        */
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().replace(R.id.home, fragmentreservar).commit();
        //show fab
        FloatingActionButton fabsearch = getActivity().findViewById(R.id.search);
        fabsearch.setVisibility(View.VISIBLE);
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
