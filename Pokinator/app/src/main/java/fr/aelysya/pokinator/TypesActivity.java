package fr.aelysya.pokinator;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Objects;

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
        Button test = findViewById(R.id.test);

        previousStep.setOnClickListener(view -> {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
        });
        nextStep.setOnClickListener(view -> {
            //Filter the data

            data.logData();
        });

        //Example to change the background for other types
        test.setOnClickListener(view -> {
            //Find the xml background
            LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(this, R.drawable.type_background);
            assert layerDrawable != null;

            //Change the top and bottom parts by their id
            layerDrawable.setDrawableByLayerId(R.id.topBack, getResources().getDrawable(R.drawable.fire_top));
            layerDrawable.setDrawableByLayerId(R.id.bottomBack, getResources().getDrawable(R.drawable.ice_bottom));

            //Set the new image
            ImageView typeBack = findViewById(R.id.backgroundType);
            typeBack.setImageDrawable(layerDrawable);
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
