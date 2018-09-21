package org.smartregister.anc.helper;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.domain.Characteristic;
import org.smartregister.domain.Setting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndegwamartin on 13/08/2018.
 */
public class CharacteristicsHelper {

    private static final String TAG = CharacteristicsHelper.class.getCanonicalName();

    public static List<Characteristic> characteristics;

    private static final Type CHARACTERISTIC_TYPE = new TypeToken<List<Characteristic>>() {
    }.getType();

    public CharacteristicsHelper(String key) {


        characteristics = CharacteristicsHelper.fetchCharacteristicsByTypeKey(key);


    }

    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

    public static List<Characteristic> fetchCharacteristicsByTypeKey(String typeKey) {
        try {

            Gson gson = new Gson();

            Setting characteristic = AncApplication.getInstance().getContext().allSettings().getSetting(typeKey);

            String jsonstring = characteristic.getValue().toString();

            return gson.fromJson(jsonstring, CHARACTERISTIC_TYPE); // contains the whole reviews list

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return new ArrayList<>();
        }
    }

}
