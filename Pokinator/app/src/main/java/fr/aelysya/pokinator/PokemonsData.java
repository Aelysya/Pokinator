package fr.aelysya.pokinator;

import android.os.Environment;
import android.util.Log;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
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


    /**
     * All existing types in Poképedia's order
     */
    private static final List<String> TYPES = new ArrayList<>();
    static{
        TYPES.add("normal");
        TYPES.add("grass");
        TYPES.add("fire");
        TYPES.add("water");
        TYPES.add("electric");
        TYPES.add("ice");
        TYPES.add("fighting");
        TYPES.add("poison");
        TYPES.add("ground");
        TYPES.add("flying");
        TYPES.add("psychic");
        TYPES.add("bug");
        TYPES.add("rock");
        TYPES.add("ghost");
        TYPES.add("dragon");
        TYPES.add("dark");
        TYPES.add("steel");
        TYPES.add("fairy");
    }

    /**
     * Create a new instance of PokemonsData
     */
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

    /** Create a new instance of PokemonsData from copying another instance
     * @param copy The PokemonsData instance to copy
     */
    public PokemonsData(PokemonsData copy){
        NUMBERS = new ArrayList<>(copy.NUMBERS);
        NAMES = new ArrayList<>(copy.NAMES);
        GENERATIONS = new ArrayList<>(copy.GENERATIONS);
        FIRST_TYPES = new ArrayList<>(copy.FIRST_TYPES);
        SECONDARY_TYPES = new ArrayList<>(copy.SECONDARY_TYPES);
        WEAKNESSES = new ArrayList<>(copy.WEAKNESSES);
        STATS = new ArrayList<>(copy.STATS);
        SIZES = new ArrayList<>(copy.SIZES);
        WEIGHTS = new ArrayList<>(copy.WEIGHTS);
        LEGENDARY_STATUS = new ArrayList<>(copy.LEGENDARY_STATUS);
        EGG_STEPS = new ArrayList<>(copy.EGG_STEPS);
        CAPTURE_RATES = new ArrayList<>(copy.CAPTURE_RATES);
        MAX_EXPERIENCE = new ArrayList<>(copy.MAX_EXPERIENCE);
    }

    /** Load a CSV file
     * @param csvFile The CSV file to read in order to load data
     */
    public void loadCSV(InputStreamReader csvFile){
        try {
            //Using reflexivity to clear all lists before reading the csv
            Log.d("CSV loading", "Clearing the lists");
            for(Field f : this.getClass().getDeclaredFields()){
                if(!f.getName().equals("TYPES")) {
                    Object fieldVal = f.get(this);
                    Method method = Objects.requireNonNull(fieldVal).getClass().getMethod("clear");
                    method.invoke(fieldVal);
                }
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
                LEGENDARY_STATUS.add(nextLine[31].equals("1"));
                EGG_STEPS.add(Integer.parseInt(nextLine[32]));
                CAPTURE_RATES.add(Integer.parseInt(nextLine[33]));
                MAX_EXPERIENCE.add(Integer.parseInt(nextLine[34]));
            }
            Log.d("CSV loading", "CSV loading end");
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | IOException e) {
            Log.e("Exception occurred", e instanceof IOException ? "CSV file not found" : "Reflection didn't work :(");
        }
    }

    /** Remove a Pokémon from the dataset
     * @param index The Pokémon's index
     */
    private void removeLine(int index){
        try{
            for(Field f : this.getClass().getDeclaredFields()){
                if(!f.getName().equals("TYPES")){
                    Object fieldVal = f.get(this);
                    Method method = Objects.requireNonNull(fieldVal).getClass().getMethod("remove", int.class);
                    method.invoke(fieldVal, index);
                }
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e){
            Log.e("Exception occurred", "Reflection didn't work :(");
        }
    }

    /** Filter the dataset based on the 3 most preferred generations of the user, removes all pokémons from other generations
     * @param generations Array of three generations to keep in the dataset
     */
    public void filterGeneration(int[] generations){
        Log.d("Data filter", "Generation filter begin, number of pokémons before: " + FIRST_TYPES.size());
        //Not using a forEach to track the index automatically
        for(int i = 0; i < GENERATIONS.size(); ++i){
            //If the pokémon doesn't belong in one of the three generations, remove it from the dataset
            if(!(GENERATIONS.get(i) == generations[0] || GENERATIONS.get(i) == generations[1] || GENERATIONS.get(i) == generations[2])){
                removeLine(i);
                i--; //Rectify the iterator position
            }
        }
        Log.d("Data filter", "Generation filter end, number of pokémons left: " + FIRST_TYPES.size());
    }

    /** Filter the dataset based on the preferred type chosen by the user, removes all pokémons that don't possess it
     * @param type The type to keep in the dataset
     */
    public void filterPreferredType(String type){
        Log.d("Data filter", "Preferred type filter begin, number of pokémons before: " + FIRST_TYPES.size());
        //Not using a forEach to track the index automatically
        for(int i = 0; i < FIRST_TYPES.size(); ++i){
            //If the pokémon doesn't have the type in first or second, remove it from the dataset
            if(!(FIRST_TYPES.get(i).equals(type) || SECONDARY_TYPES.get(i).equals(type))){
                removeLine(i);
                i--; //Rectify the iterator position
            }
        }
        Log.d("Data filter", "Preferred type filter end, number of pokémons left: " + FIRST_TYPES.size());
    }

    /** Filter the dataset based on the disliked type chosen by the user, keeps pokémons that most resists it
     * @param type The type for which to find the Pokémons that are most resistant to it
     */
    public void filterDislikedType(String type){
        Log.d("Data filter", "Disliked type filter begin, number of pokémons before: " + WEAKNESSES.size());
        int typeIndex = getTypeIndex(type);
        float maxResistance = 4; //A lower number means a higher resistance
        for(Float[] f : WEAKNESSES){
            maxResistance = Math.min(maxResistance, f[typeIndex]);
        }

        //If some of the pokémons are immune to the chosen type, we keep both the 0.25 and 0.5 resistances too
        maxResistance = Math.max(maxResistance, 0.25f);

        if(maxResistance < 2){
            //Not using a forEach to track the index automatically
            for(int i = 0; i < WEAKNESSES.size(); ++i){
                //If the pokémon has a resistance of more than 2x the max of all the remaining ones, remove it from the dataset
                if(WEAKNESSES.get(i)[typeIndex] > 2 * maxResistance){
                    removeLine(i);
                    i--; //Rectify the iterator position
                }
            }
        }
        Log.d("Data filter", "Disliked type filter end, number of pokémons left: " + WEAKNESSES.size());
    }

    /** Get the position of a type in the TYPES ArrayList
     * @param type the type to find the index of
     * @return The index of the searched type
     */
    private int getTypeIndex(String type){
        return TYPES.indexOf(type);
    }

    /** Filter the dataset based on the pokémons HP, keeps the ones with the most or the least amount of HP
     * @param keepLow Whether to keep the pokémons with the most or the least amount of HP
     */
    public void filterHP(boolean keepLow){
        Log.d("Data filter", "HP stat filter begin, number of pokémons before: " + STATS.size());
        int highestHp = 0;
        int lowestHp = 1000;
        for(Integer[] i : STATS){
            highestHp = Math.max(highestHp, i[0]);
            lowestHp = Math.min(lowestHp, i[0]);
        }

        int thirdsThreshold = (highestHp - lowestHp) / 3;

        //Not using a forEach to track the index automatically
        for(int i = 0; i < STATS.size(); ++i){
            if((keepLow && STATS.get(i)[0] > highestHp - thirdsThreshold) || (!keepLow && STATS.get(i)[0] < lowestHp + thirdsThreshold)){
                removeLine(i);
                i--; //Rectify the iterator position
            }
        }
        Log.d("Data filter", "HP stat filter end, number of pokémons left: " + STATS.size());
    }

    /** Filter the dataset based on the pokémons attacks and defenses, keeps the ones that fit the user's choices
     * @param checkSpecial Whether to compare special or physical stats
     * @param prefAttack Whether to keep pokémons with a higher attack than defense or the contrary
     */
    public void filterAtkDef(boolean checkSpecial, boolean prefAttack){
        Log.d("Data filter", "Attacks and defenses stat filter begin, number of pokémons before: " + STATS.size());
        //Not using a forEach to track the index automatically
        if(STATS.size() >= 15){
            for(int i = 0; i < STATS.size(); ++i){
                if((checkSpecial && prefAttack && STATS.get(i)[4] > STATS.get(i)[3]) //special and prefer SP-ATK and (SP-DEF > SP-ATK)
                        || (checkSpecial && !prefAttack && STATS.get(i)[4] < STATS.get(i)[3]) //special and prefer SP-DEF and (SP-ATK > SP-DEF)
                        || (!checkSpecial && prefAttack && STATS.get(i)[2] > STATS.get(i)[1]) //physical and prefer ATK and (DEF > ATK)
                        || (!checkSpecial && !prefAttack && STATS.get(i)[2] < STATS.get(i)[1]) //physical and prefer DEF and (ATK > DEF)
                ){
                    removeLine(i);
                    i--; //Rectify the iterator position
                }
            }
        }
        Log.d("Data filter", "Attacks and defenses stat filter end, number of pokémons left: " + STATS.size());
    }

    /** Filter the dataset based on the pokémons speed, keeps the slowest or the fastest ones
     * @param keepSlow Whether to keep the slowest or the fastest pokémons
     */
    public void filterSpeed(boolean keepSlow){
        Log.d("Data filter", "Speed stat filter begin, number of pokémons before: " + STATS.size());
        int highestSpeed = 0;
        int lowestSpeed = 1000;
        for(Integer[] i : STATS){
            highestSpeed = Math.max(highestSpeed, i[5]);
            lowestSpeed = Math.min(lowestSpeed, i[5]);
        }

        int thirdsThreshold = (highestSpeed - lowestSpeed) / 3;

        //Not using a forEach to track the index automatically
        for(int i = 0; i < STATS.size(); ++i){
            if((keepSlow && STATS.get(i)[5] > highestSpeed - thirdsThreshold) || (!keepSlow && STATS.get(i)[5] < lowestSpeed + thirdsThreshold)){
                removeLine(i);
                i--; //Rectify the iterator position
            }
        }
        Log.d("Data filter", "Speed stat filter end, number of pokémons left: " + STATS.size());
    }

    /** Filter the dataset based on the pokémons' size, will keep only the pokémons that
     * share the height that's the closest to the parameter value
     * @param approximateValue Which pokémon size is wanted
     */
    public void filterSize(int approximateValue){
        Log.d("Data filter", "Size filter begin, number of pokémons before: " + SIZES.size());
        float tallest = 0;
        float smallest = 1000;
        for(Float f : SIZES){
            tallest = Math.max(tallest, f);
            smallest = Math.min(smallest, f);
        }

        //Get a representation of the height value from the seekbar value chosen by the user
        float targetValue = smallest + ((float) approximateValue / 100 * (tallest - smallest));

        float minimumHeightDifference = 1000;
        float foundHeight = 1000;

        //Find the closest height to the target
        for(Float f : SIZES){
            float tempDiff = Math.min(minimumHeightDifference, Math.abs(f - targetValue));
            //If the read value is closer to the target than the previous one
            if(tempDiff < minimumHeightDifference){
                minimumHeightDifference = tempDiff;
                foundHeight = f;
            }
        }

        //Not using a forEach to track the index automatically
        for(int i = 0; i < SIZES.size(); ++i){
            //If the pokémon's height is different from the target, remove it from the dataset
            if(SIZES.get(i) != foundHeight){
                removeLine(i);
                i--; //Rectify the iterator position
            }
        }
        Log.d("Data filter", "Size filter end, number of pokémons left: " + SIZES.size());
    }

    /** Filter the dataset based on the pokémons' weight, will keep only the pokémons that
     * share the weight that's the closest to the parameter value
     * @param approximateValue Which pokémon weight is wanted
     */
    public void filterWeight(int approximateValue){
        Log.d("Data filter", "Weight filter begin, number of pokémons before: " + WEIGHTS.size());
        float lightest = 0;
        float heaviest = 1000;
        for(Float f : WEIGHTS){
            lightest = Math.max(lightest, f);
            heaviest = Math.min(heaviest, f);
        }

        //Get a representation of the weight value from the seekbar value chosen by the user
        float targetValue = heaviest + ((float) approximateValue / 100 * (lightest - heaviest));

        float minimumWeightDifference = 10000;
        float foundWeight = 10000;

        //Find the closest weight to the target
        for(Float f : WEIGHTS){
            float tempDiff = Math.min(minimumWeightDifference, Math.abs(f - targetValue));
            //If the read value is closer to the target than the previous one
            if(tempDiff < minimumWeightDifference){
                minimumWeightDifference = tempDiff;
                foundWeight = f;
            }
        }

        //Not using a forEach to track the index automatically
        for(int i = 0; i < WEIGHTS.size(); ++i){
            //If the pokémon's weight is different from the target, remove it from the dataset
            if(WEIGHTS.get(i) != foundWeight){
                removeLine(i);
                i--; //Rectify the iterator position
            }
        }
        Log.d("Data filter", "Weight filter end, number of pokémons left: " + WEIGHTS.size());
    }

    /** Filter the dataset based on the pokémons' legendary status
     * @param keepLegendaries Whether to keep or remove the legendaries from the dataset
     */
    public void filterLegendaries(boolean keepLegendaries){
        Log.d("Data filter", "Legendaries filter begin, number of pokémons before: " + LEGENDARY_STATUS.size());
        //Not using a forEach to track the index automatically
        for(int i = 0; i < LEGENDARY_STATUS.size(); ++i){
            //If the pokémon's legendary status is different from the parameter, remove it from the dataset
            if(keepLegendaries != LEGENDARY_STATUS.get(i)){
                removeLine(i);
                i--; //Rectify the iterator position
            }
        }
        Log.d("Data filter", "Legendaries filter end, number of pokémons left: " + LEGENDARY_STATUS.size());
    }

    /** Filter the dataset based on the pokémons egg steps, keeps the slowest or the fastest to hatch
     * @param keepSlow Whether to keep the slowest or the fastest pokémons to hatch
     */
    public void filterEggSteps(boolean keepSlow){
        Log.d("Data filter", "Egg steps filter begin, number of pokémons before: " + EGG_STEPS.size());
        int mostSteps = 0;
        int leastSteps = 100000;
        for(Integer i : EGG_STEPS){
            mostSteps = Math.max(mostSteps, i);
            leastSteps = Math.min(leastSteps, i);
        }

        //Divide the difference by three to use it to remove pokémons in the excluded third later
        int thirdsThreshold = (mostSteps - leastSteps) / 3;

        //Not using a forEach to track the index automatically
        for(int i = 0; i < EGG_STEPS.size(); ++i){
            if((!keepSlow && EGG_STEPS.get(i) > mostSteps - thirdsThreshold) || (keepSlow && EGG_STEPS.get(i) < leastSteps + thirdsThreshold)){
                removeLine(i);
                i--; //Rectify the iterator position
            }
        }
        Log.d("Data filter", "Egg steps filter end, number of pokémons left: " + EGG_STEPS.size());
    }

    /** Filter the dataset based on the pokémons capture rate, keeps the hardest or the easiest to capture
     * @param keepHard Whether to keep the hardest or the easiest pokémons to capture
     */
    public void filterCaptureRate(boolean keepHard){
        Log.d("Data filter", "Capture rate filter begin, number of pokémons before: " + CAPTURE_RATES.size());
        int highestRate = 0;
        int lowestRate = 1000;
        for(Integer i : CAPTURE_RATES){
            highestRate = Math.max(highestRate, i);
            lowestRate = Math.min(lowestRate, i);
        }

        //Divide the difference by three to use it to remove pokémons in the excluded third later
        int thirdsThreshold = (highestRate - lowestRate) / 3;

        //Not using a forEach to track the index automatically
        for(int i = 0; i < CAPTURE_RATES.size(); ++i){
            if((keepHard && CAPTURE_RATES.get(i) > highestRate - thirdsThreshold) || (!keepHard && CAPTURE_RATES.get(i) < lowestRate + thirdsThreshold)){
                removeLine(i);
                i--; //Rectify the iterator position
            }
        }
        Log.d("Data filter", "Capture rate filter end, number of pokémons left: " + CAPTURE_RATES.size());
    }

    /** Filter the dataset based on the pokémons max experience, keeps the longest or the fastest to level up
     * @param keepLong Whether to keep the longest or the fastest pokémons to level up
     */
    public void filterMaxExperience(boolean keepLong){
        Log.d("Data filter", "Max experience filter begin, number of pokémons before: " + MAX_EXPERIENCE.size());
        int highestXp = 0;
        int lowestXp = 2000000;
        for(Integer i : MAX_EXPERIENCE){
            highestXp = Math.max(highestXp, i);
            lowestXp = Math.min(lowestXp, i);
        }

        if(lowestXp != highestXp){
            //Not using a forEach to track the index automatically
            for(int i = 0; i < MAX_EXPERIENCE.size(); ++i){
                if((keepLong && MAX_EXPERIENCE.get(i) == lowestXp) || (!keepLong && MAX_EXPERIENCE.get(i) == highestXp)){
                    removeLine(i);
                    i--; //Rectify the iterator position
                }
            }
        }
        Log.d("Data filter", "Max experience filter end, number of pokémons left: " + MAX_EXPERIENCE.size());
    }

    /**
     * Print the names of the pokémons that are still in the dataset in the log console
     */
    public void logData(){
        for(String s : NAMES){
            Log.d("Log Names left", s);
        }
    }

    /** Getter for pokémons' first types
     * @return The FIRST_TYPES list
     */
    public List<String> getFirstTypes(){
        return FIRST_TYPES;
    }

    /** Getter for pokémons' secondary types
     * @return The SECONDARY_TYPES list
     */
    public List<String> getSecondTypes(){
        return SECONDARY_TYPES;
    }

    /** Getter for the existing types
     * @return The TYPES list
     */
    public List<String> getTypes(){
        return TYPES;
    }

    /** Get the name of the last pokémon left in the dataset
     * @return The pokémon's name
     */
    public String getLastPokemonName(){
        return NAMES.get(0);
    }

    /** Get the number of the last pokémon left in the dataset
     * @return The pokémon's number
     */
    public int getLastPokemonNumber(){
        return NUMBERS.get(0);
    }

    /**
     * Save the last pokémon's information in a file
     */
    public void savePokemon(){
        Log.d("Saving capture information", "Information saved in \"pokinator_capture_result_" + NAMES.get(0) + "\".txt\" file");
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File out = new File(folder, "pokinator_capture_result_" + NAMES.get(0) + ".txt");
        try (FileOutputStream fos = new FileOutputStream(out)) {
            PrintStream ps = new PrintStream(fos);
            ps.println("Capture information");
            ps.println("Pokémon N°" + NUMBERS.get(0) + " - " + NAMES.get(0) + " (generation " + GENERATIONS.get(0) + ")");
            ps.println("Type: " + FIRST_TYPES.get(0) + " " + SECONDARY_TYPES.get(0));
            ps.println("Statistics: "
                    + STATS.get(0)[0] + " HP, "
                    + STATS.get(0)[1] + " ATK, "
                    + STATS.get(0)[2] + " DEF, "
                    + STATS.get(0)[3] + " SP-ATK, "
                    + STATS.get(0)[4] + " SP-DEF, "
                    + STATS.get(0)[5] + " SPEED");
            ps.println("Size: " + SIZES.get(0) + " m");
            ps.println("Weight: " + WEIGHTS.get(0) + " Kg");
            ps.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
