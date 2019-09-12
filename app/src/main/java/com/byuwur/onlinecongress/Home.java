package com.byuwur.onlinecongress;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DefaultValues dv = new DefaultValues();
    PonenciaFragment pf = new PonenciaFragment();
    private Context ctx;
    public static Activity home;
    //
    private String URLfotoperfil = dv.imgfotoperfil;
    //create edittexts
    private TextView tvusername, tvuserid, tvuseremail;
    //profile photo
    private ImageView fotoperfil;
    //
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
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_home);

        ctx = Home.this;
        home = this;
        //
        Fragment fragmentprofile = new ProfileFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.home, fragmentprofile).commit();
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
        getSupportActionBar().setTitle("Categorías");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#" + getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("color", "0277bd"))));

        final FloatingActionButton fabsearch = findViewById(R.id.search);
        fabsearch.setVisibility(View.GONE);

        DrawerLayout drawer = findViewById(R.id.home_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

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
                    .putString("congreso", "").apply();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putString("nombrecongreso", null).apply();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putString("color", "0277bd").apply();

            deleteCache(ctx);

            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.action_exit) {
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
        }  else if (id == R.id.action_change) {
            Intent intent = new Intent(Home.this, Congresos.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FloatingActionButton fabsearch = findViewById(R.id.search);

        switch (id) {
            case R.id.nav_ponencia:
                getSupportActionBar().setTitle("Ponencias");
                pf.setfragment(true,false,false,false, false);
                Fragment fragmentponencia0 = new PonenciaFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.home, fragmentponencia0).commit();
                break;
            case R.id.nav_conferencia:
                getSupportActionBar().setTitle("Conferencias");
                pf.setfragment(false,true,false,false, false);
                Fragment fragmentponencia1 = new PonenciaFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.home, fragmentponencia1).commit();
                break;
            case R.id.nav_categoria:
                getSupportActionBar().setTitle("Categorías");
                pf.setfragment(false,false,true,false, false);
                Fragment fragmentponencia2 = new PonenciaFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.home, fragmentponencia2).commit();
                break;
            case R.id.nav_agenda:
                getSupportActionBar().setTitle("Agenda");
                pf.setfragment(false,false,false,true, false);
                Fragment fragmentponencia3 = new PonenciaFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.home, fragmentponencia3).commit();
                break;
            case R.id.nav_sobre:
                getSupportActionBar().setTitle("Sobre " + ctx.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("nombrecongreso", null));
                pf.setfragment(false,false,false,false, true);
                Fragment fragmentponencia4 = new PonenciaFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.home, fragmentponencia4).commit();
                break;
            case R.id.nav_perfil:
                getSupportActionBar().setTitle(getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("nombre", null) + " " + getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("apellido", null));
                Fragment fragmentperfil = new ProfileFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.home, fragmentperfil).commit();
                break;
            case R.id.nav_cuenta:
                Intent intent0 = new Intent(Home.this, Cuenta.class);
                startActivity(intent0);
                break;
            case R.id.nav_feed:
                Intent intent1 = new Intent(Home.this, Feed.class);
                startActivity(intent1);
                break;
        }
        DrawerLayout drawer = findViewById(R.id.home_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check should we need to refresh the fragment
        if (shouldRefreshOnResume) {
            setuserdata();
            shouldRefreshOnResume = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
    }
}
