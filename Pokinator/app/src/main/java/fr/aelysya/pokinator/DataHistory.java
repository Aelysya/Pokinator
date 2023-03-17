package fr.aelysya.pokinator;

import java.io.InputStreamReader;

public class DataHistory {

    private static DataHistory instance;

    public static PokemonsData baseData;
    public static PokemonsData preferencesData;
    public static PokemonsData typesData;
    public static PokemonsData statsData;
    public static PokemonsData persoData;
    public static PokemonsData physicData;

    private DataHistory(){}

    public static DataHistory getInstance(){
        return instance == null ? new DataHistory() : instance;
    }

    public void setHistory(String activityName, PokemonsData newData){
        switch (activityName){
            case "main": baseData = new PokemonsData(newData); break;
            case "preferences": preferencesData = new PokemonsData(newData); break;
            case "types": typesData = new PokemonsData(newData); break;
            case "stats": statsData = new PokemonsData(newData); break;
            case "perso": persoData = new PokemonsData(newData); break;
            case "physic": physicData = new PokemonsData(newData); break;
        }
    }

    public PokemonsData getHistory(String activityName){
        PokemonsData res;

        switch (activityName){
            case "preferences": res = new PokemonsData(preferencesData); break;
            case "types": res = new PokemonsData(typesData); break;
            case "stats": res = new PokemonsData(statsData); break;
            case "perso": res = new PokemonsData(persoData); break;
            case "physic": res = new PokemonsData(physicData); break;
            case "main": res = new PokemonsData(baseData); break;
            default: res = new PokemonsData();
        }

        return res;
    }
}
