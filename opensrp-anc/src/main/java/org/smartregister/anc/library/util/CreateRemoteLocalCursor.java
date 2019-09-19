package org.smartregister.anc.library.util;

import android.database.Cursor;

public class CreateRemoteLocalCursor {
    private String id;
    private String relationalId;
    private String firstName;
    private String lastName;
    private String dob;
    private String ancId;
    private String phoneNumber;
    private String altName;

    public CreateRemoteLocalCursor(Cursor cursor, boolean isRemote) {
        if (isRemote) {
            id = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.ID_LOWER_CASE));
        } else {
            id = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.BASE_ENTITY_ID));
        }
        relationalId = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.RELATIONAL_ID));
        firstName = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.FIRST_NAME));
        lastName = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.LAST_NAME));
        dob = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.DOB));
        ancId = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.ANC_ID));
        phoneNumber = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.PHONE_NUMBER));
        altName = cursor.getString(cursor.getColumnIndex(DBConstants.KEY.ALT_NAME));
    }

    public String getId() {
        return id;
    }

    public String getRelationalId() {
        return relationalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDob() {
        return dob;
    }

    public String getAncId() {
        return ancId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAltName() {
        return altName;
    }
}
