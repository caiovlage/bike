package com.vaidebike.service;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class LocationPointsService {

    public static LatLng[] getPlaces() {

        LatLng[] localizacoesList;
        try
        {
            LocationJson jsonParser = new LocationJson();
            JSONObject response = jsonParser.makeHttpRequest("http://www.gruposolarbrasil.com.br/json/localizacoes","GET",null);
            JSONArray localizacoes =  response.getJSONArray("localizacoes");
            localizacoesList  = new LatLng[localizacoes.length()];
            for (int i = 0; i<localizacoes.length();i++){
                JSONObject localizacao = localizacoes.getJSONObject(i);
                localizacoesList[i] = new LatLng(localizacao.getDouble("latitude"), localizacao.getDouble("longitude") );
            }
            if(localizacoes.length() < 1)
            {
               return null;
            }
           return localizacoesList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}