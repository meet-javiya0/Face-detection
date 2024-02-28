package com.example.facedetection

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnDetectFace = findViewById<Button>(R.id.btnDetectFace)

        btnDetectFace.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, 1)

            } else {
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "super.onActivityResult(requestCode, resultCode, data)",
            "androidx.appcompat.app.AppCompatActivity"
        )
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val extras = data!!.extras
            val imageBitmap = extras!!.get("data") as Bitmap
            detectFace(imageBitmap)
        } else {
            Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun detectFace(imageBitmap: Bitmap) {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val detector = FaceDetection.getClient(options)
        val image = InputImage.fromBitmap(imageBitmap, 0)

        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                var resultText = ""
                var i = 1
                for (face in faces) {
                    resultText = resultText +
                            "\n\nFace number: $i" +
                            "\nSmiling Probability: ${face.smilingProbability?.times(100)}%" +
                            "\nLeft eye open: ${face.leftEyeOpenProbability?.times(100)}%" +
                            "\nRight eye open: ${face.rightEyeOpenProbability?.times(100)}%"
                    i++
                }
                if (!faces.isNullOrEmpty()) {
                    val txtDesc = findViewById<TextView>(R.id.FaceDetectionDescription)
                    txtDesc.text = resultText
                    Toast.makeText(this, "Face detected successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Face not detected", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Face not detected", Toast.LENGTH_SHORT).show()
            }
    }
}