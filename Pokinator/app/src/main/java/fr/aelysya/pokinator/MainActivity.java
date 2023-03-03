package fr.aelysya.pokinator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;

import fr.aelysya.pokinator.utility.PokemonsData;

public class MainActivity extends AppCompatActivity {

    public static final String APP_TAG = "Pokinator";
    public static final PokemonsData DATA = new PokemonsData();
    public static boolean isXavier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isXavier = true;

        ImageView logo = findViewById(R.id.logo);

        logo.setOnClickListener(view -> {
            logo.setImageResource(isXavier ? R.drawable.pokemon_hakim_clear : R.drawable.pokemon_xavier_clear);
            isXavier = !isXavier;
        });

        DATA.loadCSV(new InputStreamReader(getResources().openRawResource(R.raw.pokemon)));
    }
}