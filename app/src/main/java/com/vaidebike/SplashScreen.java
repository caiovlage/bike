package com.vaidebike;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        boolean conected = isConnected(this);
        Handler handle = new Handler();

        if(conected)
        {
            handle.postDelayed(
                    new Runnable()
                    {
                        @Override public void run()
                        {
                            showMaps();
                        }
                    },
                    3000
            );
        }
        else
        {
            Toast.makeText(this, "Está Aplicação Precisa de internet para funcionar!", Toast.LENGTH_SHORT).show();
            handle.postDelayed(
                    new Runnable()
                    {
                        @Override public void run()
                        {
                            System.exit(0);
                        }
                    },
                    3000
            );

        }



    }

    private void showMaps()
    {
        Intent intent = new Intent(this, MapsActivity.class); startActivity(intent);
    }

    public static boolean isConnected(Context cont){
        ConnectivityManager conmag = (ConnectivityManager)cont.getSystemService(Context.CONNECTIVITY_SERVICE);

        if ( conmag != null ) {
            conmag.getActiveNetworkInfo();

            //Verifica internet pela WIFI
            if (conmag.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                return true;
            }

            //Verifica se tem internet móvel
            if (conmag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
                return true;
            }
        }

        return false;
    }

}