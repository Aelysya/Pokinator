package fr.aelysya.pokinator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
        data = new PokemonsData(DataHistory.getInstance().getHistory("perso"));

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
            vibrate();
            startActivity(new Intent(this, PersoActivity.class).putExtra("userName", getIntent().getStringExtra("userName")));
            finish();
        });
        nextStep.setOnClickListener(view -> {
            //Filter the data
            data.filterSize(sizeBar.getProgress());
            data.filterWeight(weightBar.getProgress());
            data.logData();
            DataHistory.getInstance().setHistory("physic", data);
            Log.d("Form progression", "Proceeding to results activity");
            vibrate();
            startActivity(new Intent(this, ResultActivity.class).putExtra("userName", getIntent().getStringExtra("userName")));
            finish();
        });
    }

    /** Make the phone vibrate
     */
    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(v != null && v.hasVibrator()) {
            v.vibrate(VibrationEffect.createOneShot(50,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }
}
