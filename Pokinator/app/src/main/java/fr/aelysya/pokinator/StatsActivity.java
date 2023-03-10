package fr.aelysya.pokinator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import fr.aelysya.pokinator.utility.PokemonsData;

public class StatsActivity extends AppCompatActivity {

    private Toast currentToast;
    private ToggleButton attackDef;

    public static PokemonsData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        currentToast = Toast.makeText(getApplicationContext(), "Empty Toast", Toast.LENGTH_SHORT);
        attackDef = findViewById(R.id.statToggleAttackDef);

        Button previousStep = findViewById(R.id.previousStepStats);
        Button nextStep = findViewById(R.id.nextStepStats);

        previousStep.setOnClickListener(view -> {
            Log.d("Form progression", "Going back to types activity");
            Intent intent = new Intent(this, TypesActivity.class);
            startActivity(intent);
        });

        nextStep.setOnClickListener(view -> {
            //Filter the data
            Log.d("Form progression", "Proceeding to perso activity");
            Intent intent = new Intent(this, PersoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        //Set the data to the ones stocked in previous activity in case of the user goes back in the app
        data = new PokemonsData(TypesActivity.data);
    }

    public void showMiniGame(View view){
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(attackDef.isChecked()){
            Log.d("Mini game", "Defense chosen, showing HP mini game");
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.statFragment, HpTestFragment.class, null, "statFragment")
                    .commit();
        }
        else{
            Log.d("Mini game", "Attack chosen, showing SPEED mini game");
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.statFragment, SpeedTestFragment.class, null, "statFragment")
                    .commit();
        }
    }

    /*
    public void startSpeedMiniGameThread(View view) {

        Thread thread;

        FragmentManager fragmentManager = getSupportFragmentManager();



        Runnable t = () -> {

            SeekBar seekBar = fragmentManager.findFragmentByTag("Squidward").getActivity().findViewById(R.id.seekBar);;
            boolean dir = true;

            for (int i = 0; i < 250; i++) {

                if(seekBar.getProgress() == 100){

                    dir = false;
                }
                else if(seekBar.getProgress() == 0){

                    dir = true;
                }

                if (dir) {

                    seekBar.setProgress(seekBar.getProgress() + 1);
                } else {

                    seekBar.setProgress(seekBar.getProgress() - 1);
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        };

        thread = new Thread(t);
        thread.start();

        try {
            thread.join(1);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

     */

}
