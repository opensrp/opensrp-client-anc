package org.smartregister.anc.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.smartregister.anc.R;
import org.smartregister.anc.adapter.ContactAdapter;
import org.smartregister.anc.domain.Contact;
import org.smartregister.view.activity.SecuredActivity;

import java.util.ArrayList;

public abstract class BaseContactActivity extends SecuredActivity {

    protected ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        contactAdapter = new ContactAdapter(this, new ArrayList<Contact>());

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(contactAdapter);

        createContacts();
    }

    protected abstract void createContacts();

}
