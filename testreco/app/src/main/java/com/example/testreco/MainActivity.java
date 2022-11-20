package com.example.testreco;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    ImageView img;

    Uri imageUri;
    TextView textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = findViewById(R.id.imageView);
        textview = findViewById(R.id.textView);
        ActivityResultLauncher<String> gallery = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        img.setImageURI(result);imageUri = result;
                    }
                });
        ActivityResultLauncher<String> storagePermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                result -> {
                    if (result) {
                        gallery.launch("image/*");
                    } else {
                        Toast.makeText(MainActivity.this, "Storage Permission required", Toast.LENGTH_SHORT).show();
                    }
                });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storagePermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                op(getApplicationContext(),imageUri);
            }
        });



    }
    public void op(Context context, Uri uri){
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = null;
        try {
            image = InputImage.fromFilePath(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                if(visionText.getText().equals(".") || visionText.getText().length() == 0){
                                    Toast.makeText(context, "No text found", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    textview.setText(visionText.getText());
                                    Toast.makeText(context, "Text is"+visionText.getText(), Toast.LENGTH_SHORT).show();
                                    testc(visionText);
                                }
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "No text found", Toast.LENGTH_SHORT).show();
                                    }
                                });
    }
    public void testc(Text result){
        String textResult = result.getText();
        for(Text.TextBlock textBlock : result.getTextBlocks()){
            for (Text.Line line : textBlock.getLines()) {
                for (Text.Element element : line.getElements()) {
                    Toast.makeText(this, "Text is"+element.getText(), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
}