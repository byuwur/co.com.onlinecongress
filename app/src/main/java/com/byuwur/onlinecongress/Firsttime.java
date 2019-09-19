package com.byuwur.onlinecongress;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Firsttime extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_firsttime);

        //Animation alpha, downtoup;

        String buscaridcongreso = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("congreso", "");
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);
        boolean loginsesion = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
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

        /*
        ImageView l1 = findViewById(R.id.l1);
        TextView textviewapc = findViewById(R.id.textviewapc);
        TextView textviewapc2 = findViewById(R.id.textviewapc2);
        TextView textviewapc3 = findViewById(R.id.textviewapc3);
        Button botoniniciar = findViewById(R.id.botoniniciar);

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
