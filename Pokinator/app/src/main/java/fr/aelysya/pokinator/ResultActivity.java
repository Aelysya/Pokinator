package fr.aelysya.pokinator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ResultActivity extends AppCompatActivity {

    private Toast currentToast;
    public static PokemonsData data;
    private ImageView pokemonImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        currentToast = Toast.makeText(getApplicationContext(), "Empty toast", Toast.LENGTH_SHORT);
        data = new PokemonsData(PhysicActivity.data);

        TextView pokemonInfo = findViewById(R.id.pokemonInfo);
        pokemonImage = findViewById(R.id.pokemonImage);
        pokemonInfo.setText(getString(R.string.pokemon_info, data.getLastPokemonNumber(), data.getLastPokemonName()));

        new PokeRequest(data.getLastPokemonName()).execute();
    }

    class PokeRequest extends AsyncTask<Void,Integer,Void> {
        private final String NAME;
        private String imageURL;

        public PokeRequest(String name) {
            this.NAME = name;
            imageURL = null;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect("https://bulbapedia.bulbagarden.net/wiki/" + NAME + "_(Pokémon)").get();
                Elements images = doc.select("img");
                if(MainActivity.shinyCharmEnabled){
                    for(Element e : images){
                        if(e.attr("src").contains("HOME") && e.attr("src").endsWith("_s.png")){
                            imageURL = e.attr("src");
                            Log.d("Found image", e.attr("alt") + " " + e.attr("src"));
                        }
                    }
                } else {
                    for(Element e : images){
                        if(e.attr("src").contains("HOME") && !e.attr("src").endsWith("_s.png")){
                            imageURL = e.attr("src");
                            Log.d("Found image", e.attr("alt") + " " + e.attr("src"));
                        }
                    }
                }
            } catch ( IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Associate image
            Glide.with(ResultActivity.this).load("https:" + imageURL).into(pokemonImage);
        }
    }
}
