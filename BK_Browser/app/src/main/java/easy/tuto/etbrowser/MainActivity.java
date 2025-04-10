package easy.tuto.etbrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText urlInput;
    ImageView clearUrl;
    WebView webView;
    ProgressBar progressBar;
    ImageView webBack, webForward, webRefresh, webShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlInput = findViewById(R.id.url_input);
        clearUrl = findViewById(R.id.clear_icon);
        progressBar = findViewById(R.id.progress_bar);
        webView = findViewById(R.id.web_view);
        webBack = findViewById(R.id.web_back);
        webForward = findViewById(R.id.web_forward);
        webRefresh = findViewById(R.id.web_refresh);
        webShare = findViewById(R.id.web_share);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebViewClient(new MyWebViewClient());

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        if (savedInstanceState == null) {
            loadMyUrl("https://www.google.com");
        }

        urlInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_GO || i == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(urlInput.getWindowToken(), 0);
                loadMyUrl(urlInput.getText().toString());
                return true;
            }
            return false;
        });

        clearUrl.setOnClickListener(view -> urlInput.setText(""));

        webBack.setOnClickListener(view -> {
            if (webView.canGoBack()) webView.goBack();
        });

        webForward.setOnClickListener(view -> {
            if (webView.canGoForward()) webView.goForward();
        });

        webRefresh.setOnClickListener(view -> webView.reload());

        webShare.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, "Share via"));
        });
    }

    void loadMyUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        if (Patterns.WEB_URL.matcher(url).matches()) {
            webView.loadUrl(url);
        } else {
            webView.loadUrl("https://www.google.com/search?q=" + url);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // Save webview state to prevent reload on rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            urlInput.setText(url);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
