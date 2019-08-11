package com.vaidebike;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

public class HelpActivity extends AppCompatActivity {

    private HelpActivity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ImageButton back = (ImageButton)findViewById(R.id.back);
        ImageButton call = (ImageButton)findViewById(R.id.call);

        // CONFIGURA O ONCLICK NO BOTAO DE VOLTAR
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // CONFIGURA O ONCLICK NO BOTAO DE VOLTAR
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telefone = "2124430591";
                Uri uri = Uri.parse("tel:"+telefone);
                Intent intent = new Intent(Intent.ACTION_DIAL,uri);
                startActivity(intent);
            }
        });
    }
}
