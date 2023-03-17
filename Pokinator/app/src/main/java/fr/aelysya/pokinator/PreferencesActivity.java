package fr.aelysya.pokinator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.List;

public class PreferencesActivity extends AppCompatActivity {

    public static PokemonsData data;
    private Toast currentToast;
    private List<CheckBox> generationBoxes;
    private int boxesChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        currentToast = Toast.makeText(getApplicationContext(), "Empty toast", Toast.LENGTH_SHORT);

        Button previousStep = findViewById(R.id.previousStepButtonPref);
        Button nextStep = findViewById(R.id.nextStepButtonPref);
        SwitchCompat keepLegendaries = findViewById(R.id.keepLegendariesSwitch);
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

        data = new PokemonsData(DataHistory.getInstance().getHistory("main"));
        boxesChecked = 0;

        previousStep.setOnClickListener(view -> {
            Log.d("Form progression", "Going back to main activity");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        nextStep.setOnClickListener(view -> {
            if(boxesChecked != 3){
                currentToast.cancel();
                currentToast.setText(R.string.not_enough_boxes_checked);
                currentToast.show();
            } else {
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
                DataHistory.getInstance().setHistory("preferences", data);
                Log.d("Form progression", "Proceeding to types activity");
                startActivity(new Intent(this, TypesActivity.class));
                finish();
            }
        });
    }

    /** Verify that the total amount of checkBoxes checked is not over 3
     * @param v The checkBox that was clicked
     */
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
