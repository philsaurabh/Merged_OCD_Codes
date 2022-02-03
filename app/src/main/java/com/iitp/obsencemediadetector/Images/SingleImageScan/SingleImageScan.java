package com.iitp.obsencemediadetector.Images.SingleImageScan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iitp.obsencemediadetector.Model.ScannedImages;
import com.iitp.obsencemediadetector.Utils.ImageResizer;
import com.iitp.obsencemediadetector.Utils.MlModel;
import com.iitp.obsencemediadetector.R;

import java.io.IOException;

public class SingleImageScan extends AppCompatActivity {

    ImageView imageView;
    TextView value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image_scan);

        _init();

        Intent intent = getIntent();
        if(intent==null || !intent.hasExtra("ImageUri")){
            Toast.makeText(SingleImageScan.this, "Failed to fetch Image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set Image Data
        SetData();

    }

    private void SetData() {
        String imageUriStr = getIntent().getStringExtra("ImageUri");
        Uri imageUri = Uri.parse(imageUriStr);

        try {
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            imageBitmap = ImageResizer.reduceBitmapSize(imageBitmap, 240000);
            imageView.setImageBitmap(imageBitmap);

            float Score = MlModel.ProcessData(this, imageBitmap);
            value.setText("Obscene Score: " + Score);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void _init() {
        imageView = findViewById(R.id.single_image_scan_image);
        value = findViewById(R.id.single_image_scan_value);
    }
}