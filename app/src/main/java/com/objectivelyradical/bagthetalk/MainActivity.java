package com.objectivelyradical.bagthetalk;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RecognitionListener{
    SpeechRecognizer mSpeechRecognizer;
    boolean listening = false;
    final static String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static boolean verifyPermissions(Activity activity) {
        /*
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        */

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "requestPermissions");
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    100
            );
        }

        return ActivityCompat.checkSelfPermission
                (activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    public void clickOnTalk(View v) {
        verifyPermissions(this);
        //Log.d(TAG, ""+verifyPermissions(this));

        listening = !listening;
        if(listening) {
            // Start listening
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    "何か言ってみよう");
            mSpeechRecognizer.startListening(intent);
            ((Button)findViewById(R.id.button)).setText("STOP!");
        } else {
            // end listening
            endTalking();
        }
    }

    public void endTalking() {
        listening = false;
        mSpeechRecognizer.stopListening();
        ((Button)findViewById(R.id.button)).setText("TALK!");
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.d(TAG, "onRmsChanged");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d(TAG, "onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        Log.d(TAG, "onError");

    }

    @Override
    public void onResults(Bundle results) {
        Log.d(TAG, "onResults");

        ArrayList<String> res = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        float[] conf = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);

        if(res.size() > 0) {

            float bestScore = 0f;
            String best = "";
            for (int i = 0; i < res.size(); i++) {
                Log.d(TAG, "Result " + i + ": \"" + res.get(i) + "\" (" + conf[i] + ")");

                if (conf[i] > bestScore) {
                    best = res.get(i);
                    bestScore = conf[i];
                }
            }

            TextView view = (TextView) findViewById(R.id.results);
            String text = (String) view.getText();
            view.setText(text + "\n" + best);
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, "onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG, "onEvent");
    }
}
