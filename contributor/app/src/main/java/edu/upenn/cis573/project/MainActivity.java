package edu.upenn.cis573.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    protected static Contributor contributor;
    private DataManager dataManager = new DataManager(new WebClient("10.0.2.2", 3001));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onLoginButtonClick(View view) {

        EditText loginField = findViewById(R.id.loginField);
        String login = loginField.getText().toString();

        EditText passwordField = findViewById(R.id.passwordField);
        String password = passwordField.getText().toString();

        try{
            contributor = dataManager.attemptLogin(login, password);
        }catch(IllegalArgumentException e){
            Toast.makeText(this, "Please try again with non null credentials", Toast.LENGTH_LONG).show();
            return;
        }catch(IllegalStateException e){
            Toast.makeText(this, "Error: Datamanager is in an illegal state. Please check connection to webclient + functionality", Toast.LENGTH_LONG).show();
            return;
        }


        if (contributor == null) {

            Toast.makeText(this, "Login failed!", Toast.LENGTH_LONG).show();


        } else {

            Intent i = new Intent(this, MenuActivity.class);

            startActivity(i);
        }


    }
}