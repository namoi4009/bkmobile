package com.example.testing

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.testing.ui.theme.TestingTheme
import android.content.Context
import android.webkit.WebSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WebViewScreen(
//                        url = "http://10.0.2.2:5000",
                        url = "https://lms.hcmut.edu.vn/",
//                        url = "https://www.babylonjs.com/",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun WebViewScreen(url: String, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context: Context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                setLayerType(WebView.LAYER_TYPE_HARDWARE, null)
                settings.javaScriptEnabled = true
                settings.allowFileAccess = true
                settings.allowContentAccess = true
                settings.domStorageEnabled = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                settings.allowUniversalAccessFromFileURLs = true
                settings.allowFileAccessFromFileURLs = true
                loadUrl("http://10.0.2.2:5000")
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        },
        modifier = modifier.fillMaxSize()
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestingTheme {
        Text("WebView App")
    }
}