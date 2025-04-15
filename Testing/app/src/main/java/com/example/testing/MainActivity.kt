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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private var webView: WebView? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TestingTheme {
                val viewModel: WebViewViewModel = viewModel()
                var canGoBack by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars),
                    floatingActionButton = {
                        if (canGoBack) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                FloatingActionButton(
                                    onClick = { webView?.goBack() },
                                    modifier = Modifier.padding(24.dp),
                                    shape = CircleShape
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.arrow_back),
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    WebViewScreen(
                        url = "https://www.ghibli.jp/works/",
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding),
                        onWebViewReady = { webViewInstance ->
                            webView = webViewInstance

                            // Set WebViewClient to track navigation changes
                            webViewInstance.webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    canGoBack = webViewInstance.canGoBack()
                                }
                            }

                            // Initial check
                            canGoBack = webViewInstance.canGoBack()
                        }
                    )
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
