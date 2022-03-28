package org.smartregister.anc.library.util;

import android.database.Cursor;

public class CreateRemoteLocalCursorUtils {
    private String id;
    private String relationalId;
    private String firstName;
    private String lastName;
    private String dob;
    private String ancId;
    private String phoneNumber;
    private String altName;
    private String edd;
    private String nextContact;
    private String nextContactDate;

    public CreateRemoteLocalCursorUtils(Cursor cursor, boolean isRemote) {
        if (isRemote) {
            id = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.ID_LOWER_CASE));
        } else {
            id = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID));
        }
        relationalId = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.RELATIONAL_ID));
        firstName = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.FIRST_NAME));
        lastName = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.LAST_NAME));
        dob = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.DOB));
        edd = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.EDD));
        ancId = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.ANC_ID));
        phoneNumber = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.PHONE_NUMBER));
        altName = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.ALT_NAME));
        nextContact = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.NEXT_CONTACT));
        nextContactDate = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE));
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
    public String getEdd() {
        return edd;
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

    public String getNextContact() {
        return nextContact;
    }

    public String getNextContactDate () {
        return  nextContactDate;
    }
}
