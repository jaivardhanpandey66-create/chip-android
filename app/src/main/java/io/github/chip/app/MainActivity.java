package io.github.chip.app;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String PREFS = "chip";
    private static final String KEY_URL = "server_url";

    private LinearLayout setup;
    private FrameLayout shell;
    private EditText urlField;
    private WebView wv;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        shell = new FrameLayout(this);
        shell.setBackgroundColor(Color.rgb(2, 6, 12));

        String url = prefs().getString(KEY_URL, "");
        if (!url.isEmpty()) {
            setContentView(shell);
            open(url);
        } else {
            buildSetup();
            setContentView(setup);
        }
        requestMic();
    }

    private SharedPreferences prefs() {
        return getSharedPreferences(PREFS, MODE_PRIVATE);
    }

    private int dp(float v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    private void buildSetup() {
        setup = new LinearLayout(this);
        setup.setOrientation(LinearLayout.VERTICAL);
        setup.setGravity(Gravity.CENTER);
        setup.setBackgroundColor(Color.rgb(2, 6, 12));
        int pad = dp(28);
        setup.setPadding(pad, pad, pad, pad);

        TextView title = new TextView(this);
        title.setText("CHIP");
        title.setTextColor(Color.WHITE);
        title.setTextSize(38);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        TextView sub = new TextView(this);
        sub.setText("Connect to your CHIP agent server.\n\n" +
                "Run  python3 chip_web.py  on your PC, then enter its " +
                "address here.\n(PC and phone must be on the same Wi-Fi.)");
        sub.setTextColor(Color.rgb(109, 140, 170));
        sub.setTextSize(14);
        sub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.topMargin = dp(12);
        sub.setLayoutParams(subLp);

        urlField = new EditText(this);
        urlField.setHint("http://192.168.1.50:8000");
        urlField.setTextColor(Color.WHITE);
        urlField.setHintTextColor(Color.rgb(90, 110, 130));
        urlField.setTextSize(16);
        LinearLayout.LayoutParams fieldLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fieldLp.topMargin = dp(24);
        urlField.setLayoutParams(fieldLp);

        Button go = new Button(this);
        go.setText("Connect");
        go.setOnClickListener(v -> saveAndOpen(urlField.getText().toString()));

        LinearLayout.LayoutParams goLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        goLp.topMargin = dp(14);
        go.setLayoutParams(goLp);

        setup.addView(title);
        setup.addView(sub);
        setup.addView(urlField);
        setup.addView(go);
    }

    private void saveAndOpen(String raw) {
        String url = raw.trim();
        if (url.isEmpty()) return;
        if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://" + url;
        while (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        prefs().edit().putString(KEY_URL, url).apply();
        setContentView(shell);
        open(url);
    }

    private void open(String url) {
        wv = new WebView(this);
        wv.setBackgroundColor(Color.rgb(2, 6, 12));

        WebSettings s = wv.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http://") || url.startsWith("https://")) return false;
                return true;
            }
        });

        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());   // allow mic capture for voice-in
            }
        });

        Button serverBtn = new Button(this);
        serverBtn.setText("Server");
        serverBtn.setBackgroundColor(Color.argb(210, 10, 20, 35));
        serverBtn.setTextColor(Color.rgb(127, 215, 255));
        serverBtn.setPadding(dp(14), 0, dp(14), 0);
        FrameLayout.LayoutParams btnLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.TOP | Gravity.RIGHT);
        btnLp.setMargins(0, dp(30), dp(8), 0);
        serverBtn.setLayoutParams(btnLp);
        serverBtn.setOnClickListener(v -> reopen());
        serverBtn.setZ(1f);

        shell.addView(wv);
        shell.addView(serverBtn);
        wv.loadUrl(url);
    }

    private void reopen() {
        shell.removeView(wv);
        wv.destroy();
        wv = null;
        buildSetup();
        setContentView(setup);
    }

    private void requestMic() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    @Override
    public void onBackPressed() {
        if (wv != null && wv.canGoBack()) {
            wv.goBack();
        } else {
            super.onBackPressed();
        }
    }
}