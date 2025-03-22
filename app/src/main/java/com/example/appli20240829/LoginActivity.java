package com.example.appli20240829;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisation des vues
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        // Ouverture de la base de données existante
        try {
            database = openOrCreateDatabase("peach.db", MODE_PRIVATE, null);
            Log.d("LoginActivity", "Base de données ouverte avec succès.");
        } catch (Exception e) {
            Log.e("LoginActivity", "Erreur lors de l'ouverture de la base de données : " + e.getMessage());
            Toast.makeText(this, "Erreur avec la base de données", Toast.LENGTH_LONG).show();
        }

        // Gestion du clic sur le bouton de login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                } else if (checkUser(username, password)) {
                    // Si les informations sont correctes, passer à l'activité suivante
                    Intent intent = new Intent(LoginActivity.this, AfficherListeDvdsActivity.class);
                    startActivity(intent);
                    finish(); // Terminer l'activité de login pour ne pas pouvoir revenir
                } else {
                    Toast.makeText(LoginActivity.this, "Nom d'utilisateur ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Vérifie si l'utilisateur existe dans la base de données
    private boolean checkUser(String username, String password) {
        boolean exists = false;
        try {
            String query = "SELECT * FROM user WHERE username = ? AND password = ?";
            Cursor cursor = database.rawQuery(query, new String[]{username, password});
            exists = cursor.getCount() > 0;
            cursor.close();
        } catch (Exception e) {
            Log.e("LoginActivity", "Erreur lors de la vérification de l'utilisateur : " + e.getMessage());
        }
        return exists;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
            Log.d("LoginActivity", "Base de données fermée.");
        }
    }
}
