package fr.aelysya.pokinator;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

public class PersoActivity extends AppCompatActivity {

    public static PokemonsData data;
    RatingBar eggBar;
    RatingBar capBar;
    RatingBar expBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perso);

        Button previousStep = findViewById(R.id.previousStepButtonPerso);
        Button nextStep = findViewById(R.id.nextStepButtonPerso);

        eggBar = findViewById(R.id.eggRating);
        capBar = findViewById(R.id.captureRating);
        expBar = findViewById(R.id.experienceRating);

        if(getIntent().getBooleanExtra("skippedStats", true)){
            data = new PokemonsData(DataHistory.getInstance().getHistory("types"));
        } else {
            data = new PokemonsData(DataHistory.getInstance().getHistory("stats"));
        }

        previousStep.setOnClickListener(view -> {
            Intent intent;
            if(getIntent().getBooleanExtra("skippedStats", true)){
                Log.d("Form progression", "Going back to types activity");
                intent = new Intent(this, TypesActivity.class);
            } else {
                Log.d("Form progression", "Going back to stats activity");
                intent = new Intent(this, StatsActivity.class);
            }
            startActivity(intent);
            finish();
        });
        nextStep.setOnClickListener(view -> {
            //Filter the data
            if(eggBar.getRating() < 2.0f || eggBar.getRating() > 3.0f){ //If in the middle, do not filter
                data.filterEggSteps(eggBar.getRating() < 2.0f);
            }
            if(capBar.getRating() < 2.0f || capBar.getRating() > 3.0f){
                data.filterCaptureRate(capBar.getRating() > 3.0f);
            }
            if(expBar.getRating() < 2.0f || expBar.getRating() > 3.0f){
                data.filterMaxExperience(expBar.getRating() > 3.0f);
            }
            data.logData();
            DataHistory.getInstance().setHistory("perso", data);
            Log.d("Form progression", "Proceeding to physic activity");
            startActivity(new Intent(this, PhysicActivity.class));
            finish();
        });
    }
}
