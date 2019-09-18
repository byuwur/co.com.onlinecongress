package com.byuwur.onlinecongress;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Firsttime extends AppCompatActivity {
    private ImageView l1;
    //private Animation alpha,downtoup;
    private String buscaridcongreso;
    private Context ctx;
    private TextView textviewapc, textviewapc2, textviewapc3;
    private Button botoniniciar;
    private Boolean isFirstRun, loginsesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_firsttime);

        ctx = Firsttime.this;
        buscaridcongreso = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("congreso", "");
        isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        loginsesion = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("loginsesion", false);

        if (!isFirstRun) {
            if (loginsesion) {
                if (buscaridcongreso.equals("")) {
                    Intent intentiniciar = new Intent(Firsttime.this, Congresos.class);
                    startActivity(intentiniciar);
                    finish();
                } else {
                    Intent intentiniciar = new Intent(Firsttime.this, Login.class);
                    startActivity(intentiniciar);
                    finish();
                }
            } else {
                Intent intentiniciar = new Intent(Firsttime.this, Login.class);
                startActivity(intentiniciar);
                finish();
            }
        }

        l1 = findViewById(R.id.l1);
        textviewapc = findViewById(R.id.textviewapc);
        textviewapc2 = findViewById(R.id.textviewapc2);
        textviewapc3 = findViewById(R.id.textviewapc3);
        botoniniciar = findViewById(R.id.botoniniciar);

        /*
        alpha = AnimationUtils.loadAnimation(this, com.APC.Reserv.R.anim.alpha);
        downtoup = AnimationUtils.loadAnimation(this, com.APC.Reserv.R.anim.downtoup);

        l1.setAnimation(alpha);

        textviewapc3.setAnimation(alpha);
        textviewapc2.setAnimation(alpha);
        textviewapc.setAnimation(alpha);
        botoniniciar.setAnimation(alpha);

        l1.setAnimation(downtoup);
        */

    }

    public void onClickIniciar(View view) {
        //Convertir a falso para no mostrar la bienvenida
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();
        Intent intentiniciar = new Intent(Firsttime.this, Login.class);
        startActivity(intentiniciar);
        finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setMessage("¿Desea salir de la aplicación?");
        dialog.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.create();
        dialog.show();
    }
}
