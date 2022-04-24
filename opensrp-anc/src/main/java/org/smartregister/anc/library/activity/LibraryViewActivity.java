package org.smartregister.anc.library.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import org.smartregister.anc.library.R;

public class LibraryViewActivity extends AppCompatActivity {

    ImageButton closeButton;
    TextView titleText;
    WebView webview;
    String contentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_view);

        // Set toolbar
        closeButton = findViewById(R.id.close);
        if (closeButton != null) {
            closeButton.setOnClickListener(view -> onBackPressed());
        }
        titleText = findViewById(R.id.title);
        if (titleText != null) {
            titleText.setText("");
        }

        // Get filename of selected library content
        if (getIntent() != null && getIntent().getExtras() != null) {
            contentFile = getIntent().getExtras().getString("contentFile");
        }

        // Setup WebView
        webview = findViewById(R.id.libraryWebView);
        if (!contentFile.isEmpty()) {
            webview.loadUrl(contentFile);
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}