package com.sandro.cryptoapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CryptoConverterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crypto_converter);

        final EditText amount = (EditText) findViewById(R.id.amount);
        final EditText originCurrency = (EditText) findViewById(R.id.origin_currency);
        final EditText destinationCurrency = (EditText) findViewById(R.id.destination_currency);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Button convert = (Button) findViewById(R.id.button);
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client = new OkHttpClient();

                progressBar.setVisibility(View.VISIBLE);

                Request request = new Request.Builder()
                        .url(String.format("https://api.uphold.com/v0/ticker/%s%s", originCurrency.getText(), destinationCurrency.getText()))
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        CryptoConverterActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);

                                Toast.makeText(CryptoConverterActivity.this, "Error getting the result", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            CryptoConverterActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);

                                    Toast.makeText(CryptoConverterActivity.this, "Error getting the result", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        // Read data on the worker thread
                        try {
                            String responseData = response.body().string();
                            JSONObject json = new JSONObject(responseData);
                            final String ask = json.getString("ask");

                            // Run view-related code back on the main thread
                            CryptoConverterActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView myTextView = (TextView) findViewById(R.id.value);

                                    progressBar.setVisibility(View.GONE);

                                    myTextView.setText(String.format("%f", Double.parseDouble(ask) * Double.parseDouble(amount.getText().toString())) );
                                }
                            });

                        } catch (JSONException e) {
                            CryptoConverterActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);

                                    Toast.makeText(CryptoConverterActivity.this, "Error getting the result", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });

            }
        });
    }

}

