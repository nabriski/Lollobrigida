package com.github.nabriski.lollobrigida;



import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONObject;

import java.lang.reflect.Method;


public abstract class AbstractBridge {

    public final static String LOG_TAG = "lollobrigida";
    public @interface BridgeMethod{}

    public interface Callback{

        public void onDone(JSONObject err, JSONObject result);
    }

    WebView wv;
    String bridgeName;
    Handler mainHandler;

    public AbstractBridge(WebView wv, String bridgeName){
        mainHandler = new Handler(wv.getContext().getMainLooper());
        wv.addJavascriptInterface(this,bridgeName);
        this.wv = wv;
        this.bridgeName = bridgeName;

    }

    @JavascriptInterface
    protected void init(){

        final AbstractBridge self = this;
        Runnable readyRunnable = new Runnable() {

            @Override
            public void run() {
                for(Method m : self.getClass().getMethods()){
                    if(m.getAnnotation(BridgeMethod.class)!=null){
                        String bindMethodURL;
                        if(m.getParameterTypes().length == 1){
                            bindMethodURL = String.format("javascript:%s['%s'] = function(cb){ var cbId = String(parseInt(Math.random()*1000000)); %s[cbId] = cb; %s.call('%s',null,cbId) };", bridgeName,m.getName(), bridgeName, bridgeName,m.getName());

                        }
                        else if(m.getParameterTypes().length == 2){
                            bindMethodURL = String.format("javascript:%s['%s'] = function(params,cb){ var cbId = String(parseInt(Math.random()*1000000)); %s[cbId] = cb; %s.call('%s',JSON.stringify(params),cbId) };", bridgeName,m.getName(), bridgeName, bridgeName,m.getName());
                        }
                        else{
                            throw new RuntimeException("bridged method must accept a JSONObject and Callback or only a Callback");
                        }
                         //Log.d("console",bindMethodURL);
                        //Log.d(LOG_TAG,bindMethodURL);
                        wv.loadUrl(bindMethodURL);
                    }
                }

                String onReadyURL = String.format("javascript:if(%s.onReady) %s.onReady();", bridgeName, bridgeName);
                wv.loadUrl(onReadyURL);
            }
        };

        mainHandler.post(readyRunnable);

    }

    @JavascriptInterface
    protected void call(String methodName,String parameters,final String cbID){

        try {

            Method m = null;
            for(Method nextM : this.getClass().getMethods()){
                if(nextM.getName().equals(methodName)){
                    m = nextM;
                    break;
                }
            }
            final Method finalM = m;

            final String finParams = parameters;
            final AbstractBridge self = this;
            Runnable runCB = new Runnable() {
                @Override
                public void run() {
                    Callback cb = new Callback() {
                        @Override
                        public void onDone(JSONObject err, JSONObject result) {
                            String errStr = "null";
                            if (err != null) errStr = "'" + err.toString() + "'";
                            String resultStr = "null";
                            if (result != null) resultStr = "'" + result.toString() + "'";

                            //   wv.loadUrl("javascript:koko['xxx']()");
                            String callbackURL = String.format("javascript:%s['%s'](JSON.parse(%s),JSON.parse(%s));", bridgeName, cbID, errStr, resultStr);
                            //Log.d(LOG_TAG, callbackURL);
                            wv.loadUrl(callbackURL);
                            String cleanURL = String.format("javascript:delete %s['%s']", bridgeName, cbID);
                            //Log.d(LOG_TAG, cleanURL);
                            wv.loadUrl(cleanURL);
                        }
                    };
                    try {
                        if(finalM.getParameterTypes().length == 1){
                            finalM.invoke(self,cb);
                        }
                        else if(finalM.getParameterTypes().length == 2){
                            finalM.invoke(self,new JSONObject(finParams),cb);
                        }
                    } catch (Exception e) {
                        Log.e(LOG_TAG,"Error",e);
                    }
                }
            };
            mainHandler.post(runCB);

        } catch (Exception e) {
            Log.e(LOG_TAG,"Error",e);
        }
    }


}
