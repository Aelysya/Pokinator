package fr.aelysya.pokinator;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import org.w3c.dom.Text;

import java.util.Objects;

import fr.aelysya.pokinator.utility.PokemonsData;

public class StatsActivity extends AppCompatActivity {

    private Toast currentToast;
    private ToggleButton attackDef;

    private CountDownTimer speedChrono;
    private CountDownTimer hpChrono;
    private long speedTestTimeLimit;
    private long hpTestTimeLimit;
    private int speedTestScore;
    private int seekbarAdd;

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
                    .replace(R.id.statFragment, HpTestFragment.class, null, "hpFragment")
                    .commit();
        }
        else{
            Log.d("Mini game", "Attack chosen, showing SPEED mini game");
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.statFragment, SpeedTestFragment.class, null, "speedFragment")
                    .commit();
        }
    }

    /** Start the hp SeekBar mini game
     * @param view The mini game start button that has been pressed
     */
    public void startHpMiniGame(View view) {
        view.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentActivity frag = Objects.requireNonNull(fragmentManager.findFragmentByTag("hpFragment")).requireActivity();
        SeekBar hpBar = frag.findViewById(R.id.hpBar);
        TextView result = frag.findViewById(R.id.hpTestResult);
        Button stopButton = frag.findViewById(R.id.stopButton);
        hpBar.setEnabled(false);
        hpBar.setProgress(0);

        int randomImage = (int)(Math.random() * 4);
        switch (randomImage){
            case 0: hpBar.setThumb(ContextCompat.getDrawable(this, R.drawable.hp_test_thumb_poke)); break;
            case 1: hpBar.setThumb(ContextCompat.getDrawable(this, R.drawable.hp_test_thumb_super)); break;
            case 2: hpBar.setThumb(ContextCompat.getDrawable(this, R.drawable.hp_test_thumb_hyper)); break;
            case 3: hpBar.setThumb(ContextCompat.getDrawable(this, R.drawable.hp_test_thumb_master)); break;
        }

        hpTestTimeLimit = 30000; //30s
        seekbarAdd = 1;

        stopButton.setOnClickListener(v-> {
            stopButton.setVisibility(View.GONE);
            hpChrono.cancel();
            if(hpBar.getProgress() >= 70 && hpBar.getProgress() <= 79){
                result.setText(R.string.hp_test_success);
                result.setTextColor(ContextCompat.getColor(this, R.color.green));
            } else {
                result.setText(R.string.hp_test_fail);
                result.setTextColor(ContextCompat.getColor(this, R.color.red));
            }
        });

        hpChrono = new CountDownTimer(hpTestTimeLimit, 7) {
            @Override
            public void onTick(long l) {
                hpTestTimeLimit = l;
                hpBar.setProgress(hpBar.getProgress() + seekbarAdd);
                if(hpBar.getProgress() == 100){
                    seekbarAdd = -1;
                }
                if(hpBar.getProgress() == 0){
                    seekbarAdd = 1;
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    /** Start the countdown of the speed clicker mini game
     * @param view The Pokéball's center button that has been pressed
     */
    public void startSpeedMiniGame(View view) {
        view.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentActivity frag = Objects.requireNonNull(fragmentManager.findFragmentByTag("speedFragment")).requireActivity();

        TextView speedExplain= frag.findViewById(R.id.speedExplain);
        speedExplain.setVisibility(View.INVISIBLE);
        TextView timeLeft = frag.findViewById(R.id.timeLeft);
        TextView clickAmount = frag.findViewById(R.id.clickAmount);
        speedTestScore = 0;
        speedTestTimeLimit = 10000;

        Button speedTestButton = frag.findViewById(R.id.speedButton);
        speedTestButton.setEnabled(true);
        speedTestButton.setOnClickListener(v -> {
            speedTestScore++;
            clickAmount.setText(getString(R.string.speed_test_clicks_text, speedTestScore));
        });

        speedChrono = new CountDownTimer(speedTestTimeLimit, 1000) {
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
