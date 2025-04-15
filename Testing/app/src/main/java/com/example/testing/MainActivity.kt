package com.example.testing

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testing.ui.theme.TestingTheme
import com.example.testing.ui.webview.WebViewViewModel
import android.content.Context

class MainActivity : ComponentActivity() {

    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TestingTheme {
                val viewModel: WebViewViewModel = viewModel() // ✅ Called inside Composable

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WebViewScreen(
                        url = "https://www.ghibli.jp/works/",
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    ) { webViewInstance ->
                        webView = webViewInstance // Save reference for saveState()
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView?.let { webView ->
            val stateBundle = Bundle()
            webView.saveState(stateBundle)
            (webView.context as? ComponentActivity)?.let {
                val vm: WebViewViewModel = androidx.lifecycle.ViewModelProvider(it)[WebViewViewModel::class.java]
                vm.webViewBundle = stateBundle
            }
        }
    }
}

@Composable
fun WebViewScreen(
    url: String,
    viewModel: WebViewViewModel,
    modifier: Modifier = Modifier,
    onWebViewReady: (WebView) -> Unit
) {
    AndroidView(
        factory = { context: Context ->
            WebView(context).apply {
                onWebViewReady(this)

                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.allowContentAccess = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

                if (viewModel.webViewBundle == null) {
                    loadUrl(url)
                } else {
                    restoreState(viewModel.webViewBundle!!)
                }
            }
        },
        modifier = modifier.fillMaxSize()
    )
}