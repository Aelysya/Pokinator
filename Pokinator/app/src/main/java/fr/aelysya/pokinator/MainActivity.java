package fr.aelysya.pokinator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            CSVReader reader = new CSVReader(new InputStreamReader(getResources().openRawResource(R.raw.pokemon)));
            String[] nextLine;
            nextLine = reader.readNext();
            Log.d("POKINATOR", nextLine[10]);
        } catch (IOException e) {
            Log.d("POKINATOR", "File not found.");
        }
    }
}