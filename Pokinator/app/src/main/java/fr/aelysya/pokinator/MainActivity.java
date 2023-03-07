package fr.aelysya.pokinator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import fr.aelysya.pokinator.utility.PokemonsData;

public class MainActivity extends AppCompatActivity {

    public static final String APP_TAG = "Pokinator";
    public static final PokemonsData DATA = new PokemonsData();
    private static boolean isXavier;
    private ImageView shinyCharm;
    private Toast currentToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentToast = Toast.makeText(getApplicationContext(), "Empty toast", Toast.LENGTH_SHORT);

        isXavier = true;
        ImageView logo = findViewById(R.id.logo);
        ImageView mysteryGift = findViewById(R.id.mysteryGift);
        shinyCharm = findViewById(R.id.shinyCharm);

        logo.setOnClickListener(view -> {
            Log.d("Action detected", "Switching version");
            logo.setImageResource(isXavier ? R.drawable.pokemon_hakim_clear : R.drawable.pokemon_xavier_clear);
            isXavier = !isXavier;
        });

        mysteryGift.setOnClickListener(view -> {
            Log.d("Action detected", "Launch code window");
            launchCodeWindow(view);
        });

        DATA.loadCSV(new InputStreamReader(getResources().openRawResource(R.raw.pokemon)));
    }

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
                currentToast.setText("Congratulations, you unlocked the shiny charm !");
                currentToast.show();
                popupWindow.dismiss();
                vibrate(50);
            } else {
                currentToast.cancel();
                currentToast.setText("Unknown code, try again");
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

    public boolean checkCode(){
        boolean codeOk = false;
        if(stringCode.equals("HAXACOEUR")){
            shinyCharm.setVisibility(View.VISIBLE);
            codeOk = true;
            Log.d("Mystery gift code", "Code valid, enabling shiny charm");
        }
        return codeOk;
    }

}