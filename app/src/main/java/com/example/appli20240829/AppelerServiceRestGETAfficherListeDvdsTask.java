package com.example.appli20240829;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class AppelerServiceRestGETAfficherListeDvdsTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

    private final AfficherListeDvdsActivity activity;

    public AppelerServiceRestGETAfficherListeDvdsTask(AfficherListeDvdsActivity activity) {
        this.activity = activity;
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {
        ArrayList<HashMap<String, String>> listeDvds = new ArrayList<>();
        try {
            String urlString = urls[0];
            Log.d("API_DEBUG", "URL reçue : " + urlString);

            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            int responseCode = urlConnection.getResponseCode();
            Log.d("API_DEBUG", "Code HTTP : " + responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.e("API_DEBUG", "Erreur API : " + responseCode);
                return null;
            }

            InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            String result = stringBuilder.toString();
            Log.d("API_DEBUG", "Données reçues : " + result);

            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject film = jsonArray.getJSONObject(i);
                HashMap<String, String> filmData = new HashMap<>();

                filmData.put("title", film.optString("title", "Titre inconnu"));
                filmData.put("releaseYear", film.optString("releaseYear", "Année inconnue"));
                filmData.put("rentalDuration", film.optString("rentalDuration", "Durée inconnue"));
                filmData.put("description", film.optString("description", "Aucune description disponible."));
                filmData.put("specialFeatures", film.optString("specialFeatures", "Aucune fonctionnalité spéciale."));

                listeDvds.add(filmData);
            }

        } catch (Exception e) {
            Log.e("API_DEBUG", "Erreur API", e);
        }
        return listeDvds;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> listeDvds) {
        if (activity != null && listeDvds != null) {
            activity.afficherFilms(listeDvds);
        } else {
            Log.e("API_DEBUG", "Erreur : liste null ou activité non initialisée.");
        }
    }
}
