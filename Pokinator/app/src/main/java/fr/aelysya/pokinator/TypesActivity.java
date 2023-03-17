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

public class TypesActivity extends AppCompatActivity {

    private Toast currentToast;
    public static PokemonsData data;
    private String preferredType;
    private String dislikedType;
    private List<TextView> lastDisabledTextsTop;
    private List<TextView> lastDisabledTextsBottom;
    private List<ImageView> lastDisabledImagesTop;
    private List<ImageView> lastDisabledImagesBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_types);
        currentToast = Toast.makeText(getApplicationContext(), "Empty Toast", Toast.LENGTH_SHORT);
        lastDisabledImagesBottom = new ArrayList<>();
        lastDisabledImagesTop = new ArrayList<>();
        lastDisabledTextsBottom = new ArrayList<>();
        lastDisabledTextsTop = new ArrayList<>();
        preferredType = "";
        dislikedType = "";
        data = new PokemonsData(DataHistory.getInstance().getHistory("preferences"));

        Button previousStep = findViewById(R.id.previousStepButtonTypes);
        Button nextStep = findViewById(R.id.nextStepButtonTypes);

        previousStep.setOnClickListener(view -> {
            Log.d("Form progression", "Going back to preferences activity");
            startActivity(new Intent(this, PreferencesActivity.class));
            finish();
        });
        nextStep.setOnClickListener(view -> {
            if(preferredType.equals("") || dislikedType.equals("")){
                Log.d("Type missing", "Can't go to next step, at least one type is missing");
                currentToast.cancel();
                currentToast.setText(R.string.type_missing);
                currentToast.show();
            } else {
                //Filter the data
                data.filterPreferredType(preferredType);
                data.filterDislikedType(dislikedType);
                data.logData();
                DataHistory.getInstance().setHistory("types", data);
                //Start the popup window to ask the user if they want to answer stats questions
                launchPopupWindow(findViewById(R.id.backgroundType));
            }
        });
        disableAbsentTypes();
    }

    /**
     * If the user wants to get a legendary pokémon, nearly all set of 3 generations will lack some types that are possible to chose from.
     * Disables the types that are absent in the generation set chosen
     */
    private void disableAbsentTypes(){
        List<String> existingTypes = new ArrayList<>();
        for(String s : data.getFirstTypes()){
            if(!existingTypes.contains(s)){
                existingTypes.add(s);
            }
        }

        for(String s : data.getSecondTypes()){
            if(!existingTypes.contains(s) && !s.equals("")){
                existingTypes.add(s);
            }
        }

        for(String s : data.getTypes()){
            if(!existingTypes.contains(s)){
                disableTopType(this.getResources().getIdentifier(s + "Text", "id", this.getPackageName()),
                        this.getResources().getIdentifier("pref" + s.substring(0, 1).toUpperCase() + s.substring(1), "id", this.getPackageName()));
            }
        }
    }

    /** Launch the popup window to ask the user if they want to answer questions on their pokémon's statistics
     * @param view The NextStep button that was pressed
     */
    private void launchPopupWindow(View view){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.ask_before_stats, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, 1000, 700, true);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 400);

        Button yesButton = popupView.findViewById(R.id.beforeStatsYes);
        Button noButton = popupView.findViewById(R.id.beforeStatsNo);

        noButton.setOnClickListener(v -> {
            popupWindow.dismiss();
            Log.d("Form progression", "Proceeding to perso activity, skipping stats activity");
            Intent intent = new Intent(this, PersoActivity.class);
            intent.putExtra("skippedStats", true);
            startActivity(intent);
            finish();
        });
        yesButton.setOnClickListener(v -> {
            popupWindow.dismiss();
            Log.d("Form progression", "Proceeding to stats activity");
            startActivity(new Intent(this, StatsActivity.class));
            finish();
        });
    }

    /** Change the activity background's top part depending on the chosen type
     * @param v The text that was pressed
     */
    public void computeTopBackground(View v){
        Context context = v.getContext();
        //Revert last type selected to white text
        if(!preferredType.equals("")){
            int textId = this.getResources().getIdentifier(preferredType + "Text", "id", context.getPackageName());
            TextView lastText = findViewById(textId);
            lastText.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        String fullName = getResources().getResourceName(v.getId());
        String type = fullName.substring(fullName.indexOf("/") + 1, fullName.lastIndexOf("Text"));
        preferredType = type;
        Log.d("Type background changing", "Changing top part to " + type + " type");

        //Find the xml background
        LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(this, R.drawable.type_background);
        assert layerDrawable != null;
        //Construct the drawable name to find it
        int topId = context.getResources().getIdentifier(type + "_top", "drawable", context.getPackageName());
        //Modify image
        layerDrawable.setDrawableByLayerId(R.id.topBack, ContextCompat.getDrawable(context, topId));

        //Re-find the bottom image to not override it
        if(!dislikedType.equals((""))){
            int bottomId = context.getResources().getIdentifier(dislikedType + "_bottom", "drawable", context.getPackageName());
            layerDrawable.setDrawableByLayerId(R.id.bottomBack, ContextCompat.getDrawable(context, bottomId));
        }
        if(dislikedType.equals(preferredType)
                || (dislikedType.equals("ghost") && preferredType.equals("normal"))
                || (dislikedType.equals("electric") && preferredType.equals("ground"))
                || (dislikedType.equals("ground") && preferredType.equals("flying"))
                || (dislikedType.equals("psychic") && preferredType.equals("dark"))
                || (dislikedType.equals("normal") && preferredType.equals("ghost"))
                || (dislikedType.equals("fighting") && preferredType.equals("ghost"))
                || (dislikedType.equals("poison") && preferredType.equals("steel"))
                || (dislikedType.equals("dragon") && preferredType.equals("fairy"))
        ){
            layerDrawable.setDrawableByLayerId(R.id.bottomBack, ContextCompat.getDrawable(context, R.drawable.blank));
            dislikedType = "";
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
        if(!dislikedType.equals("")){
            int textId = this.getResources().getIdentifier(dislikedType + "Text2", "id", context.getPackageName());
            TextView lastText = findViewById(textId);
            lastText.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        String fullName = getResources().getResourceName(v.getId());
        String type = fullName.substring(fullName.indexOf("/") + 1, fullName.lastIndexOf("Text"));
        dislikedType = type;
        Log.d("Type background changing", "Changing bottom part to " + type + " type");

        //Find the xml background
        LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(this, R.drawable.type_background);
        assert layerDrawable != null;
        //Construct the drawable name to find it
        int bottomId = context.getResources().getIdentifier(type + "_bottom", "drawable", context.getPackageName());
        //Modify image
        layerDrawable.setDrawableByLayerId(R.id.bottomBack, ContextCompat.getDrawable(context, bottomId));

        //Re-find the top part to not override it with blank image
        if(!preferredType.equals((""))){
            int topId = context.getResources().getIdentifier(preferredType + "_top", "drawable", context.getPackageName());
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
        for(TextView t : lastDisabledTextsBottom){
            t.setEnabled(true);
            t.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        for(ImageView i : lastDisabledImagesBottom){
            i.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.MULTIPLY);
        }
        lastDisabledImagesBottom.clear();
        lastDisabledTextsBottom.clear();
        //Disable the matching image of the preferred type
        disableBottomType(this.getResources().getIdentifier(preferredType + "Text2", "id", this.getPackageName()),
                this.getResources().getIdentifier("dis" + preferredType.substring(0,1).toUpperCase() + preferredType.substring(1), "id", this.getPackageName()));

        //Disable the immunities of the preferred type
        switch (preferredType){
            case "normal" :
                disableBottomType(this.getResources().getIdentifier("ghostText2", "id", this.getPackageName()),
                        this.getResources().getIdentifier("disGhost", "id", this.getPackageName()));
                break;
            case "ground" :
                disableBottomType(this.getResources().getIdentifier("electricText2", "id", this.getPackageName()),
                        this.getResources().getIdentifier("disElectric", "id", this.getPackageName()));
                break;
            case "flying" :
                disableBottomType(this.getResources().getIdentifier("groundText2", "id", this.getPackageName()),
                        this.getResources().getIdentifier("disGround", "id", this.getPackageName()));
                break;
            case "ghost" :
                disableBottomType(this.getResources().getIdentifier("normalText2", "id", this.getPackageName()),
                        this.getResources().getIdentifier("disNormal", "id", this.getPackageName()));

                disableBottomType(this.getResources().getIdentifier("fightingText2", "id", this.getPackageName()),
                        this.getResources().getIdentifier("disFighting", "id", this.getPackageName()));
                break;
            case "dark" :
                disableBottomType(this.getResources().getIdentifier("psychicText2", "id", this.getPackageName()),
                        this.getResources().getIdentifier("disPsychic", "id", this.getPackageName()));
                break;
            case "steel" :
                disableBottomType(this.getResources().getIdentifier("poisonText2", "id", this.getPackageName()),
                        this.getResources().getIdentifier("disPoison", "id", this.getPackageName()));
                break;
            case "fairy" :
                disableBottomType(this.getResources().getIdentifier("dragonText2", "id", this.getPackageName()),
                        this.getResources().getIdentifier("disDragon", "id", this.getPackageName()));
                break;
        }
    }

    /** Disable a text and an image based on their ID in the bottom part
     * @param textId The text ID
     * @param imageId The image ID
     */
    private void disableBottomType(int textId, int imageId){
        TextView matchingDisText = findViewById(textId);
        lastDisabledTextsBottom.add(matchingDisText);
        ImageView matchingDisImage = findViewById(imageId);
        lastDisabledImagesBottom.add(matchingDisImage);
        matchingDisText.setEnabled(false);
        matchingDisText.setTextColor(ContextCompat.getColor(this, R.color.light_gray));
        matchingDisImage.setColorFilter(ContextCompat.getColor(this, R.color.medium_gray), PorterDuff.Mode.MULTIPLY);
    }

    /** Disable a text and an image based on their ID in the top part
     * @param textId The text ID
     * @param imageId The image ID
     */
    private void disableTopType(int textId, int imageId){
        TextView prefText = findViewById(textId);
        lastDisabledTextsTop.add(prefText);
        ImageView prefImage = findViewById(imageId);
        lastDisabledImagesTop.add(prefImage);
        prefText.setEnabled(false);
        prefText.setTextColor(ContextCompat.getColor(this, R.color.light_gray));
        prefImage.setColorFilter(ContextCompat.getColor(this, R.color.medium_gray), PorterDuff.Mode.MULTIPLY);
    }
}
