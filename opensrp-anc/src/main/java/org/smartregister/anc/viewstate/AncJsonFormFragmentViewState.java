package org.smartregister.anc.viewstate;

import android.os.Parcel;

import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

/**
 * Created by ndegwamartin on 30/06/2018.
 */
public class AncJsonFormFragmentViewState extends JsonFormFragmentViewState implements android.os.Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public AncJsonFormFragmentViewState() {
    }

    private AncJsonFormFragmentViewState(Parcel in) {
        super(in);
    }

    public static final Creator<AncJsonFormFragmentViewState> CREATOR = new Creator<AncJsonFormFragmentViewState>() {
        public AncJsonFormFragmentViewState createFromParcel(
                Parcel source) {
            return new AncJsonFormFragmentViewState(source);
        }

        public AncJsonFormFragmentViewState[] newArray(
                int size) {
            return new AncJsonFormFragmentViewState[size];
        }
    };
}
