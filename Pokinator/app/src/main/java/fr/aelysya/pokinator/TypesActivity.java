package fr.aelysya.pokinator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import fr.aelysya.pokinator.utility.PokemonsData;

public class TypesActivity extends AppCompatActivity {

    private Toast currentToast;
    public static PokemonsData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_types);

        Button previousStep = findViewById(R.id.previousStepTypes);
        Button nextStep = findViewById(R.id.nextStepTypes);

        previousStep.setOnClickListener(view -> {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
        });
        nextStep.setOnClickListener(view -> {
            //Filter the data

            data.logData();
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //Set the data to the ones stocked in previous activity in case of the user goes back in the app
        data = new PokemonsData(PreferencesActivity.data);
        data.logData();
    }
}
