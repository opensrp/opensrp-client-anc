package org.smartregister.anc.helper;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.domain.Characteristic;
import org.smartregister.util.AssetHandler;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by ndegwamartin on 13/08/2018.
 */
public class CharacteristicsHelper {

    private static final String TAG = CharacteristicsHelper.class.getCanonicalName();

    public List<Characteristic> populationCharacteristics;

    private static final Type CHARACTERISTIC_TYPE = new TypeToken<List<Characteristic>>() {
    }.getType();

    public List<Characteristic> getPopulationCharacteristics() {
        return populationCharacteristics;
    }


    public CharacteristicsHelper() {

        try {
            Gson gson = new Gson();
            String jsonstring = AssetHandler.readFileFromAssetsFolder("json.characteristics/population_characteristics.json", AncApplication.getInstance().getApplicationContext());

            populationCharacteristics = gson.fromJson(jsonstring, CHARACTERISTIC_TYPE); // contains the whole reviews list

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

}
