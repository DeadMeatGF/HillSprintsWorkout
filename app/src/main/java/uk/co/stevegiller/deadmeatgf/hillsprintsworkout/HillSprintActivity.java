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
import java.util.Random;

public class HillSprintActivity extends ActionBarActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    public static final String TAG = "HillSprintActivity";

    private Button instigatePainButton;
    private ImageView exerciseImageView;
    private TextView setNumberTextView;
    private TextView repNumberTextView;
    private TextView currentExerciseTextView;
    private TextView nextExerciseTextView;
    
    private ArrayList<Exercise> fullExerciseList;
    private ArrayList<Exercise> chosenExerciseList;
    private ArrayList<Exercise> selectedExerciseList;
    private ExcerciseCountDownTimer exerciseTimer;
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
        //-- These need to be collected from Preferences
        intermediate = false;
        expert = false;
        reps = 6;
        sets = 3;
        //-- End of preferences
        getExercises();
        selectedExerciseList = new ArrayList<>();
        selectedExerciseList = getSet(chosenExerciseList);
        exerciseImageView = (ImageView) findViewById(R.id.exerciseImageView);
        currentExerciseTextView = (TextView) findViewById(R.id.currentExerciseTextView);
        nextExerciseTextView = (TextView) findViewById(R.id.nextExerciseTextView);
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
    }

    private ArrayList<Exercise> getSet(ArrayList<Exercise> list) {
        ArrayList<Exercise> set = new ArrayList<>();
        for (int i = 0; i < reps; i++) {
            Random e = new Random();
            int index = e.nextInt(list.size());
            set.add(list.get(index));
            list.remove(index);
        }
        return set;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.instigatePainButton:
                //-- Brace Yourself!
                exerciseImageView.setImageDrawable(getResources().getDrawable(R.drawable.exercise_998));
                instigatePainButton.setEnabled(false);
                instigatePainButton.setText(R.string.button_inactive);
                exerciseTimer = new ExcerciseCountDownTimer(5000, 1000, 5000, 0, "Sprint ... then " + chosenExerciseList.get(0).getName());
                exerciseTimer.start();
                currentExerciseTextView.setText("Brace Yourself!");
                nextExerciseTextView.setText("Hill Sprint");
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

    private void nextExercise() {
        //-- Let's do this!
        currentExerciseTextView.setText(nextExerciseTextView.getText());
        if (currentExerciseTextView.getText().equals("Hill Sprint")) {
            exerciseTimer = new ExcerciseCountDownTimer(10000, 1000, 5000, 0, chosenExerciseList.get(0).getName());
            exerciseTimer.start();
            exerciseImageView.setImageDrawable(getResources().getDrawable(R.drawable.exercise_997));
            nextExerciseTextView.setText(chosenExerciseList.get(0).getName());
            setNumberTextView.setText(String.valueOf(sets));
            repNumberTextView.setText(String.valueOf(chosenExerciseList.size()));
        } else if (currentExerciseTextView.getText().equals(chosenExerciseList.get(0).getName())) {
            if (chosenExerciseList.size() > 1) {
                nextExerciseTextView.setText("Get your breath back!");
                exerciseTimer = new ExcerciseCountDownTimer(30000, 1000, 5000, ExcerciseCountDownTimer.HALFWAY_NOTIFICATION + ExcerciseCountDownTimer.QUEUE_INITIAL_NUMBER_WITH_SECOND, "Recover");
            } else {
                if (sets == 1) {
                    nextExerciseTextView.setText("You've finished!");
                    exerciseTimer = new ExcerciseCountDownTimer(30000, 1000, 5000, ExcerciseCountDownTimer.HALFWAY_NOTIFICATION + ExcerciseCountDownTimer.QUEUE_INITIAL_NUMBER_WITH_SECOND, "It's Over! Take a break!");
                } else {
                    nextExerciseTextView.setText("You've completed the set.");
                    exerciseTimer = new ExcerciseCountDownTimer(30000, 1000, 5000, ExcerciseCountDownTimer.HALFWAY_NOTIFICATION + ExcerciseCountDownTimer.QUEUE_INITIAL_NUMBER_WITH_SECOND, "Set complete. Have a breather.");
                    sets--;
                }
            }
            exerciseTimer.start();
            exerciseImageView.setImageDrawable(getResources().getDrawable(chosenExerciseList.get(0).getImage()));
            chosenExerciseList.remove(0);
        } else if (currentExerciseTextView.getText().equals("Get your breath back!")) {
            exerciseTimer = new ExcerciseCountDownTimer(30000, 1000, 5000, ExcerciseCountDownTimer.HALFWAY_NOTIFICATION + ExcerciseCountDownTimer.QUEUE_INITIAL_NUMBER_WITH_SECOND, "Sprint ... then " + chosenExerciseList.get(0).getName());
            exerciseTimer.start();
            exerciseImageView.setImageDrawable(getResources().getDrawable(R.drawable.exercise_996));
            nextExerciseTextView.setText("Hill Sprint");
        } else if (currentExerciseTextView.getText().equals("You've completed the set.")) {
            chosenExerciseList.clear();
            chosenExerciseList = getSet(selectedExerciseList);
            exerciseTimer = new ExcerciseCountDownTimer(60000, 1000, 10000, ExcerciseCountDownTimer.HALFWAY_NOTIFICATION + ExcerciseCountDownTimer.QUEUE_INITIAL_NUMBER_WITH_SECOND, "Sprint ... then " + chosenExerciseList.get(0).getName());
            exerciseTimer.start();
            exerciseImageView.setImageDrawable(getResources().getDrawable(R.drawable.exercise_996));
        } else if (currentExerciseTextView.getText().equals("You've finished!")) {
            nextExerciseTextView.setText("");
            exerciseImageView.setImageDrawable(getResources().getDrawable(R.drawable.exercise_995));
        }
    }

    private class ExcerciseCountDownTimer {
        public static final int SAY_INITIAL_NUMBER = 1;
        public static final int QUEUE_INITIAL_NUMBER = 2;
        public static final int SAY_INITIAL_NUMBER_WITH_SECOND = 4;
        public static final int QUEUE_INITIAL_NUMBER_WITH_SECOND = 8;
        public static final int HALFWAY_NOTIFICATION = 16;

        private long millisInFuture;
        private long countDownInterval;
        private long countDownStart;
        private String finish;
        private long halfwayMillis;
        private boolean firstTick;
        private int type;
        private int queue;

        public ExcerciseCountDownTimer(long pMillisInFuture, long pCountDownInterval, long pCountDownStart, int pType, String sFinish) {
            this.millisInFuture = pMillisInFuture;
            this.countDownInterval = pCountDownInterval;
            this.countDownStart = pCountDownStart;
            this.finish = sFinish;
            this.type = pType;
            if ((type & HALFWAY_NOTIFICATION) == HALFWAY_NOTIFICATION) {
                this.halfwayMillis = millisInFuture / 2;
            } else {
                this.halfwayMillis = millisInFuture + 1000;
            }
            Log.v("TimerStatus", "setting halfway to " + halfwayMillis);
            if ((type & SAY_INITIAL_NUMBER) == SAY_INITIAL_NUMBER ||
                    (type & QUEUE_INITIAL_NUMBER) == QUEUE_INITIAL_NUMBER ||
                    (type & SAY_INITIAL_NUMBER_WITH_SECOND) == SAY_INITIAL_NUMBER_WITH_SECOND ||
                    (type & QUEUE_INITIAL_NUMBER_WITH_SECOND) == QUEUE_INITIAL_NUMBER_WITH_SECOND) {
                firstTick = true;
            } else {
                firstTick = false;
            }
        }

        public void start() {
            final Handler handler = new Handler();
            Log.v("TimerStatus", "starting");
            final Runnable counter = new Runnable() {

                public void run() {
                    if (millisInFuture <= 0) {
                        Log.v("TimerStatus", "done");
                        countdownSpeaker.speak(finish, TextToSpeech.QUEUE_FLUSH, null);
                        nextExercise();
                    } else {
                        long sec = millisInFuture / 1000;
                        if (millisInFuture <= countDownStart) {
                            countdownSpeaker.speak(String.valueOf(sec), TextToSpeech.QUEUE_FLUSH, null);
                        } else if (firstTick == true) {
                            if ((type & SAY_INITIAL_NUMBER) == SAY_INITIAL_NUMBER ||
                                    (type & SAY_INITIAL_NUMBER_WITH_SECOND) == SAY_INITIAL_NUMBER_WITH_SECOND) {
                                queue = TextToSpeech.QUEUE_FLUSH;
                            } else {
                                queue = TextToSpeech.QUEUE_ADD;
                            }
                            if ((type & SAY_INITIAL_NUMBER) == SAY_INITIAL_NUMBER ||
                                    (type & SAY_INITIAL_NUMBER_WITH_SECOND) == SAY_INITIAL_NUMBER_WITH_SECOND ||
                                    (type & QUEUE_INITIAL_NUMBER) == QUEUE_INITIAL_NUMBER ||
                                    (type & QUEUE_INITIAL_NUMBER_WITH_SECOND) == QUEUE_INITIAL_NUMBER_WITH_SECOND) {
                                countdownSpeaker.speak(String.valueOf(sec), queue, null);
                            }
                            if ((type & SAY_INITIAL_NUMBER_WITH_SECOND) == SAY_INITIAL_NUMBER_WITH_SECOND ||
                                    (type & QUEUE_INITIAL_NUMBER_WITH_SECOND) == QUEUE_INITIAL_NUMBER_WITH_SECOND) {
                                countdownSpeaker.speak("seconds", TextToSpeech.QUEUE_ADD, null);
                            }
                        }
                        if (Math.abs(millisInFuture - halfwayMillis) < 100) {
                            countdownSpeaker.speak("Halfway Point", TextToSpeech.QUEUE_FLUSH, null);
                        }
                        millisInFuture -= countDownInterval;
                        handler.postDelayed(this, countDownInterval);
                        firstTick = false;
                    }
                }
            };

            handler.postDelayed(counter, countDownInterval);
        }
    }
}
