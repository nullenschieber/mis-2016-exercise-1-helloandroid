package de.einervondenen.helloandroid_ct;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class helloandroid_ct extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.url_thread_handler);

        final Button RequestURL = (Button) findViewById(R.id.GetServerData);

        final EditText urlText = (EditText) findViewById(R.id.editText);

        urlText.setText("http://www.uni-weimar.de/");
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setVisibility(View.GONE);


        RequestURL.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                int duration = Toast.LENGTH_LONG;

                InputMethodManager imm =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                TextView text = (TextView)findViewById(R.id.editText2);
                text.setText("");

                WebView webView = (WebView) findViewById(R.id.webView);
                webView.setVisibility(View.GONE);

                text.setVisibility(View.GONE);


                Toast.makeText(getBaseContext(),
                        "Please wait, requesting URL ...",
                        duration).show();

                Toast.makeText(
                        getBaseContext(),
                        urlText.getText().toString(),
                        duration).show();

                Thread urlRQThreat = new Thread(new Runnable() {

                    private final HttpClient Client = new DefaultHttpClient();

                    public void run() {
                        try {

                            HttpGet httpget = new HttpGet(urlText.getText().toString());
                            ResponseHandler<String> responseHandler = new BasicResponseHandler();

                            threadMsg(Client.execute(httpget, responseHandler));

                        } catch (Throwable t) {
                            Log.i("Animation", "Thread  exception " + t);
                        }
                    }

                    private void threadMsg(String msg) {
                        if (!msg.equals(null) && !msg.equals("")) {
                            Message msgObj = handler.obtainMessage();
                            Bundle b = new Bundle();
                            b.putString("message", msg);
                            msgObj.setData(b);
                            handler.sendMessage(msgObj);
                        }
                    }


                    private final Handler handler = new Handler() {
                        public void handleMessage(Message msg) {

                            String aResponse = msg.getData().getString("message");
                            WebView webView = (WebView) findViewById(R.id.webView);
                            int duration = Toast.LENGTH_LONG;

                            if ((null != aResponse)) {

                                if(aResponse.contains("text/html")) {
                                    webView.setVisibility(View.VISIBLE);
                                    webView.loadUrl(RequestURL.toString());

                                }
                                else {
                                        Toast.makeText(getBaseContext(),
                                                "Server Response: " + aResponse,
                                                duration).show();


                                        TextView text = (TextView) findViewById(R.id.editText2);
                                        text.setText(aResponse);
                                    }
                            }
                            else
                            {

                                Toast.makeText(getBaseContext(),
                                        "Not Got Response From Server.",
                                        duration).show();
                            }

                        }
                    };

                });

                urlRQThreat.start();
            }
        });

    }



}
