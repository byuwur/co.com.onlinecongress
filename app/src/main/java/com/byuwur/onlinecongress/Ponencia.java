package com.byuwur.onlinecongress;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class Ponencia extends AppCompatActivity {

    private static String id;
    private static DefaultValues dv = new DefaultValues();
    private ProgressDialog prDialog;

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_ponencia);

        Context ctx = Ponencia.this;
        //RequestQueue rq = Volley.newRequestQueue(ctx);
        prDialog = new ProgressDialog(ctx);
        prDialog.setMessage("Por favor, espere...");

        AppBarLayout appbar = findViewById(R.id.appbar);
        appbar.setBackgroundColor(Color.parseColor("#" + getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("color", "0277bd")));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(id);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        WebView mWebView = findViewById(R.id.webviewrecurso);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
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
        mWebView.loadUrl(dv.urlraiz);
    }

    public void setid(String id) {
        Ponencia.id = id;
    }

    private void resetdata() {
        id = null;
    }

    @Override
    public void onBackPressed() {
        resetdata();
        finish();
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

            final View viewInfo = inflater.inflate(R.layout.ponencia_info, container, false);
            final View viewComentarios = inflater.inflate(R.layout.ponencia_comentarios, container, false);
            final View viewAutor = inflater.inflate(R.layout.ponencia_autor, container, false);

            assert getArguments() != null;
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                WebView viewdisp = viewInfo.findViewById(R.id.webviewinfo);
                viewdisp.setWebChromeClient(new WebChromeClient());
                viewdisp.getSettings().setJavaScriptEnabled(true);
                viewdisp.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
                viewdisp.loadUrl(dv.urlraiz);
                return viewInfo;
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                WebView viewdisp = viewComentarios.findViewById(R.id.webviewcoments);
                viewdisp.setWebChromeClient(new WebChromeClient());
                viewdisp.getSettings().setJavaScriptEnabled(true);
                viewdisp.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
                viewdisp.loadUrl(dv.urlraiz);
                return viewComentarios;
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 3) {
                WebView viewdisp = viewAutor.findViewById(R.id.webviewautor);
                viewdisp.setWebChromeClient(new WebChromeClient());
                viewdisp.getSettings().setJavaScriptEnabled(true);
                viewdisp.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });
                viewdisp.loadUrl(dv.urlraiz);
                return viewAutor;
            } else {
                return null;
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

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
