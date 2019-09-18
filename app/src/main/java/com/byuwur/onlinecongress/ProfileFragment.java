package com.byuwur.onlinecongress;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    DefaultValues dv = new DefaultValues();
    //login file to request
    private String URLnombreciudad = dv.url + "nombreciudad.php", URLfotoperfil = dv.imgfotoperfil;
    //
    private RequestQueue rq;
    //set context
    private Context ctx;
    //
    private JsonArrayRequest jsrqnombreciudad;
    private Button buttoneditar;
    private ImageView fotoperfil;
    private TextView nombreperfil, correoperfil, telefonoperfil, institucionperfil, congresoperfil;
    private String usrnombre, usrcorreo, usrphone, usrciudad, usrid, usrinst, idcongreso, nombrecongreso, colorcongreso;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private boolean shouldRefreshOnResume = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //
        ctx = getActivity();
        assert ctx != null;
        rq = Volley.newRequestQueue(ctx);
        //
        usrid = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("id", null);

        fotoperfil = view.findViewById(R.id.fotoperfil);

        nombreperfil = view.findViewById(R.id.nombreperfil);
        correoperfil = view.findViewById(R.id.correoperfil);
        telefonoperfil = view.findViewById(R.id.telefonoperfil);
        institucionperfil = view.findViewById(R.id.institucionperfil);
        congresoperfil = view.findViewById(R.id.congresoperfil);

        settexts();

        buttoneditar = view.findViewById(R.id.buttoneditar);
        buttoneditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ctx, Cuenta.class);
                startActivity(intent1);
            }
        });
        buttoneditar.setBackgroundColor(Color.parseColor("#" + ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("color", "0277bd")));
        return view;
    }

    private void settexts() {
        usrnombre = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("nombre", null) + " " + ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("apellido", null);
        usrcorreo = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("email", null);
        usrphone = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("phone", null);
        usrinst = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("institucion", null);
        idcongreso = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("congreso", "");
        nombrecongreso = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("nombrecongreso", null);
        colorcongreso = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("color", null);

        nombreperfil.setText(usrnombre);
        correoperfil.setText(usrcorreo);
        telefonoperfil.setText(usrphone);
        institucionperfil.setText(usrinst);
        congresoperfil.setText(nombrecongreso);

        setnombreciudad();

        //LOAD IMAGE
        Picasso.get().load(URLfotoperfil + usrid + "/1.jpg")
                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                .fit().centerCrop()
                .into(fotoperfil, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Carga", "Cargada");
                    }

                    @Override
                    public void onError(Exception e) {
                        fotoperfil.setImageResource(R.drawable.no_profile);
                    }
                });
    }

    private void setnombreciudad() {
        usrciudad = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("ciudad", null);
        jsrqnombreciudad = new JsonArrayRequest(Request.Method.GET, URLnombreciudad + "?ciudad=" + usrciudad,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject res = response.getJSONObject(i);
                                if (res.has("name_ciu")) {
                                    //ciudadperfil.setText(res.getString("name_ciu"));
                                } else {
                                    //ciudadperfil.setText(usrciudad);
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
