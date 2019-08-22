package com.vaidebike.service;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PhoneService {

    public static String getPhone() {

        String phone = "";
        try {
            LocationJson jsonParser = new LocationJson();
            JSONObject response = jsonParser.makeHttpRequest("http://gruposolarbrasil.com.br/json/telefone", "GET", null);
            JSONObject telefone = response.getJSONObject("telefone");
            if (telefone.length() > 0) {
                phone = telefone.getString("number");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return phone;
    }
}