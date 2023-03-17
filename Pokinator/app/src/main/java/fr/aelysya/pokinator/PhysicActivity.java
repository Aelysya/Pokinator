package fr.aelysya.pokinator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

public class PhysicActivity extends AppCompatActivity {

    public static PokemonsData data;

    SeekBar sizeBar;
    SeekBar weightBar;
    ImageView bodyImage;
    ImageView scalePlates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physic);

        Button previousStep = findViewById(R.id.previousStepButtonPhys);
        Button nextStep = findViewById(R.id.nextStepButtonPhys);
        sizeBar = findViewById(R.id.sizeBar);
        weightBar = findViewById(R.id.weightBar);
        bodyImage = findViewById(R.id.bodyImage);
        scalePlates = findViewById(R.id.scalePlates);

        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //Scale the image between 0.5 and 1.5
                float scale = (seekBar.getProgress() * 0.01f) + 0.5f;
                bodyImage.setScaleX(scale);
                bodyImage.setScaleY(scale);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        weightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //Rotate the plates image from -20 to 20
                scalePlates.setRotation((seekBar.getProgress() * -0.4f) + 20.0f);
                //Move body image between 40 and 200 layout margin bottom
                bodyImage.setTranslationY((int) ((1.6 * weightBar.getProgress()) - 80));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        previousStep.setOnClickListener(view -> {
            Log.d("Form progression", "Going back to perso activity");
            Intent intent = new Intent(this, PersoActivity.class);
            startActivity(intent);
        });
        nextStep.setOnClickListener(view -> {
            //Filter the data
            data.filterSize(sizeBar.getProgress());
            data.filterWeight(weightBar.getProgress());
            data.logData();
            Log.d("Form progression", "Proceeding to results activity");
            Intent intent = new Intent(this, ResultActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //Set the data to the ones stocked in previous activity in case of the user goes back in the app
        data = new PokemonsData(PersoActivity.data);
        //Reset the widgets
        sizeBar.setProgress(50);
        weightBar.setProgress(50);
        bodyImage.setTranslationY(0);
        bodyImage.setScaleX(1);
        bodyImage.setScaleY(1);
        scalePlates.setRotation(0.0f);
    }
}
