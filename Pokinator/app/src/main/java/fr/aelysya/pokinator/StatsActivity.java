package fr.aelysya.pokinator;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

import fr.aelysya.pokinator.utility.PokemonsData;

public class StatsActivity extends AppCompatActivity {

    private Toast currentToast;
    private ToggleButton attackDef;

    private CountDownTimer chrono;
    private long speedTestTimeLimit; //10s
    private int score; //10s

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

    /** Show the mini game corresponding to the attack or defense choice
     * @param view The ATTACK/DEFENSE button that has been pressed
     */
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

    /** Start the countdown of the speed clicker mini game
     * @param view The Pokéball's center button that has been pressed
     */
    public void startSpeedMiniGame(View view) {
        view.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentActivity frag = Objects.requireNonNull(fragmentManager.findFragmentByTag("statFragment")).requireActivity();

        TextView speedExplain= frag.findViewById(R.id.speedExplain);
        speedExplain.setVisibility(View.INVISIBLE);
        TextView timeLeft = frag.findViewById(R.id.timeLeft);
        TextView clickAmount = frag.findViewById(R.id.clickAmount);
        score = 0;
        speedTestTimeLimit = 10000;

        Button speedTestButton = frag.findViewById(R.id.speedButton);
        speedTestButton.setEnabled(true);
        speedTestButton.setOnClickListener(v -> {
            score++;
            clickAmount.setText(getString(R.string.speed_test_clicks_text, score));
        });

        chrono = new CountDownTimer(speedTestTimeLimit, 1000) {
            @Override
            public void onTick(long l) {
                speedTestTimeLimit = l;
                timeLeft.setText(getString(R.string.speed_test_time_text, (speedTestTimeLimit / 1000)));
            }

            @Override
            public void onFinish() {
                speedTestButton.setEnabled(false);
            }
        }.start();
    }

}
