package org.smartregister.anc.activity;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.json.JSONArray;
import org.json.JSONException;
import org.smartregister.anc.R;
import org.smartregister.anc.adapter.ContactScheduleAdapter;
import org.smartregister.anc.adapter.PreviousContactsAdapter;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.PreviousContacts;
import org.smartregister.anc.model.ContactSummaryModel;
import org.smartregister.anc.presenter.PreviousContactsPresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.anc.util.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PreviousContactsActivity extends AppCompatActivity implements PreviousContacts.View {

    private HashMap<String, String> clientDetails;
    protected PreviousContacts.Presenter mProfilePresenter;
    protected ActionBar actionBar;
    private TextView deliveryDate;
    private RecyclerView previousContacts;
    RecyclerView contactSchedule;
    private JsonFormUtils formUtils = new JsonFormUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getViewLayoutId());
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_back));
            actionBar.setTitle(getResources().getString(R.string.previous_contacts_header));
        }

        String baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        clientDetails = (HashMap<String, String>) getIntent().getSerializableExtra(Constants.INTENT_KEY.CLIENT_MAP);
        String contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstants.KEY.NEXT_CONTACT)));
        mProfilePresenter = new PreviousContactsPresenter(this);
        setUpViews();

        try {
            Date lastContactDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .parse(clientDetails.get(DBConstants.KEY.EDD));
            String displayContactDate =
                    new SimpleDateFormat("MMMM dd " + ", " + "yyyy", Locale.getDefault()).format(lastContactDate);
            if (!TextUtils.isEmpty(displayContactDate)) {
                deliveryDate.setText(displayContactDate);
            }


            loadPreviousContacts(baseEntityId);
            loadPreviousContactSchedule(baseEntityId, contactNo, clientDetails.get(DBConstants.KEY.EDD));
        } catch (ParseException | JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadPreviousContactSchedule(String baseEntityId, String contactNo, String edd) throws JSONException,
            ParseException {
        Facts immediatePreviousSchedule = AncApplication.getInstance().getPreviousContactRepository()
                .getImmediatePreviousSchedule(baseEntityId, contactNo);
        String contactScheduleString = "";
        if (immediatePreviousSchedule != null) {
            Map<String, Object> scheduleMap = immediatePreviousSchedule.asMap();
            for (Map.Entry<String, Object> entry : scheduleMap.entrySet()) {
                if (Constants.CONTACT_SCHEDULE.equals(entry.getKey())) {
                    contactScheduleString = entry.getValue().toString();
                }
            }
        }
        List<String> scheduleList = Utils.getListFromString(contactScheduleString);
        Date lastContactEdd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(edd);
        String formattedEdd =
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(lastContactEdd);
        List<ContactSummaryModel> schedule = formUtils
                .generateNextContactSchedule(formattedEdd, scheduleList, Integer.valueOf(contactNo));

        ContactScheduleAdapter adapter = new ContactScheduleAdapter(this, schedule);
        adapter.notifyDataSetChanged();
        contactSchedule.setLayoutManager(new LinearLayoutManager(this));
        contactSchedule.setAdapter(adapter);
    }

    private void loadPreviousContacts(String baseEntityId) {
        String contactNo = String.valueOf(Utils.getTodayContact(clientDetails.get(DBConstants.KEY.NEXT_CONTACT)));
        HashMap<String, Facts> previousContactsFacts = AncApplication.getInstance().getPreviousContactRepository()
                .getPreviousContactsFacts(baseEntityId, contactNo);

        List<Facts> contactFactsList = new ArrayList<>();

        for (Map.Entry<String, Facts> entry : previousContactsFacts.entrySet()) {
            if (Integer.parseInt(entry.getKey()) > 0) {
                Facts facts = entry.getValue();
                contactFactsList.add(facts);
            }
        }

        PreviousContactsAdapter adapter = new PreviousContactsAdapter(reverseList(contactFactsList), this);
        adapter.notifyDataSetChanged();
        previousContacts.setLayoutManager(new LinearLayoutManager(this));
        previousContacts.setAdapter(adapter);
    }


    private static List<Facts> reverseList(List<Facts> list) {
        List<Facts> reverse = new ArrayList<>(list);
        Collections.reverse(reverse);
        return reverse;
    }

    private void setUpViews() {
        deliveryDate = findViewById(R.id.delivery_date);
        previousContacts = findViewById(R.id.previous_contacts);
        contactSchedule = findViewById(R.id.upcoming_contacts);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    protected int getViewLayoutId() {
        return R.layout.activity_previous_contacts;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
