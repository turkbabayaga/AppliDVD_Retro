package com.example.appli20240829;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText usernameField, passwordField;
    private Button loginButton;
    String[] listeURLs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        listeURLs = getResources().getStringArray(R.array.listeURLs);
        Spinner spinnerURLs = findViewById(R.id.spinnerURLs);
        spinnerURLs.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapterListeURLs = ArrayAdapter.createFromResource(this, R.array.listeURLs, android.R.layout.simple_spinner_item);
        adapterListeURLs.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerURLs.setAdapter(adapterListeURLs);

        usernameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> {
            String email = usernameField.getText().toString();
            String password = passwordField.getText().toString();

            EditText edittextURL = findViewById(R.id.URLText);
            DonneesPartagees.setURLConnexion(edittextURL.getText().toString());

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

                    // ✅ Récupération et stockage du customerId
                    int idFromApi = userJson.getInt("customerId");
                    DonneesPartagees.setCustomerId(idFromApi);
                    Log.d("LOGIN", "Customer ID = " + idFromApi);

                    runOnUiThread(() -> {
                        if (password.equals(passwordFromApi)) {
                            Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
        EditText URLText = findViewById(R.id.URLText);
        URLText.setText(listeURLs[position]);
        DonneesPartagees.setURLConnexion(listeURLs[position]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}
