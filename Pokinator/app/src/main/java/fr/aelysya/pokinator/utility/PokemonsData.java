package fr.aelysya.pokinator.utility;

import android.util.Log;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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


            //Debugging Part, to remove later
            filterLegendaries(false);
            filterPreferredType("ghost");
            filterGeneration(new int[]{1, 6, 9});
            filterDislikedType("grass");
            filterAtkDef(false, false);
            filterSpeed(true);
            filterCaptureRate(false);
            filterEggSteps(true);
            for(String s : NAMES){
                Log.d("DEBUG", s);
            }


        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | IOException e) {
            Log.d("Exception occurred", e instanceof IOException ? "CSV file not found" : "Reflection didn't work :(");
        }
    }

    public void removeLine(int index){
        try{
            for(Field f : this.getClass().getDeclaredFields()){
                if(!f.getName().equals("TYPES")){
                    Object fieldVal = f.get(this);
                    Class[] arg = new Class[1];
                    arg[0] = int.class;
                    Method method = Objects.requireNonNull(fieldVal).getClass().getMethod("remove", arg);
                    method.invoke(fieldVal, index);
                }
            }
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e){
            Log.d("Exception occurred", "Reflection didn't work :(");
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

    /** Filter the dataset based on the preferred type chosen by the user, removes all pokémons that doesn't possess it
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

    private int getTypeIndex(String type){
        return TYPES.indexOf(type);
    }

    /** Filter the dataset based on the pokémons attacks and defenses, keeps the ones that fit the user's choices
     * @param checkSpecial Whether to compare special or physical stats
     * @param prefAttack Whether to keep pokémons with a higher attack than defense or the contrary
     */
    private void filterAtkDef(boolean checkSpecial, boolean prefAttack){
        Log.d("Data filter", "Attacks and defenses stat filter begin, number of pokémons before: " + STATS.size());

        //Not using a forEach to track the index automatically
        for(int i = 0; i < STATS.size(); ++i){
            if((checkSpecial && prefAttack && (STATS.get(i)[4] > STATS.get(i)[3])) //special and prefer SP-ATK and (SP-DEF > SP-ATK)
            || (checkSpecial && !prefAttack && (STATS.get(i)[4] < STATS.get(i)[3])) //special and prefer SP-DEF and (SP-ATK > SP-DEF)
            || (!checkSpecial && prefAttack && (STATS.get(i)[2] > STATS.get(i)[1])) //physical and prefer ATK and (DEF > ATK)
            || (!checkSpecial && !prefAttack && (STATS.get(i)[2] < STATS.get(i)[1])) //physical and prefer DEF and (ATK > DEF)
            ){
                removeLine(i);
                i--; //Rectify the iterator position
            }
        }
        Log.d("Data filter", "Attacks and defenses stat filter end, number of pokémons left: " + STATS.size());
    }

    /** Filter the dataset based on the pokémons speed, keeps the slowest or the fastest ones
     * @param keepSlow Whether to keep the slowest or the fastest pokémons
     */
    private void filterSpeed(boolean keepSlow){
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
    private void filterEggSteps(boolean keepSlow){
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
            if((keepSlow && EGG_STEPS.get(i) > mostSteps - thirdsThreshold) || (!keepSlow && EGG_STEPS.get(i) < leastSteps + thirdsThreshold)){
                removeLine(i);
                i--; //Rectify the iterator position
            }
        }
        Log.d("Data filter", "Egg steps filter end, number of pokémons left: " + EGG_STEPS.size());
    }

    /** Filter the dataset based on the pokémons capture rate, keeps the hardest or the easiest to capture
     * @param keepHard Whether to keep the hardest or the easiest pokémons to capture
     */
    private void filterCaptureRate(boolean keepHard){
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
}
