package fr.aelysya.pokinator.utility;

import static fr.aelysya.pokinator.MainActivity.APP_TAG;

import android.util.Log;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PokemonsData {

    /**
     * Pokédex numbers of the pokémons
     */
    private final List<Integer> NUMBERS; //Some pokémons share the same number so we cant just use the list index to find them in next lists
    /**
     * Names of the pokémons
     */
    private final List<String> NAMES;
    /**
     * Generation numbers of the pokémons
     */
    private final List<Integer> GENERATIONS;
    /**
     * First types of the pokémons
     */
    private final List<String> FIRST_TYPES;
    /**
     * Second types of the pokémons
     */
    private final List<String> SECONDARY_TYPES; //Pokémons without a secondary type are listed as empty String
    /**
     * Weaknesses of the pokémons
     */
    private final List<Float[]> WEAKNESSES; //Weaknesses are listed in Poképedia website's order
    /**
     * Stats of the pokémons
     */
    private final List<Integer[]> STATS; //Stats are listed as HP, ATK, DEF, SP-ATK, SP-DEF, SPEED
    /**
     * Sizes in meters of the pokémons
     */
    private final List<Float> SIZES;
    /**
     * Weights in kilograms of the pokémons
     */
    private final List<Float> WEIGHTS;
    /**
     * Legendary status of the pokémons
     */
    private final List<Boolean> LEGENDARY_STATUS;
    /**
     * Average number of steps necessary to hatch an egg of the pokémons
     */
    private final List<Integer> EGG_STEPS; //Even though some pokémon can't lay eggs, egg steps are specified based on Poképedia's data
    /**
     * Capture rates of the pokémons
     */
    private final List<Integer> CAPTURE_RATES;
    /**
     * Level 100 total experience of the pokémons
     */
    private final List<Integer> MAX_EXPERIENCE;

    public PokemonsData(){
        NUMBERS = new ArrayList<>();
        NAMES = new ArrayList<>();
        GENERATIONS = new ArrayList<>();
        FIRST_TYPES = new ArrayList<>();
        SECONDARY_TYPES = new ArrayList<>();
        WEAKNESSES = new ArrayList<>();
        STATS = new ArrayList<>();
        SIZES = new ArrayList<>();
        WEIGHTS = new ArrayList<>();
        LEGENDARY_STATUS = new ArrayList<>();
        EGG_STEPS = new ArrayList<>();
        CAPTURE_RATES = new ArrayList<>();
        MAX_EXPERIENCE = new ArrayList<>();
    }

    /** Load a CSV file
     * @param csvFile The CSV file to read in order to load data
     */
    public void loadCSV(InputStreamReader csvFile){
        try {
            //Using reflexivity to clear all lists before reading the csv
            Log.d("CSV loading", "Clearing the lists");
            for(Field f : this.getClass().getDeclaredFields()){
                Object fieldVal = f.get(this);
                Method method = Objects.requireNonNull(fieldVal).getClass().getMethod("clear");
                method.invoke(fieldVal);
            }

            CSVReader reader = new CSVReader(csvFile);
            String[] nextLine;
            reader.readNext(); //Skip first line
            Log.d("CSV loading", "CSV loading begin");
            while ((nextLine = reader.readNext()) != null) {
                NUMBERS.add(Integer.parseInt(nextLine[0]));
                NAMES.add(nextLine[1]);
                GENERATIONS.add(Integer.parseInt(nextLine[2]));
                FIRST_TYPES.add(nextLine[3]);
                SECONDARY_TYPES.add(nextLine[4]);

                Float[] tempWeaknesses = new Float[18];
                for(int i = 0; i < 18; ++i){
                    tempWeaknesses[i] = Float.parseFloat(nextLine[i + 5]);
                }
                WEAKNESSES.add(tempWeaknesses);

                Integer[] tempStats = new Integer[6];
                for(int i = 0; i < 6; ++i){
                    tempStats[i] = Integer.parseInt(nextLine[i + 23]);
                }
                STATS.add(tempStats);
                SIZES.add(Float.parseFloat(nextLine[29]));
                WEIGHTS.add(Float.parseFloat(nextLine[30]));
                LEGENDARY_STATUS.add(Boolean.parseBoolean(nextLine[31]));
                EGG_STEPS.add(Integer.parseInt(nextLine[32]));
                CAPTURE_RATES.add(Integer.parseInt(nextLine[33]));
                MAX_EXPERIENCE.add(Integer.parseInt(nextLine[34]));
            }
            Log.d("CSV loading", "CSV loading end");

            //Debug part to ensure the csv has correctly been loaded
            /*
            Log.d("CSV DEBUG number", NUMBERS.get(1000).toString());
            Log.d("CSV DEBUG name", NAMES.get(1000));
            Log.d("CSV DEBUG gen", GENERATIONS.get(1000).toString());
            Log.d("CSV DEBUG fType", FIRST_TYPES.get(1000));
            Log.d("CSV DEBUG sType", SECONDARY_TYPES.get(1000));
            for(int i = 0; i < 18; ++i){
                Log.d("CSV DEBUG weaks", WEAKNESSES.get(1000)[i].toString());
            }
            for(int i = 0; i < 6; ++i){
                Log.d("CSV DEBUG stats", STATS.get(1000)[i].toString());
            }
            Log.d("CSV DEBUG size", SIZES.get(1000).toString());
            Log.d("CSV DEBUG weight", WEIGHTS.get(1000).toString());
            Log.d("CSV DEBUG legendary", LEGENDARY_STATUS.get(1000).toString());
            Log.d("CSV DEBUG egg", EGG_STEPS.get(1000).toString());
            Log.d("CSV DEBUG capRate", CAPTURE_RATES.get(1000).toString());
            Log.d("CSV DEBUG maxExp", MAX_EXPERIENCE.get(1000).toString());
            */
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | IOException e) {
            Log.d("Exception occurred", e instanceof IOException ? "CSV file not found" : "Reflexivity didn't work :(");
        }
    }
}