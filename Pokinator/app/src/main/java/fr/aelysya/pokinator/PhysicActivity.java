package fr.aelysya.pokinator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PhysicActivity extends AppCompatActivity {

    private Toast currentToast;
    public static PokemonsData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physic);

        currentToast = Toast.makeText(getApplicationContext(), "Empty Toast", Toast.LENGTH_SHORT);

        Button previousStep = findViewById(R.id.previousStepButtonPhys);
        Button nextStep = findViewById(R.id.nextStepButtonPhys);

        previousStep.setOnClickListener(view -> {
            Log.d("Form progression", "Going back to perso activity");
            Intent intent = new Intent(this, PersoActivity.class);
            startActivity(intent);
        });
        nextStep.setOnClickListener(view -> {
            //Filter the data
            data.logData();
            Log.d("Form progression", "Proceeding to end activity");
            //Intent intent = new Intent(this, PhysicActivity.class);
            //startActivity(intent);
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //Set the data to the ones stocked in previous activity in case of the user goes back in the app
        data = new PokemonsData(PersoActivity.data);
    }
}
