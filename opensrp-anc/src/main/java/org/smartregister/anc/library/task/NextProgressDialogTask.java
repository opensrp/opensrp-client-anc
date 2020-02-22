//package org.smartregister.anc.library.task;
//
//import android.content.Context;
//
//import com.vijay.jsonwizard.fragments.JsonFormFragment;
//
//import org.smartregister.anc.library.activity.ContactJsonFormActivity;
//
//public class NextProgressDialogTask extends com.vijay.jsonwizard.task.NextProgressDialogTask {
//    public NextProgressDialogTask(Context context, JsonFormFragment jsonFormFragment) {
//        super(context, jsonFormFragment);
//    }
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//        ((ContactJsonFormActivity) getContext()).getSupportFragmentManager().beginTransaction()
//                .setCustomAnimations(com.vijay.jsonwizard.R.anim.enter_from_right, com.vijay.jsonwizard.R.anim.exit_to_left, com.vijay.jsonwizard.R.anim.enter_from_left,
//                        com.vijay.jsonwizard.R.anim.exit_to_right).replace(com.vijay.jsonwizard.R.id.container, getFormFragment()).addToBackStack(getFormFragment().getClass().getSimpleName())
//                .commitAllowingStateLoss(); // use https://stackoverflow.com/a/10261449/9782187
//        return null;
//    }
//}
