package org.smartregister.anc.util;

import android.database.Cursor;
import android.support.annotation.NonNull;

import org.smartregister.anc.repository.AncRepository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-07-04
 */

public class Utils {


    public static ArrayList<Object> runQuery(@NonNull String query) {
        Cursor cursor = ((AncRepository) ((DrishtiApplication) DrishtiApplication.getInstance()).getRepository()).getReadableDatabase().rawQuery(query, null);

        ArrayList<Object> rows = new ArrayList<>();
        if (cursor != null) {
            int cols = cursor.getColumnCount();

            while (cursor.moveToNext()) {
                Object[] col = new Object[cols];

                for (int i = 0; i < cols; i++) {
                    int type = cursor.getType(i);
                    Object cellValue = null;

                    if (type == Cursor.FIELD_TYPE_FLOAT) {
                        cellValue = (Float) cursor.getFloat(i);
                    } else if (type == Cursor.FIELD_TYPE_INTEGER) {
                        cellValue = (Integer) cursor.getInt(i);
                    } else if (type == Cursor.FIELD_TYPE_STRING) {
                        cellValue = (String) cursor.getString(i);
                    }

                    if (cols > 1) {
                        col[i] = cellValue;
                    } else {
                        rows.add(cellValue);
                    }
                }

                if (cols > 1) {
                    rows.add(col);
                }
            }
        }

        return rows;
    }
}
