package fr.aelysya.pokinator;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import java.util.Random;

public class ResultActivity extends AppCompatActivity {

    public static PokemonsData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        data = new PokemonsData(DataHistory.getInstance().getHistory("physic"));

        TextView pokemonInfo = findViewById(R.id.pokemonInfo);
        ImageView pokemonImage = findViewById(R.id.pokemonImage);
        pokemonInfo.setText(getString(R.string.pokemon_info, data.getLastPokemonNumber(), data.getLastPokemonName()));
        Button backHome = findViewById(R.id.returnHomeButton);
        Button quit = findViewById(R.id.quitApplicationButton);
        Button save = findViewById(R.id.saveButton);

        backHome.setOnClickListener(view ->{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        save.setOnClickListener(view ->{
            data.savePokemon();
            Toast.makeText(this, R.string.pokemon_saved, Toast.LENGTH_LONG).show();
        });

        quit.setOnClickListener(view -> finish());


        Glide.with(ResultActivity.this).load(constructURL(data.getLastPokemonNumber())).into(pokemonImage);
    }

    @Override
    public void onBackPressed(){} //Set method to do nothing to prevent the user from going back when they are in the final activity

    /** Compute the URL to find the final pokémon image and show it
     * @param number The pokémon's pokédex number
     * @return The URL to find the pokémon's sprite
     */
    private String constructURL(int number){
        StringBuilder url = new StringBuilder("https://projectpokemon.org/images/sprites-models/sv-sprites-home/");
        if(number < 1000){
            if (number < 100){
                url.append("0");
            }
            url.append("0");
        }
        url.append(number);
        url.append(checkIrregularPokemons(data.getLastPokemonName()));
        url.append(".png");
        return url.toString();
    }

    /** Completes the URL for irregular pokémons like those with multiple forms
     * @param name The pokémon's name
     * @return An URL optional part if the pokémon is irregular
     */
    private String checkIrregularPokemons(String name){
        String optionalPart = ""; //Doesn't change if the pokémon is regular

        if(name.contains("Mega ") ||
                name.contains("Alolan ") ||
                name.contains("Galarian ") ||
                name.contains("Hisuian ") ||
                name.contains("Paldean ") ||
                name.contains("Origin ") ||         //Giratina, Dialga and Palkia
                name.contains("Sky ") ||            //Shaymin
                name.contains("Sunny ") ||          //Castform
                name.contains("Primal ") ||         //Kyogre and Groudon
                name.contains("Attack ") ||         //Deoxys
                name.contains("Sandy ") ||          //Wormadam
                name.contains("Heat ") ||           //Rotom
                name.contains("Therian ") ||        //Tornadus, Thundurus, Landorus
                name.contains("White ") ||          //Kyurem
                name.contains("Pirouette ") ||      //Meloetta
                name.contains("Unbound ") ||        //Hoopa
                name.contains("Pom-Pom ") ||        //Oricorio
                name.contains("Dusk Mane ") ||      //Necrozma
                name.contains("Female ") ||         //Indeedee, Basculegion and Oinkologne
                name.contains("Crowned ") ||        //Zacian and Zamazenta
                name.contains("Ice Rider ") ||      //Calyrex
                name.equals("Hero Palafin")         //Palafin
        ){
            optionalPart = "_01";
        }

        if(name.endsWith(" Y") ||                   //Mega Charizard Y and Mega Mewtwo Y
                name.contains("Blaze Breed ") ||    //Paldean Tauros
                name.equals("Galarian Meowth") ||
                name.equals("Galarian Slowbro") ||
                name.contains("Defense ") ||        //Deoxys
                name.contains("Rainy ") ||          //Castform
                name.contains("Trash ") ||          //Wormadam
                name.contains("Wash ") ||           //Rotom
                name.contains("Black ") ||          //Kyurem
                name.contains("Ash-") ||            //Greninja
                name.contains("Pa'u") ||            //Oricorio
                name.contains("Dawn Wings ") ||     //Necrozma
                name.contains("Shadow Rider ")      //Calyrex
        ){
            optionalPart = "_02";
        }

        if(name.contains("Aqua Breed ") ||          //Paldean Tauros
                name.contains("Snowy ") ||          //Castform
                name.contains("Speed ") ||          //Deoxys
                name.contains("Frost ") ||          //Rotom
                name.contains("Sensu ") ||          //Oricorio
                name.equals("Ultra Necrozma")       //Necrozma
        ){
            optionalPart = "_03";
        }

        if(name.contains("Fan ")){                  //Rotom
            optionalPart = "_04";
        }

        if(name.contains("Mow ")){                  //Rotom
            optionalPart = "_05";
        }

        //If Unown if found, return a random form
        if(name.equals("Unown")){
            Random r = new Random();
            int randomNumber = r.nextInt(28);
            if(randomNumber != 0){ //If 0, keep the A form which has no additional part in its URL
                optionalPart = "_" + (randomNumber < 10 ? "0" + randomNumber : randomNumber);
            }
        }
        return optionalPart;
    }
}
