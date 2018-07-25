package org.smartregister.anc.provider;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.anc.R;
import org.smartregister.anc.fragment.BaseRegisterFragment;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.util.Set;

import static org.smartregister.util.Utils.getName;

/**
 * Created by keyman on 26/06/2018.
 */

public class AdvancedSearchProvider extends RegisterProvider {

    public AdvancedSearchProvider(Context context, Set visibleColumns, View.OnClickListener onClickListener) {
        super(context, visibleColumns, onClickListener);

    }

    @Override
    protected void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        super.populatePatientColumn(pc, client, viewHolder);
    }
}
