package com.example.appli20240829;

import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class AppelerServiceRestGETAfficherListeDvdsTask extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {

    private AfficherListeDvdsActivity activity;

    public AppelerServiceRestGETAfficherListeDvdsTask(AfficherListeDvdsActivity activity) {
        this.activity = activity;
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(Void... voids) {
        ArrayList<HashMap<String, String>> listeFilms = new ArrayList<>();
        try {
            String urlApi = DonneesPartagees.getURLConnexion() + "/toad/film/all";
            URL url = new URL(urlApi);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();

            JSONArray jsonArray = new JSONArray(json.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject film = jsonArray.getJSONObject(i);

                HashMap<String, String> filmData = new HashMap<>();
                filmData.put("id", String.valueOf(film.getInt("filmId")));
                filmData.put("title", film.getString("title"));
                filmData.put("description", film.getString("description"));
                filmData.put("releaseYear", String.valueOf(film.getInt("releaseYear")));
                filmData.put("rentalDuration", String.valueOf(film.getInt("rentalDuration")));
                filmData.put("specialFeatures", film.getString("specialFeatures"));

                // DSA Début - récupération de la catégorie
                JSONObject categoryObj = film.optJSONObject("category");
                if (categoryObj != null) {
                    filmData.put("category", categoryObj.optString("name", ""));
                } else {
                    filmData.put("category", "");
                }
                Log.d("FILM_CATEGORY", "Film: " + film.getString("title") + " / Catégorie: " + filmData.get("category"));
                // DSA Fin

                listeFilms.add(filmData);
            }

        } catch (Exception e) {
            Log.e("API_FILMS", "Erreur : " + e.getMessage(), e);
        }

        return listeFilms;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> films) {
        super.onPostExecute(films);

        if (films != null && !films.isEmpty()) {
            // DSA Début - remplissage centralisé + affichage visuel
            activity.afficherFilms(films);
            // DSA Fin
        } else {
            LinearLayout container = activity.findViewById(R.id.listeContainer);
            container.removeAllViews();

            TextView vide = new TextView(activity);
            vide.setText("Aucun film disponible.");
            vide.setGravity(Gravity.CENTER);
            container.addView(vide);
        }
    }
}
