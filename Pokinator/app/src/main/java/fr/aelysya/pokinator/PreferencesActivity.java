package fr.aelysya.pokinator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import fr.aelysya.pokinator.utility.PokemonsData;

public class PreferencesActivity extends AppCompatActivity {

    private Toast currentToast;
    public static PokemonsData data;
    private List<CheckBox> generationBoxes;
    private int boxesChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        currentToast = Toast.makeText(getApplicationContext(), "Empty toast", Toast.LENGTH_SHORT);

        Button previousStep = findViewById(R.id.previousStepPref);
        Button nextStep = findViewById(R.id.nextStepPref);
        Switch keepLegendaries = findViewById(R.id.keepLegendaries);
        generationBoxes = new ArrayList<>();
        generationBoxes.add(findViewById(R.id.generation1));
        generationBoxes.add(findViewById(R.id.generation2));
        generationBoxes.add(findViewById(R.id.generation3));
        generationBoxes.add(findViewById(R.id.generation4));
        generationBoxes.add(findViewById(R.id.generation5));
        generationBoxes.add(findViewById(R.id.generation6));
        generationBoxes.add(findViewById(R.id.generation7));
        generationBoxes.add(findViewById(R.id.generation8));
        generationBoxes.add(findViewById(R.id.generation9));
        boxesChecked = 0;

        previousStep.setOnClickListener(view -> {
            Log.d("Form progression", "Going back to main activity");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        nextStep.setOnClickListener(view -> {
            //TODO ensure there are 3 boxes checked, cancel the form advancement if not
            //Filter the data
            data.filterLegendaries(keepLegendaries.isChecked());
            int[] generations = new int[3];
            int genCpt = 0;
            for(CheckBox c : generationBoxes){
                if(c.isChecked()){
                    String fullName = getResources().getResourceName(c.getId());
                    //Keep only the generation number
                    String number = fullName.substring(fullName.lastIndexOf("n") + 1);
                    generations[genCpt] = Integer.parseInt(number);
                    genCpt++;
                }
            }
            data.filterGeneration(generations);
            data.logData();
            Log.d("Form progression", "Proceeding to types activity");
            Intent intent = new Intent(this, TypesActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //Set the data to the ones stocked in previous activity in case of the user goes back in the app
        data = new PokemonsData(MainActivity.data);
    }

    public void verifyAmountOfCheckedBoxes(View v){
        CheckBox box = (CheckBox) v;
        if(box.isChecked()){
            if(boxesChecked < 3){
                Log.d("Generation boxes", "Adding a box");
                boxesChecked++;
            } else {
                Log.d("Generation boxes", "Max boxes reached, unchecking last box");
                currentToast.cancel();
                currentToast.setText(R.string.too_many_generations);
                currentToast.show();
                box.setChecked(false);
            }
        } else {
            Log.d("Generation boxes", "Removing a box");
            boxesChecked--;
        }
    }
}
