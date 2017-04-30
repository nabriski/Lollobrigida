# Lollobrigida
Android WebView Bridge

# Usage

## Java

```java
WebView wv = (WebView)findViewById(R.id.webview);
wv.getSettings().setJavaScriptEnabled(true);

RandomQuoteBridge bridge = new RandomQuoteBridge(wv,"quotes");
String html = getString(R.string.html);
wv.loadData(html,"text/html",null);
```

Where ```RandomQuoteBridge``` is:
```java
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
    public void getQuote(JSONObject input,Callback cb) throws JSONException{
        cb.onDone(
                null,
                new JSONObject(String.format("{\"quote\":\"%s\"}",quotes[(int)(Math.round(Math.random()*quotes.length))]))
        );

    }
}
```

## HTML
```html
<html>
   <body>
        <h1>Hello!</h1>
        <script>
            function getRandomColor() {
                var letters = '0123456789ABCDEF';
                var color = '#';
                for (var i = 0; i < 6; i++ ) {
                    color += letters[Math.floor(Math.random() * 16)];
                }
                return color;
            }
            function changeQuote(){
                quotes.getQuote({},function(err,resp){
                    document.querySelector("h1").innerHTML = resp.quote;
                    document.querySelector("h1").style.color = getRandomColor();
                });
            }
            quotes.onReady = changeQuote;
            quotes.init();
        </script>
        <a href="javascript:changeQuote()">Change Quote</a>
    </body>
</html>
```
