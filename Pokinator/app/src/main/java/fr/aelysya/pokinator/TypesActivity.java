package fr.aelysya.pokinator;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private String prefType;
    private String disType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_types);
        prefType = "";
        disType = "";
        currentToast = Toast.makeText(getApplicationContext(), "Empty Toast", Toast.LENGTH_SHORT);

        Button previousStep = findViewById(R.id.previousStepTypes);
        Button nextStep = findViewById(R.id.nextStepTypes);

        previousStep.setOnClickListener(view -> {
            Log.d("Form progression", "Going back to preferences activity");
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
        });
        nextStep.setOnClickListener(view -> {
            if(prefType.equals("") || disType.equals("")){
                Log.d("Generation boxes", "Can't go to next step, at least one type is missing");
                currentToast.cancel();
                currentToast.setText(R.string.type_missing);
                currentToast.show();
            } else {
                //Filter the data
                data.filterPreferredType(prefType);
                data.filterDislikedType(disType);
                data.logData();
                Log.d("Form progression", "Proceeding to stats activity");
                Intent intent = new Intent(this, StatsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //Set the data to the ones stocked in previous activity in case of the user goes back in the app
        data = new PokemonsData(PreferencesActivity.data);
    }

    //TODO add remembering af the type last selected
    public void computeTopBackground(View v){
        String fullName = getResources().getResourceName(v.getId());
        String type = fullName.substring(fullName.indexOf("/") + 1, fullName.lastIndexOf("Text"));
        prefType = type;
        Log.d("Type background changing", "Changing top part to " + type + " type");

        //Find the xml background
        LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(this, R.drawable.type_background);
        assert layerDrawable != null;
        //Construct the drawable name to find it
        Context context = v.getContext();
        int topId = context.getResources().getIdentifier(type + "_top", "drawable", context.getPackageName());
        //Modify image
        layerDrawable.setDrawableByLayerId(R.id.topBack, context.getResources().getDrawable(topId));

        //Refind the bottom image to not override it
        if(!disType.equals((""))){
            int bottomId = context.getResources().getIdentifier(disType + "_bottom", "drawable", context.getPackageName());
            layerDrawable.setDrawableByLayerId(R.id.bottomBack, context.getResources().getDrawable(bottomId));
        }

        //Set the new image
        ImageView typeBack = findViewById(R.id.backgroundType);
        typeBack.setImageDrawable(layerDrawable);
    }

    //TODO add remembering af the type last selected
    public void computeBottomBackground(View v){
        String fullName = getResources().getResourceName(v.getId());
        String type = fullName.substring(fullName.indexOf("/") + 1, fullName.lastIndexOf("Text"));
        disType = type;
        Log.d("Type background changing", "Changing bottom part to " + type + " type");

        //Find the xml background
        LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(this, R.drawable.type_background);
        assert layerDrawable != null;
        //Construct the drawable name to find it
        Context context = v.getContext();
        int bottomId = context.getResources().getIdentifier(type + "_bottom", "drawable", context.getPackageName());
        //Modify image
        layerDrawable.setDrawableByLayerId(R.id.bottomBack, context.getResources().getDrawable(bottomId));

        //Refind the top part to not override it with blank image
        if(!prefType.equals((""))){
            int topId = context.getResources().getIdentifier(prefType + "_top", "drawable", context.getPackageName());
            layerDrawable.setDrawableByLayerId(R.id.topBack, context.getResources().getDrawable(topId));
        }

        //Set the new image
        ImageView typeBack = findViewById(R.id.backgroundType);
        typeBack.setImageDrawable(layerDrawable);
    }
}
