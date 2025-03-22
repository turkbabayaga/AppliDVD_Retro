package com.example.appli20240829;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PanierActivity extends AppCompatActivity {
    private LinearLayout panierContainer;
    private HashMap<String, Integer> panier = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);

        panierContainer = findViewById(R.id.panierContainer);
        Button btnViderPanier = findViewById(R.id.btnViderPanier);
        Button btnRetour = findViewById(R.id.btnRetour);

        ArrayList<String> panierStr = getIntent().getStringArrayListExtra("panier");
        if (panierStr != null) {
            for (String item : panierStr) {
                String[] parts = item.split(";");
                if (parts.length == 2) {
                    try {
                        String titre = parts[0];
                        int quantite = Integer.parseInt(parts[1]);
                        panier.put(titre, quantite);
                    } catch (NumberFormatException e) {
                        Log.e("PanierActivity", "Quantité non valide pour l'élément : " + item);
                    }
                } else {
                    Log.e("PanierActivity", "Format incorrect pour l'élément : " + item);
                }
            }
        }

        afficherPanier();

        btnViderPanier.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous vraiment vider votre panier ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        panier.clear();
                        afficherPanier();
                        Toast.makeText(this, "Panier vidé !", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });

        btnRetour.setOnClickListener(v -> {
            ArrayList<String> panierStrUpdated = new ArrayList<>();
            for (String titre : panier.keySet()) {
                panierStrUpdated.add(titre + ";" + panier.get(titre));
            }
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra("panier", panierStrUpdated);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
        Button btnValiderPanier = findViewById(R.id.btnValiderPanier);
        btnValiderPanier.setOnClickListener(v -> validerPanier());
    }

    private void afficherPanier() {
        panierContainer.removeAllViews();

        if (panier.isEmpty()) {
            TextView txtVide = new TextView(this);
            txtVide.setText("Votre panier est vide.");
            txtVide.setTextSize(18);
            txtVide.setPadding(20, 20, 20, 20);
            txtVide.setTextColor(Color.GRAY);
            txtVide.setGravity(Gravity.CENTER);
            panierContainer.addView(txtVide);
            return;
        }

        for (String titre : panier.keySet()) {
            int quantite = panier.get(titre);

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(20, 20, 20, 20);
            card.setBackgroundColor(Color.LTGRAY);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 10, 10, 10);
            card.setLayoutParams(params);

            TextView txtFilm = new TextView(this);
            txtFilm.setText(titre + " (x" + quantite + ")");
            txtFilm.setTextSize(18);
            txtFilm.setPadding(8, 8, 8, 8);
            card.addView(txtFilm);

            LinearLayout layoutBoutons = new LinearLayout(this);
            layoutBoutons.setOrientation(LinearLayout.HORIZONTAL);
            layoutBoutons.setGravity(Gravity.CENTER);

            Button btnMoins = new Button(this);
            btnMoins.setText("-");
            btnMoins.setTextSize(20);
            btnMoins.setOnClickListener(v -> {
                if (quantite > 1) {
                    panier.put(titre, quantite - 1);
                } else {
                    panier.remove(titre);
                }
                afficherPanier();
            });

            Button btnPlus = new Button(this);
            btnPlus.setText("+");
            btnPlus.setTextSize(20);
            btnPlus.setOnClickListener(v -> {
                panier.put(titre, panier.getOrDefault(titre, 0) + 1);
                afficherPanier();
            });

            layoutBoutons.addView(btnMoins);
            layoutBoutons.addView(btnPlus);
            card.addView(layoutBoutons);

            panierContainer.addView(card);
        }
    }
    private void validerPanier() {
        if (panier.isEmpty()) {
            Toast.makeText(this, "Votre panier est vide !", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                // Format de date pour la base de données
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                for (String titre : panier.keySet()) {
                    int inventoryId = 1; // À récupérer dynamiquement selon la BDD
                    int customerId = 1; // À récupérer dynamiquement selon l'utilisateur connecté
                    int staffId = 1; // L’ID du staff qui enregistre la location

                    // Générer la date de location (date actuelle)
                    Date currentDate = new Date();
                    String rentalDate = dateFormat.format(currentDate);

                    // Calculer la date de retour (1 jour après la date de location)
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentDate);
                    calendar.add(Calendar.DAY_OF_MONTH, 1); // Ajouter 1 jour
                    String returnDate = dateFormat.format(calendar.getTime());

                    // Générer la date de dernière mise à jour (date actuelle)
                    String lastUpdate = dateFormat.format(currentDate);

                    // Construire l'URL et les paramètres
                    String apiUrl = "http://10.0.2.2:8080/toad/rental/add";
                    String postData = "rental_date=" + rentalDate
                            + "&inventory_id=" + inventoryId
                            + "&customer_id=" + customerId
                            + "&return_date=" + returnDate
                            + "&staff_id=" + staffId
                            + "&last_update=" + lastUpdate;

                    // Envoyer la requête POST
                    boolean success = sendPostRequest(apiUrl, postData);

                    if (success) {
                        Log.d("VALIDATION", "Location enregistrée pour : " + titre);
                    } else {
                        Log.e("VALIDATION", "Erreur lors de l'enregistrement de " + titre);
                    }
                }

                // Mise à jour UI après validation
                runOnUiThread(() -> {
                    panier.clear();
                    afficherPanier();
                    Toast.makeText(this, "Commande validée avec succès !", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                Log.e("VALIDATION", "Erreur lors de l'envoi de la requête", e);
            }
        }).start();
    }

    private boolean sendPostRequest(String apiUrl, String postData) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // Envoyer les données
            OutputStream os = conn.getOutputStream();
            os.write(postData.getBytes("UTF-8"));
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            Log.d("API_RESPONSE", "Code réponse : " + responseCode);

            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            Log.e("API_ERROR", "Erreur lors de la requête API", e);
            return false;
        }
    }

}
