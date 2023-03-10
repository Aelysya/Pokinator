package fr.aelysya.pokinator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import fr.aelysya.pokinator.utility.PokemonsData;

public class StatsActivity extends AppCompatActivity {

    private Toast currentToast;
    public static PokemonsData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        currentToast = Toast.makeText(getApplicationContext(), "Empty Toast", Toast.LENGTH_SHORT);

        Button previousStep = findViewById(R.id.previousStepStats);
        Button nextStep = findViewById(R.id.nextStepStats);

        previousStep.setOnClickListener(view -> {
            Log.d("Form progression", "Going back to types activity");
            Intent intent = new Intent(this, TypesActivity.class);
            startActivity(intent);
        });

        nextStep.setOnClickListener(view -> {
            //Filter the data
            Log.d("Form progression", "Proceeding to perso activity");
            Intent intent = new Intent(this, PersoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //Set the data to the ones stocked in previous activity in case of the user goes back in the app
        data = new PokemonsData(TypesActivity.data);
    }

    public void validationStat(View view){

        FragmentManager fragmentManager = getSupportFragmentManager();

        if(fragmentManager.findFragmentByTag("statFragment") == null){

            Log.d("Tag", "Fragment not Found");
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.statFragment, SpeedTestFragment.class, null, "statFragment")
                    .commit();
        }
        else{

            Log.d("Tag", "Fragment Found");
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .remove(fragmentManager.findFragmentByTag("statFragment"))
                    .commit();
        }
    }

}
