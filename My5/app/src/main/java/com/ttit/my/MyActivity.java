package com.ttit.my;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView=new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(mClient);
        webView.addJavascriptInterface(new MyJavaScriptInterface(),
                "BRIDGE");
        setContentView(webView);
        webView.loadUrl("file:///android_asset/form.html");
        }
        private static final String JS_SETELEMENT="javascript:document." +
                "getElementById('%s').value='%s'";
        private static final String JS_GETELEMENT =
            "javascript:window.BRIDGE.storeElement('%s',document.getElementById('%s').value)";
        private static final String ELEMENTID = "emailAddress";
        private WebViewClient mClient=new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                executeJavascript(view,String.format(JS_GETELEMENT, ELEMENTID, ELEMENTID));
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                SharedPreferences prefs=getPreferences(Activity.MODE_PRIVATE);
                executeJavascript(view,String.format(JS_SETELEMENT, ELEMENTID, prefs.getString(ELEMENTID, ""))) ;
                
            }
        };

    private void executeJavascript(WebView view, String script) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            view.evaluateJavascript(script,null);
        }else{
            view.loadUrl(script);
        }
    }
    private class MyJavaScriptInterface{
        @JavascriptInterface
        public void storeElement(String id,String element){
            SharedPreferences.Editor edit=getPreferences(Activity.MODE_PRIVATE).edit();
            edit.putString(id,element);
            edit.commit();
            if(!TextUtils.isEmpty(element)){
                Toast.makeText(MyActivity.this, element, Toast.LENGTH_SHORT).show();
            }

        }
    }

}