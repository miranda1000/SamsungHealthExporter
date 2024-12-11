package com.miranda1000.samsunghealthexporter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.documentfile.provider.DocumentFile;

import com.miranda1000.samsunghealthexporter.database.SamsungHealthDatabase;
import com.miranda1000.samsunghealthexporter.database.SamsungHealthMySQLDatabase;
import com.miranda1000.samsunghealthexporter.entities.HeartRate;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE_OPEN_DOCUMENT = 1;

    private final SamsungHealthDiskSystem samsungHealthDiskSystem = new SamsungHealthDiskSystem(this);

    private static final String ip = "192.168.1.80";
    private static final String username = "root",
                                password = "admin";
    private static final String ddbbName = "health";
    private final SamsungHealthDatabase samsungHealthDatabase = new SamsungHealthMySQLDatabase(ip, username, password, ddbbName);

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
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT);
            } catch (Exception ex) {
                // something went wrong; notify
                label.setText("EXCEPTION: " + ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
                Log.e("SamsungHealthMainActivity", "Exception while launching intent", ex);
                Toast.makeText(v.getContext(), R.string.export_failed, Toast.LENGTH_LONG)
                        .show();
            }
        });

        if (!this.samsungHealthDatabase.canConnect()) {
            // failed to connect to the database
            label.setText(R.string.ddbb_connection_failed);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_OPEN_DOCUMENT && resultCode == RESULT_OK) {
            Uri treeUri = data.getData();
            if (treeUri != null) {
                // Persist permission for future use
                getContentResolver().takePersistableUriPermission(
                        treeUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );

                DocumentFile latestExport = samsungHealthDiskSystem.getLatestExport(treeUri);
                final TextView label = findViewById(R.id.label);
                if (latestExport == null) {
                    label.setText(R.string.no_export);
                }
                else {
                    if (this.exportInformation(latestExport)) {
                        label.setText(R.string.export_msg);
                    }
                }
            }
        }
    }

    public boolean exportInformation(@NonNull DocumentFile latestExport) {
        try {
            HeartRate[] extractHeartRate = this.samsungHealthDiskSystem.extractHeartRate(latestExport);
            this.samsungHealthDatabase.exportHeartRate(extractHeartRate);
            return true;
        } catch (Exception ex) {
            // something went wrong; notify
            final TextView label = findViewById(R.id.label);
            label.setText("EXCEPTION: " + ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
            Log.e("SamsungHealthMainActivity", "Exception while exporting", ex);
            return false;
        }
    }
}