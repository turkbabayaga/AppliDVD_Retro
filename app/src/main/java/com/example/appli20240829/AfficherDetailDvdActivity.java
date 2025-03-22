package com.example.appli20240829;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class AfficherDetailDvdActivity extends AppCompatActivity {

    private TextView titreDvd, anneeSortieDvd, dureeLocationDvd, descriptionDvd, specialFeaturesDvd;
    private Button boutonRetour, boutonAjouterPanier;
    private HashMap<String, Integer> panier = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficher_detail_dvd);

        // Initialisation des vues
        titreDvd = findViewById(R.id.titreDvd);
        anneeSortieDvd = findViewById(R.id.anneeSortieDvd);
        dureeLocationDvd = findViewById(R.id.dureeLocationDvd);
        descriptionDvd = findViewById(R.id.descriptionDvd);
        specialFeaturesDvd = findViewById(R.id.specialFeaturesDvd);
        boutonRetour = findViewById(R.id.boutonRetour);
        boutonAjouterPanier = findViewById(R.id.boutonAjouterPanier);

        // Récupérer les données du film passées par l'intent
        Intent intent = getIntent();
        String titre = intent.getStringExtra("title");
        String releaseYear = intent.getStringExtra("releaseYear");
        String rentalDuration = intent.getStringExtra("rentalDuration");
        String description = intent.getStringExtra("description");
        String specialFeatures = intent.getStringExtra("specialFeatures");

        // Afficher les détails du film
        titreDvd.setText(titre);
        anneeSortieDvd.setText("Année : " + releaseYear);
        dureeLocationDvd.setText("Durée : " + rentalDuration + " jours");
        descriptionDvd.setText("Description : " + description);
        specialFeaturesDvd.setText("Fonctionnalités spéciales : " + specialFeatures);

        // Récupérer le panier passé par l'intent (s'il existe)
        ArrayList<String> panierStr = intent.getStringArrayListExtra("panier");
        if (panierStr != null) {
            for (String item : panierStr) {
                String[] parts = item.split(";");
                if (parts.length == 2) {
                    panier.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
        }

        // Gestion du clic sur le bouton "Ajouter au panier"
        boutonAjouterPanier.setOnClickListener(v -> {
            // Ajouter le film au panier
            panier.put(titre, panier.getOrDefault(titre, 0) + 1);
            Toast.makeText(this, "Ajouté au panier !", Toast.LENGTH_SHORT).show();
        });

        // Gestion du clic sur le bouton "Retour"
        boutonRetour.setOnClickListener(v -> {
            // Convertir le panier en une liste de chaînes pour le passer via un Intent
            ArrayList<String> panierStrUpdated = new ArrayList<>();
            for (String filmTitre : panier.keySet()) {
                panierStrUpdated.add(filmTitre + ";" + panier.get(filmTitre));
            }

            // Renvoyer le panier à l'activité précédente
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("panier", panierStrUpdated);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}