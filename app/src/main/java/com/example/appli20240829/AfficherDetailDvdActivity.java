
package com.example.appli20240829;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AfficherDetailDvdActivity extends AppCompatActivity {

    private int filmId;
    private String titre;
    private Button boutonAjouter;
    private TextView dispoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficher_detail_dvd);

        TextView titreView = findViewById(R.id.titreDvd);
        TextView anneeSortieView = findViewById(R.id.anneeSortieDvd);
        TextView dureeLocationView = findViewById(R.id.dureeLocationDvd);
        TextView descriptionView = findViewById(R.id.descriptionDvd);
        TextView specialFeaturesView = findViewById(R.id.specialFeaturesDvd);
        dispoText = findViewById(R.id.dispoText); // Nouveau champ à afficher
        boutonAjouter = findViewById(R.id.boutonAjouterPanier);
        Button boutonRetour = findViewById(R.id.boutonRetour);

        filmId = Integer.parseInt(getIntent().getStringExtra("id"));
        titre = getIntent().getStringExtra("title");

        titreView.setText(titre);
        anneeSortieView.setText("Année de sortie : " + getIntent().getStringExtra("releaseYear"));
        dureeLocationView.setText("Durée : " + getIntent().getStringExtra("rentalDuration") + " jours");
        descriptionView.setText(getIntent().getStringExtra("description"));
        specialFeaturesView.setText("Fonctionnalités : " + getIntent().getStringExtra("specialFeatures"));

        boutonAjouter.setOnClickListener(v -> {
            FilmPanierItem item = new FilmPanierItem(filmId, titre);
            int quantite = DonneesPartagees.panierGlobal.getOrDefault(item, 0);
            DonneesPartagees.panierGlobal.put(item, quantite + 1);
            Toast.makeText(this, "Ajouté au panier", Toast.LENGTH_SHORT).show();
        });

        boutonRetour.setOnClickListener(v -> finish());

        verifierDisponibilite(filmId);
    }

    private void verifierDisponibilite(int id) {
        new Thread(() -> {
            try {
                String urlString = DonneesPartagees.getURLConnexion() + "/toad/inventory/available/getById?id=" + id;
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = in.readLine();
                in.close();

                runOnUiThread(() -> {
                    if (response != null && !response.trim().isEmpty()) {
                        dispoText.setText("✅ Disponible");
                        boutonAjouter.setEnabled(true);
                    } else {
                        dispoText.setText("❌ Non disponible");
                        boutonAjouter.setEnabled(false);
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    dispoText.setText("❌ Erreur de connexion");
                    boutonAjouter.setEnabled(false);
                });
            }
        }).start();
    }
}
