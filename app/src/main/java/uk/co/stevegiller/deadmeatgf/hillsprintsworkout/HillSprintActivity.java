package uk.co.stevegiller.deadmeatgf.hillsprintsworkout;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class HillSprintActivity extends ActionBarActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    public static final String TAG = "HillSprintActivity";

    private Button instigatePainButton;
    private ImageView exerciseImageView;
    private TextView setNumberTextView;
    private TextView repNumberTextView;
    
    private ArrayList<Exercise> fullExerciseList;
    private ArrayList<Exercise> chosenExerciseList;
    private MyCountDownTimer timer;
    private TextToSpeech countdownSpeaker;
    
    private boolean intermediate;
    private boolean expert;
    private int sets;
    private int reps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hill_sprint);
        countdownSpeaker = new TextToSpeech(this, this);
        countdownSpeaker.setLanguage(Locale.UK);
        timer = new MyCountDownTimer(5000, 1000);
        //-- These need to be collected from Preferences
        intermediate = false;
        expert = false;
        reps = 6;
        sets = 3;
        //-- End of preferences
        getExercises();
        exerciseImageView = (ImageView) findViewById(R.id.exerciseImageView);
        setNumberTextView = (TextView) findViewById(R.id.setNumberTextView);
        setNumberTextView.setText(String.valueOf(sets));
        repNumberTextView = (TextView) findViewById(R.id.repNumberTextView);
        repNumberTextView.setText(String.valueOf(reps));
        instigatePainButton = (Button) findViewById(R.id.instigatePainButton);
        instigatePainButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hill_sprint, menu);
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
    
    private void getExercises() {
        Log.d(TAG, "Creating exercise lists ...");
        fullExerciseList = new ArrayList<>();
        chosenExerciseList = new ArrayList<>();
        String[] standard_exercises = getResources().getStringArray(R.array.standard_exercises);
        String[] intermediate_exercises = getResources().getStringArray(R.array.intermediate_exercises);
        String[] expert_exercises = getResources().getStringArray(R.array.expert_exercises);
        TypedArray standard_images = getResources().obtainTypedArray(R.array.standard_exercise_images);
        TypedArray intermediate_images = getResources().obtainTypedArray(R.array.intermediate_exercise_images);
        TypedArray expert_images = getResources().obtainTypedArray(R.array.expert_exercise_images);
        TypedArray standard_thumbs = getResources().obtainTypedArray(R.array.standard_exercise_thumbs);
        TypedArray intermediate_thumbs = getResources().obtainTypedArray(R.array.intermediate_exercise_thumbs);
        TypedArray expert_thumbs = getResources().obtainTypedArray(R.array.expert_exercise_thumbs);
        for(int loop = 0; loop < standard_exercises.length; loop++) {
            fullExerciseList.add(new Exercise(standard_exercises[loop], standard_images.getResourceId(loop, 0), standard_thumbs.getResourceId(loop, 0), "", 0, true));
            chosenExerciseList.add(new Exercise(standard_exercises[loop], standard_images.getResourceId(loop, 0), standard_thumbs.getResourceId(loop, 0), "", 0, true));
        }
        for (int loop = 0; loop < intermediate_exercises.length; loop++) {
            if(intermediate) {
                fullExerciseList.add(new Exercise(intermediate_exercises[loop], intermediate_images.getResourceId(loop, 0), intermediate_thumbs.getResourceId(loop, 0), "", 0, true));
                chosenExerciseList.add(new Exercise(intermediate_exercises[loop], intermediate_images.getResourceId(loop, 0), intermediate_thumbs.getResourceId(loop, 0), "", 0, true));
            } else {
                fullExerciseList.add(new Exercise(intermediate_exercises[loop], intermediate_images.getResourceId(loop, 0), intermediate_thumbs.getResourceId(loop, 0), "", 0, false));
            }
        }
        for (int loop = 0; loop < expert_exercises.length; loop++) {
            if(expert) {
                fullExerciseList.add(new Exercise(expert_exercises[loop], expert_images.getResourceId(loop, 0), expert_thumbs.getResourceId(loop, 0), "", 0, true));
                chosenExerciseList.add(new Exercise(expert_exercises[loop], expert_images.getResourceId(loop, 0), expert_thumbs.getResourceId(loop, 0), "", 0, true));
            } else {
                fullExerciseList.add(new Exercise(expert_exercises[loop], expert_images.getResourceId(loop, 0), expert_thumbs.getResourceId(loop, 0), "", 0, false));
            }
        }
        Log.d(TAG, "Finished creating exercise lists ...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.instigatePainButton:
                //-- Brace Yourself!
                exerciseImageView.setImageDrawable(getResources().getDrawable(R.drawable.exercise_998));
                instigatePainButton.setEnabled(false);
                instigatePainButton.setText(R.string.button_inactive);
                timer.start();
                break;
            default:
                break;
        }
    }

    @Override
    public void onInit(int status) {
        //-- Beep!
    }

    @Override
    protected void onDestroy() {
        countdownSpeaker.stop();
        countdownSpeaker.shutdown();
        super.onDestroy();
    }

    private class MyCountDownTimer {
        private long millisInFuture;
        private long countDownInterval;

        public MyCountDownTimer(long pMillisInFuture, long pCountDownInterval) {
            this.millisInFuture = pMillisInFuture;
            this.countDownInterval = pCountDownInterval;
        }

        public void start() {
            final Handler handler = new Handler();
            Log.v("status", "starting");
            final Runnable counter = new Runnable() {

                public void run() {
                    if (millisInFuture <= 0) {
                        Log.v("status", "done");
                        countdownSpeaker.speak("Go", TextToSpeech.QUEUE_FLUSH, null);
                    } else {
                        long sec = millisInFuture / 1000;
                        Log.v("status", Long.toString(sec) + " seconds remain");
                        countdownSpeaker.speak(String.valueOf(sec), TextToSpeech.QUEUE_FLUSH, null);
                        millisInFuture -= countDownInterval;
                        handler.postDelayed(this, countDownInterval);
                    }
                }
            };

            handler.postDelayed(counter, countDownInterval);
        }
    }
}
