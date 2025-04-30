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

public class AfficherListeDvdsActivity extends AppCompatActivity {

    private LinearLayout listeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficher_liste_dvds);

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
}
