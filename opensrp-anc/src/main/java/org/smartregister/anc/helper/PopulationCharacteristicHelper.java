package org.smartregister.anc.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.smartregister.anc.domain.Characteristic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndegwamartin on 13/08/2018.
 */
public class PopulationCharacteristicHelper {

    public List<Characteristic> populationCharacteristics;

    public List<Characteristic> getPopulationCharacteristics() {
        return populationCharacteristics;
    }

    public PopulationCharacteristicHelper() {


        String jsonFile = "";
        populationCharacteristics = (ArrayList<Characteristic>) fromJson(jsonFile,
                new TypeToken<ArrayList<String>>() {
                }.getType());
    }

    public static Object fromJson(String jsonString, Type type) {
        return new Gson().fromJson(jsonString, type);
    }


}
