package com.byuwur.onlinecongress;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class AdaptadorFotos extends PagerAdapter {
    private Context ctx;
    private String[] urlimages;
    private ImageView imageviewfoto;

    AdaptadorFotos(Context ctx, String[] urlimages) {
        this.ctx = ctx;
        this.urlimages = urlimages;
    }

    @Override
    public int getCount() {
        return urlimages.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        imageviewfoto = new ImageView(ctx);
        Picasso.get()
                .load(urlimages[position])
                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                .fit().centerCrop()
                .into(imageviewfoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Carga", "Cargada");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("Carga", "Error al cargar");
                        imageviewfoto.setImageResource(R.drawable.no_image);
                        Toast.makeText(ctx, "Ocurrieron errores al cargar algunas imágenes.", Toast.LENGTH_SHORT).show();
                    }
                });
        container.addView(imageviewfoto);

        return imageviewfoto;
        //return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);

        //super.destroyItem(container, position, object);
    }
}
