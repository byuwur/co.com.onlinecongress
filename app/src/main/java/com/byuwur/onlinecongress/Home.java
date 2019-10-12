package com.byuwur.onlinecongress;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.andremion.counterfab.CounterFab;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.Objects;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DefaultValues dv = new DefaultValues();
    private PonenciaFragment pf = new PonenciaFragment();
    private Context ctx;
    private RequestQueue rq;
    //
    private String URLfotoperfil = dv.imgfotoperfil;
    private TextView tvusername, tvuserid, tvuseremail;
    private ImageView fotoperfil;
    private CounterFab fabnotif;
    private String usrid, usrnombre, usrcorreo;
    private boolean shouldRefreshOnResume = false;

    //DELETE CACHE WHEN SESSION'S GONE
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            assert children != null;
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_home);

        ctx = Home.this;
        rq = Volley.newRequestQueue(ctx);

        pf.setfragment(false, false, false, true, false);
        getSupportFragmentManager().beginTransaction().replace(R.id.home, new PonenciaFragment()).commit();
        //Textview on side panel
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        headerView.findViewById(R.id.navbg);
        headerView.setBackgroundColor(Color.parseColor("#" + getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("color", "0277bd")));

        tvusername = headerView.findViewById(R.id.usernombre);
        tvuserid = headerView.findViewById(R.id.userid);
        tvuseremail = headerView.findViewById(R.id.useremail);
        fotoperfil = headerView.findViewById(R.id.fotoperfil);

        usrnombre = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("nombre", null) + " " + getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("apellido", null);
        usrid = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("id", null);
        usrcorreo = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("email", null);

        setuserdata();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Agenda");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("color", "0277bd"))));

        fabnotif = findViewById(R.id.search);
        fabnotif.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#" + getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("color", "0277bd"))));
        fabnotif.setVisibility(View.VISIBLE);
        fabnotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Anuncios y notificaciones");
                getSupportFragmentManager().beginTransaction().replace(R.id.home, new NotifFragment()).commit();
                fabnotif.setVisibility(View.GONE);
            }
        });
        loadnotif();

        DrawerLayout drawer = findViewById(R.id.home_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //BROADCAST RECEIVER TO FINISH ACTIVITY REMOTELY
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                if (Objects.equals(intent.getAction(), "finish_home"))
                    finish();
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_home"));
    }

    @SuppressLint("SetTextI18n")
    private void setuserdata() {
        tvusername.setText(usrnombre);
        tvuserid.setText(usrcorreo);
        tvuseremail.setText("#" + usrid);

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.home_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setMessage("¿Desea salir de la aplicación?");
            dialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if user pressed "yes", then he is allowed to exit from application
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
    }

    /*
    @Override
    public void onFragmentInteraction(Uri uri) {} */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
            deleteCache(ctx);
            startActivity(new Intent(Home.this, Login.class));
            finish();
        } else if (id == R.id.action_exit) {
            super.onBackPressed();
        } else if (id == R.id.action_change) {
            startActivity(new Intent(Home.this, Congresos.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FloatingActionButton fabnotif = findViewById(R.id.search);
        fabnotif.setVisibility(View.VISIBLE);
        switch (id) {
            case R.id.nav_ponencia:
                Objects.requireNonNull(getSupportActionBar()).setTitle("Ponencias");
                pf.setfragment(true, false, false, false, false);
                getSupportFragmentManager().beginTransaction().replace(R.id.home, new PonenciaFragment()).commit();
                break;
            case R.id.nav_conferencia:
                Objects.requireNonNull(getSupportActionBar()).setTitle("Conferencias");
                pf.setfragment(false, true, false, false, false);
                getSupportFragmentManager().beginTransaction().replace(R.id.home, new PonenciaFragment()).commit();
                break;
            case R.id.nav_categoria:
                Objects.requireNonNull(getSupportActionBar()).setTitle("Categorías");
                pf.setfragment(false, false, true, false, false);
                getSupportFragmentManager().beginTransaction().replace(R.id.home, new PonenciaFragment()).commit();
                break;
            case R.id.nav_agenda:
                Objects.requireNonNull(getSupportActionBar()).setTitle("Agenda");
                pf.setfragment(false, false, false, true, false);
                getSupportFragmentManager().beginTransaction().replace(R.id.home, new PonenciaFragment()).commit();
                break;
            case R.id.nav_sobre:
                Objects.requireNonNull(getSupportActionBar()).setTitle("Sobre " + ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("nombrecongreso", null));
                pf.setfragment(false, false, false, false, true);
                getSupportFragmentManager().beginTransaction().replace(R.id.home, new PonenciaFragment()).commit();
                break;
            case R.id.nav_perfil:
                Objects.requireNonNull(getSupportActionBar()).setTitle(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("nombre", null) + " " + getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("apellido", null));
                getSupportFragmentManager().beginTransaction().replace(R.id.home, new ProfileFragment()).commit();
                break;
            case R.id.nav_cuenta:
                startActivity(new Intent(Home.this, Cuenta.class));
                break;
            case R.id.nav_feed:
                startActivity(new Intent(Home.this, Feed.class));
                break;
        }
        DrawerLayout drawer = findViewById(R.id.home_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadnotif() {
        String URLnotif = dv.url + "notificaciones.php?congreso=" + getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("congreso", null);
        StringRequest jsrqllenar = new StringRequest(Request.Method.GET, URLnotif,
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
                        int resplength = resp.length() - getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                                .getInt("notif", 0);
                        for (int i = 0; i < resplength; i++) {
                            try {
                                fabnotif.increase();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        rq.add(jsrqllenar);
    }

    private void logout() {
        //LOGOUT ACTION
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("loginsesion", false).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("id", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("nombre", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("apellido", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("email", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("ciudad", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("phone", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("pass", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("sexo", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("institucion", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("congreso", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("nombrecongreso", null).apply();
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putString("color", "0277bd").apply();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldRefreshOnResume) {
            setuserdata();
            //loadnotif();
            shouldRefreshOnResume = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
    }
}
