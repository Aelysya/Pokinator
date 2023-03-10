package fr.aelysya.pokinator;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import fr.aelysya.pokinator.utility.PokemonsData;

public class PersoActivity extends AppCompatActivity {

    private Toast currentToast;
    public static PokemonsData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perso);

        currentToast = Toast.makeText(getApplicationContext(), "Empty Toast", Toast.LENGTH_SHORT);

        Button previousStep = findViewById(R.id.previousStepPerso);
        Button nextStep = findViewById(R.id.nextStepPerso);

        RatingBar eggBar = findViewById(R.id.eggRating);
        RatingBar capBar = findViewById(R.id.captureRating);
        RatingBar expBar = findViewById(R.id.experienceRating);

        previousStep.setOnClickListener(view -> {
            Log.d("Form progression", "Going back to stats activity");
            Intent intent = new Intent(this, StatsActivity.class);
            startActivity(intent);
        });
        nextStep.setOnClickListener(view -> {
            //Filter the data
            if(eggBar.getRating() != 2.0f){ //If middle value, do not filter
                data.filterEggSteps(eggBar.getRating() == 1.0f);
            }
            if(capBar.getRating() != 2.0f){
                data.filterCaptureRate(capBar.getRating() == 3.0f);
            }
            if(expBar.getRating() != 2.0f){
                data.filterMaxExperience(expBar.getRating() == 3.0f);
            }
            data.logData();
            Log.d("Form progression", "Proceeding to physic activity");
            //Intent intent = new Intent(this, PhysicActivity.class);
            //startActivity(intent);
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //Set the data to the ones stocked in previous activity in case of the user goes back in the app
        data = new PokemonsData(StatsActivity.data);
    }
}
