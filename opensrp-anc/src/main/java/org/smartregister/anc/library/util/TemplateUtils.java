package org.smartregister.anc.library.util;

import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import timber.log.Timber;

public class TemplateUtils {

    public static JSONObject getTemplateAsJson(@NonNull Context context, @NonNull String templateName) {
        InputStream inputStream;
        StringBuilder stringBuilder;

        try {
            String locale = context.getResources().getConfiguration().locale.getLanguage();
            locale = locale.equalsIgnoreCase("en") ? "" : "-" + locale;

            inputStream = context.getAssets().open("template" + locale + "/" + templateName + ".json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String jsonString;
            stringBuilder = new StringBuilder();

            while ((jsonString = reader.readLine()) != null) {
                stringBuilder.append(jsonString);
            }

            inputStream.close();

            return new JSONObject(stringBuilder.toString());
        } catch (IOException | JSONException e) {
            Timber.e(e);
        }
        return null;
    }
}
