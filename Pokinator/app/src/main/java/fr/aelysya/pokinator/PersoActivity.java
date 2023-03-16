package fr.aelysya.pokinator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

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

        previousStep.setOnClickListener(view -> {
            if(getIntent().getBooleanExtra("skippedStats", true)){
                Log.d("Form progression", "Going back to types activity");
                Intent intent = new Intent(this, TypesActivity.class);
                startActivity(intent);
            } else {
                Log.d("Form progression", "Going back to stats activity");
                Intent intent = new Intent(this, StatsActivity.class);
                startActivity(intent);
            }
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
            Log.d("Form progression", "Proceeding to physic activity");
            Intent intent = new Intent(this, PhysicActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //Set the data to the ones stocked in previous activity in case of the user goes back in the app
        if(getIntent().getBooleanExtra("skippedStats", true)){
            data = new PokemonsData(TypesActivity.data);
        } else {
            data = new PokemonsData(StatsActivity.data);
        }
        //Reset the widgets
        eggBar.setRating(1);
        capBar.setRating(1);
        expBar.setRating(1);
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
