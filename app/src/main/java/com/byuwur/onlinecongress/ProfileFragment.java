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

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    DefaultValues dv = new DefaultValues();
    private Context ctx;

    private ImageView fotoperfil;
    private TextView nombreperfil, correoperfil, telefonoperfil, institucionperfil, congresoperfil;

    private OnFragmentInteractionListener mListener;
    private boolean shouldRefreshOnResume = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

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
            // TODO: Rename and change types of parameters
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
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
        String usrid = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("id", null);

        fotoperfil = view.findViewById(R.id.fotoperfil);

        nombreperfil = view.findViewById(R.id.nombreperfil);
        correoperfil = view.findViewById(R.id.correoperfil);
        telefonoperfil = view.findViewById(R.id.telefonoperfil);
        institucionperfil = view.findViewById(R.id.institucionperfil);
        congresoperfil = view.findViewById(R.id.congresoperfil);

        settexts();

        Button buttoneditar = view.findViewById(R.id.buttoneditar);
        buttoneditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ctx, Cuenta.class);
                startActivity(intent1);
            }
        });
        buttoneditar.setBackgroundColor(Color.parseColor(ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("color", "0277bd")));
        return view;
    }

    private void settexts() {
        String usrnombre = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("nombre", null) + " " + ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("apellido", null);
        String usrcorreo = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("email", null);
        String usrphone = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("phone", null);
        String usrinst = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("institucion", null);
        String idcongreso = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("congreso", null);
        String nombrecongreso = ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("nombrecongreso", null);

        nombreperfil.setText(usrnombre);
        correoperfil.setText(usrcorreo);
        telefonoperfil.setText(usrphone);
        institucionperfil.setText(usrinst);
        congresoperfil.setText(nombrecongreso);

        //LOAD IMAGE
        Picasso.get().load(dv.urlraiz + "congreso/Fotografias/Logos_Congresos/" + idcongreso + "/1.jpg")
                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                .fit().centerInside()
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

    @Override
    public void onResume() {
        super.onResume();
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
