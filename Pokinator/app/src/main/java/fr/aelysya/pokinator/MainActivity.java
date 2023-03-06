package fr.aelysya.pokinator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;

import fr.aelysya.pokinator.utility.PokemonsData;

public class MainActivity extends AppCompatActivity {

    public static final String APP_TAG = "Pokinator";
    public static final PokemonsData DATA = new PokemonsData();
    private static boolean isXavier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        isXavier = true;
        ImageView logo = findViewById(R.id.logo);
        ImageView mysteryGift = findViewById(R.id.mysteryGift);
        ImageView shinyCharm = findViewById(R.id.shinyCharm);

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

    public void launchCodeWindow(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.code_enter, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, 1000, 1000, true);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 250);

        Button cancelButton = popupView.findViewById(R.id.cancelButton);
        Button confirmButton = popupView.findViewById(R.id.confirmButton);

        cancelButton.setOnClickListener(view -> popupWindow.dismiss());
        confirmButton.setOnClickListener(view -> popupWindow.dismiss()); //TODO implement code verification
    }
}