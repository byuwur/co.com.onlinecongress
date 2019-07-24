package com.mateus.resweb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DefaultValues dv = new DefaultValues();
    private Context ctx;
    //
    private String URLfotoperfil = dv.imgfotoperfil;
    //create edittexts
    private TextView tvusername,tvuserid, tvuseremail;
    //profile photo
    private ImageView fotoperfil;
    //
    private String usrid, usrnombre, usrcorreo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_home);

        ctx = Home.this;

        //
        Fragment fragmentprofile = new ProfileFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.home, fragmentprofile).commit();
        //Textview on side panel
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        tvusername= headerView.findViewById(R.id.usernombre);
        tvuserid= headerView.findViewById(R.id.userid);
        tvuseremail= headerView.findViewById(R.id.useremail);
        fotoperfil= headerView.findViewById(R.id.fotoperfil);

        usrnombre = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("nombre", null);
        usrid = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("id", null);
        usrcorreo = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("email", null);

        //setuserdata();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mi perfil");

        final FloatingActionButton fabsearch = findViewById(R.id.search);
        fabsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportActionBar().setTitle("Buscar");
                //switch fragment
                Fragment fragmentbuscar = new SearchFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.home, fragmentbuscar).commit();
                //hide fab
                fabsearch.setVisibility(View.GONE);
            }
        });

        DrawerLayout drawer = findViewById(R.id.home_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.home_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);dialog.setCancelable(false);
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
                    .putString("email", null).apply();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putString("ciudad", null).apply();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putString("phone", null).apply();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                    .putString("pass", null).apply();

            deleteCache(ctx);

            Intent intent = new Intent(Home.this, Home.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.action_exit) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this); dialog.setCancelable(false);
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
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FloatingActionButton fabsearch = findViewById(R.id.search);

        switch (id) {
            case R.id.nav_ponencia:
                getSupportActionBar().setTitle("Ponencias");
                Fragment fragmentreservar = new PonenciaFragment();getSupportFragmentManager().beginTransaction().replace(R.id.home, fragmentreservar).commit();
                //show fab
                fabsearch.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_ponente:
                getSupportActionBar().setTitle("Ponentes");
                Fragment fragmentreservas = new PonenteFragment();getSupportFragmentManager().beginTransaction().replace(R.id.home, fragmentreservas).commit();
                //show fab
                fabsearch.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_agendado:
                getSupportActionBar().setTitle("Agendados");
                Fragment fragmentfavoritos = new AgendadoFragment();getSupportFragmentManager().beginTransaction().replace(R.id.home, fragmentfavoritos).commit();
                //show fab
                fabsearch.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_perfil:
                getSupportActionBar().setTitle("Mi perfil");
                fabsearch.setVisibility(View.GONE);
                //Fragment fragmentperfil = new PerfilFragment();getSupportFragmentManager().beginTransaction().replace(R.id.home, fragmentperfil).commit();
                break;
            case R.id.nav_cuenta:
                Intent intent1 = new Intent(Home.this, Cuenta.class);
                startActivity(intent1);
                break;
            case R.id.nav_chat:
                Intent intent2 = new Intent(Home.this, Chat.class);
                startActivity(intent2);
                break;
            case R.id.nav_feed:
                Intent intent3 = new Intent(Home.this, Feed.class);
                startActivity(intent3);
                break;
        }
        DrawerLayout drawer = findViewById(R.id.home_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /*
    @Override
    public void onFragmentInteraction(Uri uri) {} */

    private boolean shouldRefreshOnResume = false;
    @Override
    public void onResume() {
        super.onResume();
        // Check should we need to refresh the fragment
        if(shouldRefreshOnResume){
            //setuserdata();
            shouldRefreshOnResume=false;
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
    }

    //DELETE CACHE WHEN SESSION'S GONE
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
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
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
