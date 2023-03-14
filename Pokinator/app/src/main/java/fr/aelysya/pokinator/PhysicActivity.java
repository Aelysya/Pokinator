package fr.aelysya.pokinator;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
        SeekBar sizeBar = findViewById(R.id.sizeBar);
        SeekBar weightBar = findViewById(R.id.weightBar);
        ImageView bodyImage = findViewById(R.id.bodyImage);
        ImageView scalePlates = findViewById(R.id.scalePlates);

        sizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //Scale the image between 0.5 and 1.5
                float scale = (seekBar.getProgress() * 0.01f) + 0.5f;
                bodyImage.setScaleX(scale);
                bodyImage.setScaleY(scale);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10,
                getResources().getDisplayMetrics()
        );

        weightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //Rotate the plates image from -20 to 20
                scalePlates.setRotation((seekBar.getProgress() * -0.4f) + 20.0f);
                //Move body image between 40 and 200 layout margin bottom
                bodyImage.setTranslationY((int) ((1.6 * weightBar.getProgress()) - 80));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
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
            //Intent intent = new Intent(this, ResultsActivity.class);
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
