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

                // DonneesPartagees.setFilmIdForTitle (obsolète, supprimé)(filmData.get("title"), Integer.parseInt(filmData.get("id")));

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

        LinearLayout container = activity.findViewById(R.id.listeContainer);
        container.removeAllViews();

        if (films != null && !films.isEmpty()) {
            for (HashMap<String, String> film : films) {
                LinearLayout bloc = new LinearLayout(activity);
                bloc.setOrientation(LinearLayout.VERTICAL);
                bloc.setPadding(30, 30, 30, 30);
                bloc.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 20, 0, 20);
                bloc.setLayoutParams(params);

                TextView titre = new TextView(activity);
                titre.setText("🎬 " + film.get("title"));
                titre.setTextSize(18);
                titre.setPadding(0, 0, 0, 10);

                TextView description = new TextView(activity);
                description.setText("📝 " + film.get("description"));

                Button btnDetails = new Button(activity);
                btnDetails.setText("Détails");
                btnDetails.setOnClickListener(v -> {
                    Intent intent = new Intent(activity, AfficherDetailDvdActivity.class);
                    intent.putExtra("id", film.get("id"));
                    intent.putExtra("title", film.get("title"));
                    intent.putExtra("description", film.get("description"));
                    intent.putExtra("releaseYear", film.get("releaseYear"));
                    intent.putExtra("rentalDuration", film.get("rentalDuration"));
                    intent.putExtra("specialFeatures", film.get("specialFeatures"));
                    activity.startActivity(intent);
                });

                bloc.addView(titre);
                bloc.addView(description);
                bloc.addView(btnDetails);

                container.addView(bloc);
            }
        } else {
            TextView vide = new TextView(activity);
            vide.setText("Aucun film disponible.");
            vide.setGravity(Gravity.CENTER);
            container.addView(vide);
        }
    }
}
