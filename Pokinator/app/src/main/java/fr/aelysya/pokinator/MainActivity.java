package fr.aelysya.pokinator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    public static final String APP_TAG = "Pokinator";
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

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.pokemon)));
            String[] nextLine;
            nextLine = reader.readNext();
            Log.d(APP_TAG, nextLine[0]);
        } catch (IOException e) {
            Log.d(APP_TAG, "File not found.");
        }
    }
}