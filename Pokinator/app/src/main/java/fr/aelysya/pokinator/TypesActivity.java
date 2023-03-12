package fr.aelysya.pokinator;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

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
                Log.d("Type missing", "Can't go to next step, at least one type is missing");
                currentToast.cancel();
                currentToast.setText(R.string.type_missing);
                currentToast.show();
            } else {
                //Filter the data
                data.filterPreferredType(prefType);
                data.filterDislikedType(disType);
                data.logData();
                //Start the popup window to ask the user if they want to answer stats questions
                launchPopupWindow(findViewById(R.id.backgroundType));
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //Set the data to the ones stocked in previous activity in case of the user goes back in the app
        data = new PokemonsData(PreferencesActivity.data);
    }

    private void launchPopupWindow(View view){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.ask_before_stats, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, 1000, 600, true);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 400);

        Button yesButton = popupView.findViewById(R.id.beforeStatsYes);
        Button noButton = popupView.findViewById(R.id.beforeStatsNo);

        noButton.setOnClickListener(v -> {
            popupWindow.dismiss();
            Log.d("Form progression", "Proceeding to perso activity");
            Intent intent = new Intent(this, PersoActivity.class);
            intent.putExtra("skippedStats", true);
            startActivity(intent);
        });
        yesButton.setOnClickListener(v -> {
            popupWindow.dismiss();
            Log.d("Form progression", "Proceeding to stats activity");
            Intent intent = new Intent(this, StatsActivity.class);
            startActivity(intent);
        });
    }

    /** Change the activity background's top part depending on the chosen type
     * @param v The text that was pressed
     */
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
        if(disType.equals(prefType)
                || (disType.equals("ghost") && prefType.equals("normal"))
                || (disType.equals("electric") && prefType.equals("ground"))
                || (disType.equals("ground") && prefType.equals("flying"))
                || (disType.equals("psychic") && prefType.equals("dark"))
                || (disType.equals("normal") && prefType.equals("ghost"))
                || (disType.equals("fighting") && prefType.equals("ghost"))
                || (disType.equals("poison") && prefType.equals("steel"))
                || (disType.equals("dragon") && prefType.equals("fairy"))
        ){
            layerDrawable.setDrawableByLayerId(R.id.bottomBack, ContextCompat.getDrawable(context, R.drawable.blank));
            disType = "";
        }


        //Set the new image
        ImageView typeBack = findViewById(R.id.backgroundType);
        typeBack.setImageDrawable(layerDrawable);

        //Change text color to mark the type picked
        TextView text = (TextView) v;
        text.setTextColor(ContextCompat.getColor(context, R.color.black));

        manageDislikeImages();
    }

    /** Change the activity background's bottom part depending on the chosen type
     * @param v The text that was pressed
     */
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
        text.setTextColor(ContextCompat.getColor(context, R.color.black));
    }

    /**
     * Manage the disabling of dislike images after clicking on a preferred one
     */
    public void manageDislikeImages(){
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
        disableType(this.getResources().getIdentifier(prefType + "Text2", "id", this.getPackageName()),
                this.getResources().getIdentifier("dis" + prefType.substring(0,1).toUpperCase() + prefType.substring(1), "id", this.getPackageName()));

        //Disable the immunities of the preferred type
        switch (prefType){
            case "normal" :
                disableType(this.getResources().getIdentifier("ghostText2", "id", this.getPackageName()),
                        this.getResources().getIdentifier("disGhost", "id", this.getPackageName()));
                break;
            case "ground" :
                disableType(this.getResources().getIdentifier("electricText2", "id", this.getPackageName()),
                        this.getResources().getIdentifier("disElectric", "id", this.getPackageName()));
                break;
            case "flying" :
                disableType(this.getResources().getIdentifier("groundText2", "id", this.getPackageName()),
                        this.getResources().getIdentifier("disGround", "id", this.getPackageName()));
                break;
            case "ghost" :
                disableType(this.getResources().getIdentifier("normalText2", "id", this.getPackageName()),
                        this.getResources().getIdentifier("disNormal", "id", this.getPackageName()));

                disableType(this.getResources().getIdentifier("fightingText2", "id", this.getPackageName()),
                        this.getResources().getIdentifier("disFighting", "id", this.getPackageName()));
                break;
            case "dark" :
                disableType(this.getResources().getIdentifier("psychicText2", "id", this.getPackageName()),
                        this.getResources().getIdentifier("disPsychic", "id", this.getPackageName()));
                break;
            case "steel" :
                disableType(this.getResources().getIdentifier("poisonText2", "id", this.getPackageName()),
                        this.getResources().getIdentifier("disPoison", "id", this.getPackageName()));
                break;
            case "fairy" :
                disableType(this.getResources().getIdentifier("dragonText2", "id", this.getPackageName()),
                        this.getResources().getIdentifier("disDragon", "id", this.getPackageName()));
                break;
        }
    }

    /** Disable a text and an image based on their ID
     * @param textId The text ID
     * @param imageId The image ID
     */
    private void disableType(int textId, int imageId){
        TextView matchingDisText = findViewById(textId);
        lastDisabledTexts.add(matchingDisText);
        ImageView matchingDisImage = findViewById(imageId);
        lastDisabledImages.add(matchingDisImage);
        matchingDisText.setEnabled(false);
        matchingDisText.setTextColor(ContextCompat.getColor(this, R.color.light_gray));
        matchingDisImage.setColorFilter(ContextCompat.getColor(this, R.color.medium_gray), PorterDuff.Mode.MULTIPLY);
    }
}
