package com.example.aidlapplication

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.KeyEvent
import android.webkit.*
import androidx.core.view.MotionEventCompat

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    private var mserv: IITArchAidlInterface? = null

    private val mconn = object: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            if(p1==null) return
            mserv = IITArchAidlInterface.Stub.asInterface(p1)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mserv = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView = findViewById(R.id.target_webview)

        val intent = Intent(this,ITArchAIDLService::class.java)
        bindService(intent,mconn, Context.BIND_AUTO_CREATE)

        webView.webViewClient = object: WebViewClient(){
            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                if(mserv==null) {
                    Log.d("DEBUG", "called error")
                    return
                }
                if(errorResponse==null){
                    Log.d("DEBUG","response not exist")
                    return
                }
                val url = mserv!!.calcStatusURL(errorResponse.statusCode)
                val html = "<img style='object-fit: scale-down;' src='${url}'>"
                view?.loadDataWithBaseURL(null,html,"text/html","UTF8",null)
            }
        }

        webView.settings.allowContentAccess = true
        webView.settings.allowFileAccess = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.loadUrl("http://10.0.2.2:1234/")

        // TODO:
        // 2. デモ用にサーバを立てておきたい
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            if(webView.canGoBack()) webView.goBack()
            else webView.loadUrl("http://10.0.2.2:1234")
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}