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

public class MainActivity extends AppCompatActivity {

    private Toast currentToast;
    public static PokemonsData data;
    public static boolean versionIsXavier;
    private EditText nameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentToast = Toast.makeText(getApplicationContext(), "Empty toast", Toast.LENGTH_SHORT);

        versionIsXavier = true;

        ImageView logo = findViewById(R.id.logo);
        Button beginButton = findViewById(R.id.beginButton);
        nameInput = findViewById(R.id.nameInput);

        //Load the CSV with all the data
        data = new PokemonsData();
        data.loadCSV(new InputStreamReader(getResources().openRawResource(R.raw.pokemon)));
        DataHistory.getInstance().setHistory("main", data);

        logo.setOnClickListener(view -> {
            Log.d("Main logo clicked", "Switching version");
            logo.setImageResource(versionIsXavier ? R.drawable.pokemon_hakim_clear : R.drawable.pokemon_xavier_clear);
            versionIsXavier = !versionIsXavier;
        });

        beginButton.setOnClickListener(view -> {
            String userName = nameInput.getText().toString();
            if(userName.equals("")){
                currentToast.cancel();
                currentToast.setText(R.string.empty_name_input);
                currentToast.show();
            } else {
                Log.d("Form progression", "Proceeding to preferences activity");
                vibrate();
                startActivity(new Intent(this, PreferencesActivity.class).putExtra("userName", userName));
                finish();
            }
        });
    }

    /** Make the phone vibrate
     */
    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(v != null && v.hasVibrator()) {
            v.vibrate(VibrationEffect.createOneShot(50,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }
}