package com.tabletlink;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

public class MainActivity extends Activity {
    WebView w;
    SharedPreferences p;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        p = getSharedPreferences("t", 0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        w = new WebView(this);
        setContentView(w);
        WebSettings s = w.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        w.setWebViewClient(new WebViewClient() {
            @Override public void onReceivedError(WebView v, int c, String d, String u) {
                v.loadData("<body style='background:#0d1117;color:#eee;font:22px sans-serif;padding:40px'>"
                    + "Cannot reach the PC.<br><br>Make sure TabletLink is running on the PC, "
                    + "the address is right, and both are on the same Wi-Fi (or USB is connected).<br><br>"
                    + "Press Back to change the address.</body>", "text/html", "utf-8");
            }
        });
        String a = p.getString("a", null);
        if (a == null) ask(); else load(a);
    }

    void load(String a) {
        a = a.trim();
        if (!a.startsWith("http")) a = "http://" + a;
        w.loadUrl(a);
    }

    void ask() {
        final EditText e = new EditText(this);
        e.setHint("10.98.86.77:8765  or  localhost:8765 for USB");
        e.setText(p.getString("a", ""));
        e.setSingleLine();
        new AlertDialog.Builder(this).setTitle("PC address").setView(e)
            .setPositiveButton("Connect", (d, i) -> {
                String a = e.getText().toString().trim();
                if (a.length() > 0) { p.edit().putString("a", a).apply(); load(a); }
            })
            .setNegativeButton("Cancel", null)
            .setNeutralButton("Exit", (d, i) -> finish())
            .show();
    }

    @Override public void onBackPressed() { ask(); }

    @Override public void onWindowFocusChanged(boolean f) {
        super.onWindowFocusChanged(f);
        if (f) getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
