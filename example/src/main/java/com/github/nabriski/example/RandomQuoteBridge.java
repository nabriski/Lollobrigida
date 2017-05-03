package com.github.nabriski.example;

import android.webkit.WebView;

import com.github.nabriski.lollobrigida.AbstractBridge;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nabriski on 30/04/2017.
 */

public class RandomQuoteBridge extends AbstractBridge {

    String[] quotes = {
            "LET OFF SOME STEAM, BENNET",
            "SEE YOU AT THE PARTY, RICHTER!",
            "I DON’T DO REQUESTS",
            "HERE IS SUB-ZERO… NOW, PLAIN ZERO"
    };

    public RandomQuoteBridge(WebView wv,String bridgeName){
        super(wv,bridgeName);
    }

    @BridgeMethod
    public void getQuote(Callback cb) throws JSONException{
        cb.onDone(
                null,
                new JSONObject(String.format("{\"quote\":\"%s\"}",quotes[(int)(Math.round(Math.random()*quotes.length))]))
        );

    }
}
