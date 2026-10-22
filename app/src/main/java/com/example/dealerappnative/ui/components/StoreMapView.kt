package com.example.dealerappnative.ui.components

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.dealerappnative.model.StoreLocation
import com.example.dealerappnative.model.StoreRoute
import com.example.dealerappnative.data.DealerMapDefaults

private const val assetHost = "appassets.androidplatform.net"

/** The vector basemap is isolated here; address and editing controls are native Compose. */
@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun StoreMapView(
    location: StoreLocation,
    editing: Boolean,
    recenterRequest: Int,
    route: StoreRoute?,
    cameraTarget: StoreLocation?,
    cameraRequest: Int,
    onPositionChange: (Double, Double) -> Unit,
    onTileStatus: (Boolean) -> Unit,
    onMapMovingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    bottomContentInset: Dp = 0.dp
) {
    val currentBottomInset = rememberUpdatedState(bottomContentInset)
    val currentLocation = rememberUpdatedState(location)
    val currentEditing = rememberUpdatedState(editing)
    val currentRecenter = rememberUpdatedState(recenterRequest)
    val currentRoute = rememberUpdatedState(route)
    val currentCamera = rememberUpdatedState(cameraTarget)
    val currentCameraRequest = rememberUpdatedState(cameraRequest)
    val positionCallback = rememberUpdatedState(onPositionChange)
    val tileCallback = rememberUpdatedState(onTileStatus)
    val movingCallback = rememberUpdatedState(onMapMovingChange)

    fun configure(
        view: WebView,
        point: StoreLocation = currentLocation.value,
        edit: Boolean = currentEditing.value,
        recenter: Int = currentRecenter.value,
        routeValue: StoreRoute? = currentRoute.value,
        camera: StoreLocation? = currentCamera.value,
        cameraToken: Int = currentCameraRequest.value
    ) {
        val state = view.tag as? MapViewState ?: return // Wait for the bundled map script.
        val inset = currentBottomInset.value.value
        if (state.bottomInset != inset) {
            state.bottomInset = inset
            view.evaluateJavascript("window.setStoreSheetInset($inset);", null)
        }
        val cssHeight = view.height / view.resources.displayMetrics.density
        view.evaluateJavascript("window.resizeStoreMap($cssHeight);", null)
        val config = "${point.latitude},${point.longitude},${edit},${recenter}"
        if (state.config != config) {
            state.config = config
            view.evaluateJavascript(
                "window.configureStoreMap(${point.latitude},${point.longitude},${edit},${recenter});", null
            )
        }
        val geometry = routeValue?.geometryJson
        val routeConfig = "${point.latitude},${point.longitude}:$geometry:$inset"
        if (state.routeConfig != routeConfig) {
            state.routeConfig = routeConfig
            view.evaluateJavascript("window.updateStoreRoute(${point.latitude},${point.longitude},${geometry ?: "null"});", null)
        }
        if (edit && camera != null && state.cameraRequest != cameraToken) {
            state.cameraRequest = cameraToken
            view.evaluateJavascript("window.focusStorePoint(${camera.latitude},${camera.longitude});", null)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                WebView.setWebContentsDebuggingEnabled((context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0)
                settings.javaScriptEnabled = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> configure(this) }
                settings.allowFileAccess = false
                settings.allowContentAccess = false
                settings.userAgentString = "DealerUiPrototype/1.0 (com.example.dealerappnative)"
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                addJavascriptInterface(MapBridge(
                    onPositionChange = { lat, lng -> positionCallback.value(lat, lng) },
                    onTileStatus = { available -> tileCallback.value(available) },
                    onMapMovingChange = { moving -> movingCallback.value(moving) },
                    onReady = { post { tag = MapViewState(); configure(this) } }
                ), "StoreMap")
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(message: ConsoleMessage): Boolean {
                        Log.d("StoreMap", message.message())
                        return true
                    }
                }
                webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                        if (request.url.host != assetHost) return null
                        val path = request.url.path.orEmpty().removePrefix("/")
                        if (!path.startsWith("map/") || ".." in path) return WebResourceResponse(null, null, null)
                        val mime = when (path.substringAfterLast('.')) {
                            "html" -> "text/html"
                            "js" -> "application/javascript"
                            "css" -> "text/css"
                            "png" -> "image/png"
                            "svg" -> "image/svg+xml"
                            else -> "text/plain"
                        }
                        return WebResourceResponse(mime, "UTF-8", context.assets.open(path))
                    }

                    // Remote pages must never gain access to the local editing bridge.
                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) = true

                }
                // Bundle the code inline so map setup never waits on a remote script or stylesheet.
                val html = context.assets.open("map/index.html").bufferedReader().use { it.readText() }
                    .replace("<link rel=\"stylesheet\" href=\"maplibre-gl.css\">",
                        "<style>" + context.assets.open("map/maplibre-gl.css").bufferedReader().use { it.readText() } + "</style>")
                    .replace("<script src=\"maplibre-gl.js\"></script>",
                        "<script>" + context.assets.open("map/maplibre-gl.js").bufferedReader().use { it.readText() } + "</script>")
                val styledHtml = html.replace("__BASEMAP_STYLE__",
                    context.assets.open("map/bright-style.json").bufferedReader().use { it.readText() })
                    .replace("__DEALER_POSITION__", "[${DealerMapDefaults.longitude},${DealerMapDefaults.latitude}]")
                loadDataWithBaseURL("https://$assetHost/map/", styledHtml, "text/html", "UTF-8", null)
            }
        },
        // Capture the changing inputs even before the asynchronous HTML ready callback.
        update = { configure(it, location, editing, recenterRequest, route, cameraTarget, cameraRequest) },
        onRelease = { it.removeJavascriptInterface("StoreMap"); it.destroy() }
    )
}

private class MapBridge(
    private val onPositionChange: (Double, Double) -> Unit,
    private val onTileStatus: (Boolean) -> Unit,
    private val onMapMovingChange: (Boolean) -> Unit,
    private val onReady: () -> Unit
) {
    private val main = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun ready() { main.post { onReady() } }

    @JavascriptInterface
    fun positionChanged(latitude: Double, longitude: Double) {
        if (latitude.isFinite() && longitude.isFinite() && latitude in -85.0..85.0 && longitude in -180.0..180.0) {
            main.post { onPositionChange(latitude, longitude) }
        }
    }

    @JavascriptInterface
    fun mapMoving(moving: Boolean) { main.post { onMapMovingChange(moving) } }

    @JavascriptInterface
    fun tileStatus(available: Boolean) { main.post { onTileStatus(available) } }
}

private class MapViewState(var config: String = "", var routeConfig: String = "", var cameraRequest: Int = -1, var bottomInset: Float = -1f)
