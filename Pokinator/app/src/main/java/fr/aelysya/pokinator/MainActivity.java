package fr.aelysya.pokinator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import fr.aelysya.pokinator.utility.PokemonsData;

public class MainActivity extends AppCompatActivity {

    public static final String APP_TAG = "Pokinator";
    public static PokemonsData data = new PokemonsData();
    private static boolean isXavier;
    private static boolean shinyCharmEnabled;
    private ImageView shinyCharm;
    EditText nameInput;
    private Toast currentToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentToast = Toast.makeText(getApplicationContext(), "Empty toast", Toast.LENGTH_SHORT);

        isXavier = true;
        shinyCharmEnabled = true;

        ImageView logo = findViewById(R.id.logo);
        ImageView mysteryGift = findViewById(R.id.mysteryGift);
        shinyCharm = findViewById(R.id.shinyCharm);
        shinyCharm.setEnabled(false);
        Button beginButton = findViewById(R.id.beginButton);
        nameInput = findViewById(R.id.nameInput);

        //Load the CSV with all the data
        data.loadCSV(new InputStreamReader(getResources().openRawResource(R.raw.pokemon)));

        logo.setOnClickListener(view -> {
            Log.d("Action detected", "Switching version");
            logo.setImageResource(isXavier ? R.drawable.pokemon_hakim_clear : R.drawable.pokemon_xavier_clear);
            isXavier = !isXavier;
        });

        mysteryGift.setOnClickListener(view -> {
            Log.d("Action detected", "Launch code window");
            launchCodeWindow(view);
        });

        shinyCharm.setOnClickListener(view -> {
            Log.d("Action detected", "Switching shiny charm state");
            shinyCharm.setImageResource(shinyCharmEnabled ? R.drawable.shiny_charm_crossed : R.drawable.shiny_charm);
            shinyCharmEnabled = !shinyCharmEnabled;
        });

        beginButton.setOnClickListener(view -> {
            String userName = nameInput.getText().toString();
            if(userName.equals("")){
                currentToast.cancel();
                currentToast.setText(R.string.empty_name_input);
                currentToast.show();
            } else {
                Intent intent = new Intent(this, PreferencesActivity.class);
                intent.putExtra("userName", userName);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        nameInput.setText("");
    }

    /** Make the phone vibrate
     * @param duration_ms Vibration duration in milliseconds
     */
    public void vibrate(long duration_ms) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(duration_ms < 1)
            duration_ms = 1;
        if(v != null && v.hasVibrator()) {
            v.vibrate(VibrationEffect.createOneShot(duration_ms,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }


    //Mystery gift code management part
    private List<ImageView> codeImageViews;
    private String stringCode;

    /** Show the popup window containing the mystery gift code keyboard
     * @param v The view to show the popup window in
     */
    public void launchCodeWindow(View v) {
        codeImageViews = new ArrayList<>();
        stringCode = "";

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.code_enter, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, 1000, 1000, true);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 250);

        Button cancelButton = popupView.findViewById(R.id.cancelButton);
        Button confirmButton = popupView.findViewById(R.id.confirmButton);
        ImageView delButton = popupView.findViewById(R.id.delButton);
        codeImageViews.add(popupView.findViewById(R.id.codeChar1));
        codeImageViews.add(popupView.findViewById(R.id.codeChar2));
        codeImageViews.add(popupView.findViewById(R.id.codeChar3));
        codeImageViews.add(popupView.findViewById(R.id.codeChar4));
        codeImageViews.add(popupView.findViewById(R.id.codeChar5));
        codeImageViews.add(popupView.findViewById(R.id.codeChar6));
        codeImageViews.add(popupView.findViewById(R.id.codeChar7));
        codeImageViews.add(popupView.findViewById(R.id.codeChar8));
        codeImageViews.add(popupView.findViewById(R.id.codeChar9));

        cancelButton.setOnClickListener(view -> popupWindow.dismiss());
        confirmButton.setOnClickListener(view -> {
            if(checkCode()){
                currentToast.cancel();
                currentToast.setText(R.string.code_congrat);
                currentToast.show();
                popupWindow.dismiss();
                vibrate(50);
            } else {
                currentToast.cancel();
                currentToast.setText(R.string.code_fail);
                currentToast.show();
                vibrate(100);
            }
        });

        delButton.setOnClickListener(view -> {
            //Remove the last character
            if(!stringCode.equals("")){
                vibrate(20);
                stringCode = stringCode.substring(0, stringCode.length()-1);
                codeImageViews.get(stringCode.length()).setImageResource(R.drawable.blank);
            }
            else{
                vibrate(100);
            }
            Log.d("Mystery gift code", "Removing last character, current code: " + stringCode);
        });
    }

    /** Construct and show the code when a key is clicked in the mystery gift code keyboard
     * @param v The ImageView that was clicked on
     */
    public void codeKeyboardOnClick(View v){
        if(stringCode.length() < 9){
            String fullName = getResources().getResourceName(v.getId());
            String name = fullName.substring(fullName.lastIndexOf("/") + 1);
            //If the clicked image is a digit
            if(name.length() > 1){
                switch(name){
                    case "zero": stringCode += "0"; break;
                    case "one": stringCode += "1"; break;
                    case "two": stringCode += "2"; break;
                    case "three": stringCode += "3"; break;
                    case "four": stringCode += "4"; break;
                    case "five": stringCode += "5"; break;
                    case "six": stringCode += "6"; break;
                    case "seven": stringCode += "7"; break;
                    case "eight": stringCode += "8"; break;
                    case "nine": stringCode += "9"; break;
                    default:
                }
            } else {
                stringCode += name;
            }
            ImageView img = (ImageView) v;
            codeImageViews.get(stringCode.length()-1).setImageDrawable(img.getDrawable());
            vibrate(20);
            Log.d("Mystery gift code", "Current code: " + stringCode);
        } else {
            Log.d("Mystery gift code", "Max code size reached, ignored input");
        }
    }

    /** Verify the current code and compare it the the known ones
     * @return Whether the current code is valid or not
     */
    public boolean checkCode(){
        boolean codeOk = false;
        if(stringCode.equals("HAXACOEUR")){
            shinyCharm.setEnabled(true);
            shinyCharm.setVisibility(View.VISIBLE);
            codeOk = true;
            Log.d("Mystery gift code", "Code valid, enabling shiny charm");
        }
        return codeOk;
    }

}