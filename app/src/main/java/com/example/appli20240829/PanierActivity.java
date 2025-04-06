
package com.example.appli20240829;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PanierActivity extends AppCompatActivity {

    private LinearLayout layoutPanier;
    private HashMap<FilmPanierItem, Integer> panier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);

        layoutPanier = findViewById(R.id.layoutPanier);
        Button boutonVider = findViewById(R.id.boutonVider);
        Button boutonValider = findViewById(R.id.boutonValider);
        Button boutonRetour = findViewById(R.id.boutonRetour);

        panier = DonneesPartagees.panierGlobal;

        afficherPanier();

        boutonVider.setOnClickListener(v -> {
            panier.clear();
            layoutPanier.removeAllViews();
            Toast.makeText(this, "Panier vidé", Toast.LENGTH_SHORT).show();
        });

        boutonRetour.setOnClickListener(v -> finish());

        boutonValider.setOnClickListener(v -> {
            if (panier.isEmpty()) {
                Toast.makeText(this, "Panier vide", Toast.LENGTH_SHORT).show();
            } else {
                validerCommande();
            }
        });
    }

    private void afficherPanier() {
        layoutPanier.removeAllViews();

        for (FilmPanierItem item : panier.keySet()) {
            int quantite = panier.get(item);

            LinearLayout ligne = new LinearLayout(this);
            ligne.setOrientation(LinearLayout.HORIZONTAL);
            ligne.setGravity(Gravity.CENTER_VERTICAL);
            ligne.setPadding(0, 16, 0, 16);

            TextView nomFilm = new TextView(this);
            nomFilm.setText(item.titre);
            nomFilm.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));

            Button btnMoins = new Button(this);
            btnMoins.setText("−");
            btnMoins.setOnClickListener(v -> {
                int q = panier.get(item);
                if (q > 1) {
                    panier.put(item, q - 1);
                } else {
                    panier.remove(item);
                }
                afficherPanier();
            });

            TextView qte = new TextView(this);
            qte.setText(" x" + quantite + " ");
            qte.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            Button btnPlus = new Button(this);
            btnPlus.setText("+");
            btnPlus.setOnClickListener(v -> {
                panier.put(item, panier.get(item) + 1);
                afficherPanier();
            });

            ligne.addView(nomFilm);
            ligne.addView(btnMoins);
            ligne.addView(qte);
            ligne.addView(btnPlus);

            layoutPanier.addView(ligne);
        }
    }

    private void validerCommande() {
        new Thread(() -> {
            try {
                for (Map.Entry<FilmPanierItem, Integer> entry : panier.entrySet()) {
                    FilmPanierItem item = entry.getKey();
                    int quantite = entry.getValue();

                    for (int i = 0; i < quantite; i++) {
                        URL url = new URL(DonneesPartagees.getURLConnexion() + "/toad/inventory/available/getById?id=" + item.filmId);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        Scanner scanner = new Scanner(connection.getInputStream());
                        String response = scanner.hasNext() ? scanner.nextLine() : "";
                        scanner.close();

                        if (!response.isEmpty()) {
                            int inventoryId = Integer.parseInt(response);
                            URL rentalUrl = new URL(DonneesPartagees.getURLConnexion() + "/toad/rental/add");
                            HttpURLConnection rentalConn = (HttpURLConnection) rentalUrl.openConnection();
                            rentalConn.setRequestMethod("POST");
                            rentalConn.setDoOutput(true);

                            String now = LocalDateTime.now().toString();
                            String postData = "rental_date=" + now +
                                    "&inventory_id=" + inventoryId +
                                    "&customer_id=" + DonneesPartagees.getCustomerId() +
                                    "&return_date=" +
                                    "&staff_id=1" +
                                    "&last_update=" + now;

                            OutputStreamWriter writer = new OutputStreamWriter(rentalConn.getOutputStream());
                            writer.write(postData);
                            writer.flush();
                            writer.close();

                            rentalConn.getInputStream().close(); // force l'exécution
                        }
                    }
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Commande validée", Toast.LENGTH_SHORT).show();
                    DonneesPartagees.panierGlobal.clear();
                    afficherPanier();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
