package com.mateus.resweb;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Firsttime extends AppCompatActivity {
    private ImageView l1;
    //private Animation alpha,downtoup;
    private TextView textviewapc,textviewapc2,textviewapc3;
    private Button botoniniciar;
    private Boolean isFirstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_firsttime);

        isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (!isFirstRun) {
            Intent intentiniciar = new Intent(Firsttime.this, Home.class);
            startActivity(intentiniciar);
            finish();
        }

        l1 = findViewById(R.id.l1);
        textviewapc = findViewById(R.id.textviewapc);
        textviewapc2 = findViewById(R.id.textviewapc2);
        textviewapc3 = findViewById(R.id.textviewapc3);
        botoniniciar = findViewById (R.id.botoniniciar);

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

    public void onClickIniciar(View view){
        //Convertir a falso para no mostrar la bienvenida
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).apply();
        Intent intentiniciar = new Intent(Firsttime.this, Home.class);
        startActivity(intentiniciar);
        finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this); dialog.setCancelable(false);
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
