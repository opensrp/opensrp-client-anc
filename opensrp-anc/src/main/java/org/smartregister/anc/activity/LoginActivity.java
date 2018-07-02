package org.smartregister.anc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.R;
import org.smartregister.util.FormUtils;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//For Testing Json Form function only...this code should be in Extended AncApplication class
        Context context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        CoreLibrary.init(context);

        TextView textView = findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startFormActivity();
            }
        });
    }

    private void startFormActivity() {
        try {
            Intent intent = new Intent(this, AncJsonFormActivity.class);

            JSONObject form = FormUtils.getInstance(this).getFormJson("anc_close");
            if (form != null) {
                intent.putExtra("json", form.toString());

                startActivityForResult(intent, 0);
            }
        } catch (Exception e) {
            Log.e("LoginActivity", e.getMessage());
        }
    }

}
