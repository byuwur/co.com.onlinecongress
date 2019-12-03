package com.byuwur.onlinecongress;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class Ponencia extends AppCompatActivity {

    private static String id, tipo;
    private static DefaultValues dv = new DefaultValues();
    private ProgressDialog prDialog;
    private WebView mWebView;

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_ponencia);

        final Context ctx = Ponencia.this;
        //RequestQueue rq = Volley.newRequestQueue(ctx);
        prDialog = new ProgressDialog(ctx);
        prDialog.setMessage("Por favor, espere...");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setBackgroundColor(Color.parseColor(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("color", "0277bd")));

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(Color.parseColor(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("color", "0277bd")));
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mWebView = findViewById(R.id.webviewrecurso);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public Bitmap getDefaultVideoPoster() {
                return BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                prDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                prDialog.dismiss();
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(dv.url + "recurso.php?ponencia=" + id);

        final View activityRootView = findViewById(R.id.ponencia);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(ctx, 200)) // if more than 200 dp, it's probably a keyboard...
                    mWebView.setVisibility(View.GONE);
                else
                    mWebView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setid(String id) {
        Ponencia.id = id;
    }

    public void settype(String tipo) {
        Ponencia.tipo = tipo;
    }

    private void resetdata() {
        id = null;
    }

    @Override
    protected void onDestroy() {
        mWebView.destroy();
        mWebView = null;
        try {
            super.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setMessage("¿Desea salir del recurso?");
        dialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                resetdata();
                finish();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        dialog.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {
        //The fragment argument representing the section number for this fragment.
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            final Context ctx = getActivity();
            assert ctx != null;
            RequestQueue rq = Volley.newRequestQueue(ctx);
            String URLinfoponencia = dv.url + "infoponencia.php", URLinfoponente = dv.url + "infoponente.php", URLforo = dv.urlraiz, URLautores = dv.url;

            final View viewInfo = inflater.inflate(R.layout.ponencia_info, container, false);
            final View viewComentarios = inflater.inflate(R.layout.ponencia_comentarios, container, false);
            final View viewAutor = inflater.inflate(R.layout.ponencia_autor, container, false);

            assert getArguments() != null;
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                final TextView nombreponencia, instponencia, idiomaponencia, categoriaponencia, resumenponencia, fechaponencia;
                nombreponencia = viewInfo.findViewById(R.id.nombreponencia);
                instponencia = viewInfo.findViewById(R.id.instponencia);
                idiomaponencia = viewInfo.findViewById(R.id.idiomaponencia);
                categoriaponencia = viewInfo.findViewById(R.id.categoriaponencia);
                resumenponencia = viewInfo.findViewById(R.id.resumenponencia);
                fechaponencia = viewInfo.findViewById(R.id.fechaponencia);

                JsonArrayRequest llenarinfoponente = new JsonArrayRequest(Request.Method.GET, URLinfoponencia + "?id=" + id,
                        new Response.Listener<JSONArray>() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onResponse(JSONArray response) {
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject res = response.getJSONObject(i);
                                        nombreponencia.setText(res.getString("Titulo"));
                                        instponencia.setText(res.getString("InstitucionPatrocinadora"));
                                        idiomaponencia.setText(res.getString("Idioma"));
                                        categoriaponencia.setText(res.getString("NombreCategoria"));
                                        resumenponencia.setText(res.getString("Resumen"));
                                        fechaponencia.setText(new SimpleDateFormat("EEEE, dd MMMM/yyyy", new Locale("es"))
                                                .format(Objects.requireNonNull(new SimpleDateFormat("yyyy-MM-dd", new Locale("es")).parse(res.getString("Fecha")))));
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
                rq.add(llenarinfoponente);

                return viewInfo;
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                WebView viewdisp = viewComentarios.findViewById(R.id.webviewcoments);
                viewdisp.setWebChromeClient(new WebChromeClient());
                viewdisp.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
                viewdisp.getSettings().setJavaScriptEnabled(true);
                viewdisp.loadUrl(URLforo);
                return viewComentarios;
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 3 && Ponencia.tipo.equals("0")) {
                final TextView nombreponente, instponente, nivelponente, paisponente, resumenponente;
                final ImageView imgponente;
                imgponente = viewAutor.findViewById(R.id.imgponente);
                nombreponente = viewAutor.findViewById(R.id.nombreponente);
                instponente = viewAutor.findViewById(R.id.instponente);
                nivelponente = viewAutor.findViewById(R.id.nivelponente);
                paisponente = viewAutor.findViewById(R.id.paisponente);
                resumenponente = viewAutor.findViewById(R.id.resumenponente);

                JsonArrayRequest llenarinfoponente = new JsonArrayRequest(Request.Method.GET, URLinfoponente + "?id=" + id,
                        new Response.Listener<JSONArray>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onResponse(JSONArray response) {
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject res = response.getJSONObject(i);
                                        nombreponente.setText(res.getString("Nombres") + " " + res.getString("Apellidos"));
                                        instponente.setText(res.getString("Institucion"));
                                        nivelponente.setText(res.getString("NivelFormacion"));
                                        paisponente.setText(res.getString("NombrePais"));
                                        resumenponente.setText(res.getString("ResumenPonente"));
                                        final String telefono = res.getString("Telefono").replaceAll("\\+", "");
                                        Button whatsapp = viewAutor.findViewById(R.id.whatsapp);
                                        whatsapp.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://wa.me/" + telefono)));
                                            }
                                        });
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
                rq.add(llenarinfoponente);

                return viewAutor;
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 3 && Ponencia.tipo.equals("1") ) {
                WebView webviewautores = viewComentarios.findViewById(R.id.webviewcoments);
                webviewautores.setWebChromeClient(new WebChromeClient());
                webviewautores.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
                webviewautores.getSettings().setJavaScriptEnabled(true);
                webviewautores.loadUrl(URLautores + "infoautores.php?id=" + Ponencia.id);
                return viewComentarios;
        } else {
                return null;
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
