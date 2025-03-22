package com.example.appli20240829;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;

public class AfficherListeDvdsActivity extends AppCompatActivity {

    private LinearLayout listeContainer;
    private Button btnPanier;
    private HashMap<String, Integer> panier = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficher_liste_dvds);

        listeContainer = findViewById(R.id.listeContainer);
        btnPanier = findViewById(R.id.btnPanier);

        btnPanier.setOnClickListener(v -> ouvrirPanier());

        // Appeler l'API pour récupérer la liste des films
        new AppelerServiceRestGETAfficherListeDvdsTask(this).execute("http://10.0.2.2:8080/toad/film/all");
    }

    public void afficherFilms(ArrayList<HashMap<String, String>> filmList) {
        listeContainer.removeAllViews();

        for (HashMap<String, String> film : filmList) {
            String titreFilm = film.get("title");

            // Création d'une carte pour afficher le film
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(20, 20, 20, 20);

            TextView txtFilm = new TextView(this);
            txtFilm.setText(titreFilm + " (" + film.get("releaseYear") + ")");
            txtFilm.setTextSize(18);
            txtFilm.setPadding(8, 8, 8, 8);
            card.addView(txtFilm);

            // Bouton "Détail"
            Button boutonDetail = new Button(this);
            boutonDetail.setText("Détail");
            boutonDetail.setOnClickListener(v -> afficherDetails(film));
            card.addView(boutonDetail);

            listeContainer.addView(card);
        }
    }

    private void afficherDetails(HashMap<String, String> film) {
        Intent intent = new Intent(this, AfficherDetailDvdActivity.class);
        intent.putExtra("title", film.get("title"));
        intent.putExtra("releaseYear", film.get("releaseYear"));
        intent.putExtra("rentalDuration", film.get("rentalDuration"));
        intent.putExtra("description", film.get("description"));
        intent.putExtra("specialFeatures", film.get("specialFeatures"));

        // Passer le panier actuel à AfficherDetailDvdActivity
        ArrayList<String> panierStr = new ArrayList<>();
        for (String titre : panier.keySet()) {
            panierStr.add(titre + ";" + panier.get(titre));
        }
        intent.putStringArrayListExtra("panier", panierStr);

        // Lancer AfficherDetailDvdActivity avec un résultat attendu
        startActivityForResult(intent, 1); // 1 est un code de requête arbitraire
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Récupérer le panier mis à jour depuis AfficherDetailDvdActivity
            ArrayList<String> panierStr = data.getStringArrayListExtra("panier");
            if (panierStr != null) {
                panier.clear(); // Vider le panier actuel
                for (String item : panierStr) {
                    String[] parts = item.split(";");
                    if (parts.length == 2) {
                        panier.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            }
        }
    }

    private void ouvrirPanier() {
        if (panier.isEmpty()) {
            Toast.makeText(this, "Votre panier est vide.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir le panier en une liste de chaînes
        ArrayList<String> panierStr = new ArrayList<>();
        for (String titre : panier.keySet()) {
            panierStr.add(titre + ";" + panier.get(titre));
        }

        // Ouvrir PanierActivity avec le panier
        Intent intent = new Intent(this, PanierActivity.class);
        intent.putStringArrayListExtra("panier", panierStr);
        startActivity(intent);
    }
}