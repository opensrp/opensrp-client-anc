package org.smartregister.anc.library.viewstate;

import android.os.Parcel;

import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

/**
 * Created by ndegwamartin on 30/06/2018.
 */
public class ContactJsonFormFragmentViewState extends JsonFormFragmentViewState implements android.os.Parcelable {
    public static final Creator<ContactJsonFormFragmentViewState> CREATOR = new Creator<ContactJsonFormFragmentViewState>() {
        public ContactJsonFormFragmentViewState createFromParcel(Parcel source) {
            return new ContactJsonFormFragmentViewState(source);
        }

        public ContactJsonFormFragmentViewState[] newArray(int size) {
            return new ContactJsonFormFragmentViewState[size];
        }
    };

    public ContactJsonFormFragmentViewState() {
    }

    private ContactJsonFormFragmentViewState(Parcel in) {
        super(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }
}
