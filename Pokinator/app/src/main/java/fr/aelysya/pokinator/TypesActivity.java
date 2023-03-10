package fr.aelysya.pokinator;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.aelysya.pokinator.utility.PokemonsData;

public class TypesActivity extends AppCompatActivity {

    private Toast currentToast;
    public static PokemonsData data;
    private String prefType;
    private String disType;
    private List<TextView> lastDisabledTexts;
    private List<ImageView> lastDisabledImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_types);
        prefType = "";
        disType = "";
        currentToast = Toast.makeText(getApplicationContext(), "Empty Toast", Toast.LENGTH_SHORT);
        lastDisabledImages = new ArrayList<>();
        lastDisabledTexts = new ArrayList<>();

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
        Context context = v.getContext();
        //Revert last type selected to white text
        if(!prefType.equals("")){
            int textId = this.getResources().getIdentifier(prefType + "Text", "id", context.getPackageName());
            TextView lastText = findViewById(textId);
            lastText.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        String fullName = getResources().getResourceName(v.getId());
        String type = fullName.substring(fullName.indexOf("/") + 1, fullName.lastIndexOf("Text"));
        prefType = type;
        Log.d("Type background changing", "Changing top part to " + type + " type");

        //Find the xml background
        LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(this, R.drawable.type_background);
        assert layerDrawable != null;
        //Construct the drawable name to find it
        int topId = context.getResources().getIdentifier(type + "_top", "drawable", context.getPackageName());
        //Modify image
        layerDrawable.setDrawableByLayerId(R.id.topBack, ContextCompat.getDrawable(context, topId));

        //Re-find the bottom image to not override it
        if(!disType.equals((""))){
            int bottomId = context.getResources().getIdentifier(disType + "_bottom", "drawable", context.getPackageName());
            layerDrawable.setDrawableByLayerId(R.id.bottomBack, ContextCompat.getDrawable(context, bottomId));
        }

        //Set the new image
        ImageView typeBack = findViewById(R.id.backgroundType);
        typeBack.setImageDrawable(layerDrawable);

        //Change text color to mark the type picked
        TextView text = (TextView) v;
        text.setTextColor(getResources().getColor(R.color.black));

        disableDislikeButtons();
    }

    //TODO add remembering af the type last selected
    public void computeBottomBackground(View v){
        Context context = v.getContext();
        //Revert last type selected to white text
        if(!disType.equals("")){
            int textId = this.getResources().getIdentifier(disType + "Text2", "id", context.getPackageName());
            TextView lastText = findViewById(textId);
            lastText.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        String fullName = getResources().getResourceName(v.getId());
        String type = fullName.substring(fullName.indexOf("/") + 1, fullName.lastIndexOf("Text"));
        disType = type;
        Log.d("Type background changing", "Changing bottom part to " + type + " type");

        //Find the xml background
        LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(this, R.drawable.type_background);
        assert layerDrawable != null;
        //Construct the drawable name to find it
        int bottomId = context.getResources().getIdentifier(type + "_bottom", "drawable", context.getPackageName());
        //Modify image
        layerDrawable.setDrawableByLayerId(R.id.bottomBack, ContextCompat.getDrawable(context, bottomId));

        //Re-find the top part to not override it with blank image
        if(!prefType.equals((""))){
            int topId = context.getResources().getIdentifier(prefType + "_top", "drawable", context.getPackageName());
            layerDrawable.setDrawableByLayerId(R.id.topBack, ContextCompat.getDrawable(context, topId));
        }

        //Set the new image
        ImageView typeBack = findViewById(R.id.backgroundType);
        typeBack.setImageDrawable(layerDrawable);

        //Change text color to mark the type picked
        TextView text = (TextView) v;
        text.setTextColor(getResources().getColor(R.color.black));
    }

    public void disableDislikeButtons(){
        //Re-enable last disabled images and texts
        for(TextView t : lastDisabledTexts){
            t.setEnabled(true);
            t.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        for(ImageView i : lastDisabledImages){
            i.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.MULTIPLY);
        }
        lastDisabledImages.clear();
        lastDisabledTexts.clear();
        //Disable the matching image of the preferred type
        int textId = this.getResources().getIdentifier(prefType + "Text2", "id", this.getPackageName());
        int imageId = this.getResources().getIdentifier("dis" + prefType.substring(0,1).toUpperCase() + prefType.substring(1), "id", this.getPackageName());
        TextView matchingDisText = findViewById(textId);
        lastDisabledTexts.add(matchingDisText);
        ImageView matchingDisImage = findViewById(imageId);
        lastDisabledImages.add(matchingDisImage);
        matchingDisText.setEnabled(false);
        matchingDisText.setTextColor(ContextCompat.getColor(this, R.color.light_gray));
        matchingDisImage.setColorFilter(ContextCompat.getColor(this, R.color.medium_gray), PorterDuff.Mode.MULTIPLY);
        //Disable the immunities of the preferred type
        switch (prefType){
            
        }
    }
}
