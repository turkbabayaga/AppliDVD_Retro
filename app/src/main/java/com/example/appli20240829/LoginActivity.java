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

    private EditText usernameField, passwordField;
    private Button loginButton;
    String[] listeURLs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //SPINNER URLs-D
        listeURLs = getResources().getStringArray(R.array.listeURLs);
        Spinner spinnerURLs=findViewById(R.id.spinnerURLs);
        spinnerURLs.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence>adapterListeURLs=ArrayAdapter.createFromResource(this, R.array.listeURLs, android.R.layout.simple_spinner_item);
        adapterListeURLs.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerURLs.setAdapter(adapterListeURLs);
        //SPINNER URLs-F

        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> {
            String email = usernameField.getText().toString();
            String password = passwordField.getText().toString();

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
                // URL mise à jour avec le bon chemin
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

                            // Rediriger vers la page principale
                            Intent intent = new Intent(this, AfficherListeDvdsActivity.class);
                            startActivity(intent);
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

    // jsp il sert a quoi ce code mais ça marche donc tranquille
    //SPINNER URLs-D
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Make toast of the name of the course which is selected in the spinner
        //Toast.makeText(getApplicationContext(), listeURLs[position], Toast.LENGTH_SHORT).show();
        DonneesPartagees.setURLConnexion(listeURLs[position]);
        Toast.makeText(getApplicationContext(), DonneesPartagees.getURLConnexion(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // No action needed when no selection is made
    }
    //SPINNER URLs-F
}
