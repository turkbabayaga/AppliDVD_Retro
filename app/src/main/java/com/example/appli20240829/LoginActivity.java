package com.example.appli20240829;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
//SPINNER URLs-D
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ArrayAdapter ;
import android.widget.Toast;
//SPINNER URLs-F

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText usernameField, passwordField, edittextURL;
    private Button loginButton;
    private Spinner spinnerURLs;
    private String[] listeURLs;
    private String urlSelectionnee = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Spinner init
        listeURLs = getResources().getStringArray(R.array.listeURLs);
        spinnerURLs = findViewById(R.id.spinnerURLs);
        ArrayAdapter<CharSequence> adapterListeURLs = ArrayAdapter.createFromResource(
                this, R.array.listeURLs, android.R.layout.simple_spinner_item
        );
        adapterListeURLs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerURLs.setAdapter(adapterListeURLs);
        spinnerURLs.setOnItemSelectedListener(this);

        // Champs
        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        edittextURL = findViewById(R.id.URLText);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> {
            String email = usernameField.getText().toString();
            String password = passwordField.getText().toString();
            String urlManuelle = edittextURL.getText().toString().trim();

            // Décision : champ manuel prioritaire, sinon Spinner
            String urlFinale = urlManuelle.isEmpty() ? urlSelectionnee : urlManuelle;
            DonneesPartagees.setURLConnexion(urlFinale);
            Toast.makeText(this, "Connexion à : " + urlFinale, Toast.LENGTH_SHORT).show();

            if (!email.isEmpty() && !password.isEmpty()) {
                login(email, password);
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(String email, String password) {
        new Thread(() -> {
            try {
                String apiUrl = DonneesPartagees.getURLConnexion() + "/toad/customer/getByEmail?email=" + email;
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONObject userJson = new JSONObject(response.toString());
                    String passwordFromApi = userJson.getString("password");

                    runOnUiThread(() -> {
                        if (password.equals(passwordFromApi)) {
                            Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, AfficherListeDvdsActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Mot de passe incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Utilisateur non trouvé.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e("LOGIN", "Erreur : ", e);
                runOnUiThread(() -> Toast.makeText(this, "Erreur de connexion au serveur.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        urlSelectionnee = listeURLs[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        urlSelectionnee = ""; // par défaut
    }
}
