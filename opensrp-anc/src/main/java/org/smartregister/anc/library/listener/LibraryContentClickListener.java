package org.smartregister.anc.library.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.activity.LibraryContentActivity;
import org.smartregister.anc.library.activity.LibraryViewActivity;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.view.customcontrols.CustomFontTextView;

public class LibraryContentClickListener implements View.OnClickListener {
    private Activity activity;

    public LibraryContentClickListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        if (view != null) {
            CustomFontTextView header = view.findViewById(R.id.library_text_header);
            String headerText = header.getText().toString();

            TextView contentFileView = view.findViewById(R.id.library_content_file);
            String contentFile = contentFileView.getText().toString();

            if (activity != null) {
                ((BaseHomeRegisterActivity) activity).setLibrary(true);

                Intent intent = new Intent(activity, LibraryViewActivity.class);
                intent.putExtra("contentHeader", headerText);
                intent.putExtra("contentFile", contentFile);
                activity.startActivity(intent);
            }
        }
    }
}
