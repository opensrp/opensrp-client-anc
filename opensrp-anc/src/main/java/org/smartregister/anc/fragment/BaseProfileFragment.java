package org.smartregister.anc.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;

import org.smartregister.anc.util.Utils;

/**
 * Created by ndegwamartin on 12/07/2018.
 */
public abstract class BaseProfileFragment extends Fragment implements View.OnTouchListener {

    private static final String TAG = BaseProfileFragment.class.getCanonicalName();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int action = event.getAction();

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Utils.showToast(getActivity(), "Action was DOWN");
                return true;
            case (MotionEvent.ACTION_UP):
                Utils.showToast(getActivity(), "Action was UP");
                return true;
            default:
                return true;
        }
    }
}