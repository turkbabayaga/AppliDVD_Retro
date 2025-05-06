package com.example.appli20240829;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class AfficherListeDvdsActivity extends AppCompatActivity {
    // DSA Début - Déclarations pour filtre catégorie
    private Spinner spinnerCategories;
    private Button btnFiltrerCategorie;
    private ArrayList<HashMap<String, String>> listeFilmsOriginale = new ArrayList<>();
    // DSA Fin


    private LinearLayout listeContainer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficher_liste_dvds);
        // DSA Début - Init spinner + bouton filtre
        spinnerCategories = findViewById(R.id.spinnerCategories);
        btnFiltrerCategorie = findViewById(R.id.btnFiltrerCategorie);
        chargerCategories();

        btnFiltrerCategorie.setOnClickListener(v -> {
            String selectedCategory = spinnerCategories.getSelectedItem().toString();
            ArrayList<HashMap<String, String>> filmsFiltres = new ArrayList<>();

            for (HashMap<String, String> film : listeFilmsOriginale) {
                String categoryName = film.get("category");
                if (categoryName != null && categoryName.equalsIgnoreCase(selectedCategory)) {
                    filmsFiltres.add(film);
                }
            }

            afficherFilms(filmsFiltres);
        });
        // DSA Fin


        listeContainer = findViewById(R.id.listeContainer);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button boutonPanier = findViewById(R.id.boutonPanier);
        boutonPanier.setOnClickListener(v -> {
            Intent intent = new Intent(AfficherListeDvdsActivity.this, PanierActivity.class);
            startActivity(intent);
        });

        // Appel de l’AsyncTask
        new AppelerServiceRestGETAfficherListeDvdsTask(this).execute();
    }

    // Appelé par onPostExecute() pour afficher les films
    public void afficherFilms(ArrayList<HashMap<String, String>> films) {

        // DSA Début - Sauvegarde liste complète pour filtrage
        listeFilmsOriginale = new ArrayList<>(films);
        // DSA Fin
        listeContainer.removeAllViews();

        for (HashMap<String, String> film : films) {
            LinearLayout bloc = new LinearLayout(this);
            bloc.setOrientation(LinearLayout.VERTICAL);
            bloc.setPadding(30, 30, 30, 30);
            bloc.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 20, 0, 20);
            bloc.setLayoutParams(params);

            TextView titre = new TextView(this);
            titre.setText("🎬 " + film.get("title"));
            titre.setTextSize(18);
            titre.setPadding(0, 0, 0, 10);

            TextView description = new TextView(this);
            description.setText("📝 " + film.get("description"));

            Button btnDetails = new Button(this);
            btnDetails.setText("Détails");
            btnDetails.setOnClickListener(v -> {
                Intent intent = new Intent(this, AfficherDetailDvdActivity.class);
                intent.putExtra("id", film.get("id"));
                intent.putExtra("title", film.get("title"));
                intent.putExtra("description", film.get("description"));
                intent.putExtra("releaseYear", film.get("releaseYear"));
                intent.putExtra("rentalDuration", film.get("rentalDuration"));
                intent.putExtra("specialFeatures", film.get("specialFeatures"));
                startActivity(intent);
            });

            bloc.addView(titre);
            bloc.addView(description);
            bloc.addView(btnDetails);

            listeContainer.addView(bloc);
        }
    }

    private void chargerCategories() {
        new Thread(() -> {
            try {
                URL url = new URL(DonneesPartagees.getURLConnexion() + "/toad/category/all");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                JSONArray jsonArray = new JSONArray(json.toString());
                ArrayList<String> categories = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    categories.add(obj.getString("name"));
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategories.setAdapter(adapter);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
