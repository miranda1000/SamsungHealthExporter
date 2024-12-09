package com.miranda1000.samsunghealthexporter;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private final String LOG_PREFIX = "SamsungHealthExporter-MainApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final Button btn = findViewById(R.id.export);
        final TextView label = findViewById(R.id.label);
        btn.setOnClickListener((View v) -> {
            label.setText(R.string.exporting);

            try {
                File latestExport = getLatestExport();
                if (latestExport == null) {
                    label.setText(R.string.no_export);
                    return;
                }
            } catch (Exception ex) {
                // something went wrong; notify
                label.setText("EXCEPTION: " + ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
                Toast.makeText(v.getContext(), R.string.export_failed, Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Nullable
    public File getLatestExport() {
        final File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (downloadsFolder == null) {
            Log.e(LOG_PREFIX, "The downloads folder was null");
            return null;
        }

        if (!downloadsFolder.exists()) {
            Log.e(LOG_PREFIX, "The downloads folder doesn't exist");
            return null;
        }

        return null;
    }
}