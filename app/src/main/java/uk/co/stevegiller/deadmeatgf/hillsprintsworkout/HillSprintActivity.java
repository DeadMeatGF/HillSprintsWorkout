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

    private static final int NOT_STARTED = 0;
    private static final int PRE_EXERCISE = 1;
    private static final int HILL_SPRINT = 2;
    private static final int DO_EXERCISE = 3;
    private static final int LAST_EXERCISE = 4;
    private static final int SHORT_REST = 5;
    private static final int LONG_REST = 6;
    private static final int FINISHED = 7;

    private Button instigatePainButton;
    private ImageView exerciseImageView;
    private TextView setNumberTextView;
    private TextView repNumberTextView;
    private TextView currentExerciseTextView;
    private TextView nextExerciseTextView;

    private ArrayList<Exercise> fullExerciseList;       //-- List of all exercises in the app.
    private ArrayList<Exercise> chosenExerciseList;     //-- Exercises chosen for the current set.
    private ArrayList<Exercise> selectedExerciseList;   //-- Exercises selected for inclusion in the current workout.
    private ExcerciseCountDownTimer exerciseTimer;
    private TextToSpeech countdownSpeaker;
    
    private boolean intermediate;
    private boolean expert;
    private int totalSets;
    private int currentSet;
    private int totalReps;
    private int currentRep;
    private int currentPhase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hill_sprint);
        countdownSpeaker = new TextToSpeech(this, this);
        countdownSpeaker.setLanguage(Locale.UK);
        //-- These need to be collected from Preferences

        intermediate = false;
        expert = false;
        totalReps = 6;
        totalSets = 3;
        //-- End of preferences
        currentPhase = NOT_STARTED;
        currentSet = 0;
        currentRep = 0;
        getExercises();
        chosenExerciseList = new ArrayList<>();
        chosenExerciseList = getSet(selectedExerciseList);
        exerciseImageView = (ImageView) findViewById(R.id.exerciseImageView);
        currentExerciseTextView = (TextView) findViewById(R.id.currentExerciseTextView);
        nextExerciseTextView = (TextView) findViewById(R.id.nextExerciseTextView);
        setNumberTextView = (TextView) findViewById(R.id.setNumberTextView);
        setNumberTextView.setText(currentSet + "/" + totalSets);
        repNumberTextView = (TextView) findViewById(R.id.repNumberTextView);
        repNumberTextView.setText(currentRep + "/" + totalReps);
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
        selectedExerciseList = new ArrayList<>();
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
            selectedExerciseList.add(new Exercise(standard_exercises[loop], standard_images.getResourceId(loop, 0), standard_thumbs.getResourceId(loop, 0), "", 0, true));
        }
        for (int loop = 0; loop < intermediate_exercises.length; loop++) {
            if(intermediate) {
                fullExerciseList.add(new Exercise(intermediate_exercises[loop], intermediate_images.getResourceId(loop, 0), intermediate_thumbs.getResourceId(loop, 0), "", 0, true));
                selectedExerciseList.add(new Exercise(intermediate_exercises[loop], intermediate_images.getResourceId(loop, 0), intermediate_thumbs.getResourceId(loop, 0), "", 0, true));
            } else {
                fullExerciseList.add(new Exercise(intermediate_exercises[loop], intermediate_images.getResourceId(loop, 0), intermediate_thumbs.getResourceId(loop, 0), "", 0, false));
            }
        }
        for (int loop = 0; loop < expert_exercises.length; loop++) {
            if(expert) {
                fullExerciseList.add(new Exercise(expert_exercises[loop], expert_images.getResourceId(loop, 0), expert_thumbs.getResourceId(loop, 0), "", 0, true));
                selectedExerciseList.add(new Exercise(expert_exercises[loop], expert_images.getResourceId(loop, 0), expert_thumbs.getResourceId(loop, 0), "", 0, true));
            } else {
                fullExerciseList.add(new Exercise(expert_exercises[loop], expert_images.getResourceId(loop, 0), expert_thumbs.getResourceId(loop, 0), "", 0, false));
            }
        }
    }

    private ArrayList<Exercise> getSet(ArrayList<Exercise> list) {
        ArrayList<Exercise> set = new ArrayList<>();
        for (int i = 0; i < totalReps; i++) {
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
                currentPhase = nextPhase(currentPhase);
                instigatePainButton.setEnabled(false);
                instigatePainButton.setText(R.string.button_inactive);
                nextExercise();
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
        Log.d(TAG, "Entered nextExercise() ... currentPhase = " + currentPhase);
        currentPhase = nextPhase(currentPhase);
        switch (currentPhase) {
            case NOT_STARTED:
                Log.d(TAG, "Processing phase for nextExercise().NOT_STARTED");
                Log.e(TAG, "Something fucked up! You shouldn't ever be able to trigger this");
                break;
            case PRE_EXERCISE:
                Log.d(TAG, "Processing phase for nextExercise().PRE_EXERCISE");
                //-- 10 second countdown;
                exerciseTimer = new ExcerciseCountDownTimer(10000, 1000, 5000, 0, "Sprint");
                exerciseTimer.start();
                //-- Set image to "Brace Yourself";
                exerciseImageView.setImageDrawable(getResources().getDrawable(R.drawable.exercise_998));
                //-- Set Current Exercise Text to "Get Ready"
                currentExerciseTextView.setText("Get Ready!");
                //-- Set Next Exercise Text to "Hill Sprint"
                nextExerciseTextView.setText("Hill Sprint");
                break;
            case HILL_SPRINT:
                Log.d(TAG, "Processing phase for nextExercise().HILL_SPRINT");
                //-- 10 second countdown;
                exerciseTimer = new ExcerciseCountDownTimer(10000, 1000, 5000, 0, chosenExerciseList.get(currentRep - 1).getName());
                exerciseTimer.start();
                //-- Set image to "Hill Sprint";
                exerciseImageView.setImageDrawable(getResources().getDrawable(R.drawable.exercise_997));
                //-- Set Current Exercise Text to "Hill Sprint"
                currentExerciseTextView.setText("Hill Sprint");
                //-- Set Next Exercise Text to current exercise name
                nextExerciseTextView.setText(chosenExerciseList.get(currentRep - 1).getName());
                break;
            case DO_EXERCISE:
                Log.d(TAG, "Processing phase for nextExercise().DO_EXERCISE");
                //-- 30 second countdown;
                exerciseTimer = new ExcerciseCountDownTimer(30000, 1000, 5000, ExcerciseCountDownTimer.HALFWAY_NOTIFICATION + ExcerciseCountDownTimer.QUEUE_INITIAL_NUMBER_WITH_SECOND, "Recover");
                exerciseTimer.start();
                //-- Set image to current exercise image;
                exerciseImageView.setImageDrawable(getResources().getDrawable(chosenExerciseList.get(currentRep - 1).getImage()));
                //-- Set Current Exercise Text to current exercise name
                currentExerciseTextView.setText(chosenExerciseList.get(currentRep - 1).getName());
                //-- Set Next Exercise Text to "Recover"
                nextExerciseTextView.setText("Recover");
                break;
            case LAST_EXERCISE:
                Log.d(TAG, "Processing phase for nextExercise().LAST_EXERCISE");
                //-- 30 second countdown;
                exerciseTimer = new ExcerciseCountDownTimer(30000, 1000, 5000, ExcerciseCountDownTimer.HALFWAY_NOTIFICATION + ExcerciseCountDownTimer.QUEUE_INITIAL_NUMBER_WITH_SECOND, "You're Finished!");
                exerciseTimer.start();
                //-- Set image to current exercise image;
                exerciseImageView.setImageDrawable(getResources().getDrawable(chosenExerciseList.get(currentRep - 1).getImage()));
                //-- Set Current Exercise Text to current exercise name
                currentExerciseTextView.setText(chosenExerciseList.get(currentRep - 1).getName());
                //-- Set Next Exercise Text to "Finish"
                nextExerciseTextView.setText("Finish!");
                break;
            case SHORT_REST:
                Log.d(TAG, "Processing phase for nextExercise().SHORT_REST");
                //-- 30 second countdown;
                exerciseTimer = new ExcerciseCountDownTimer(30000, 1000, 5000, ExcerciseCountDownTimer.HALFWAY_NOTIFICATION + ExcerciseCountDownTimer.QUEUE_INITIAL_NUMBER_WITH_SECOND, "Sprint");
                exerciseTimer.start();
                //-- Set image to "Take a break";
                exerciseImageView.setImageDrawable(getResources().getDrawable(R.drawable.exercise_996));
                //-- Set Current Exercise Text to "Recover"
                currentExerciseTextView.setText("Recover");
                //-- Set Next Exercise Text to "Hill Sprint"
                nextExerciseTextView.setText("Hill Sprint");
                break;
            case LONG_REST:
                Log.d(TAG, "Processing phase for nextExercise().LONG_REST");
                //-- 60 second countdown;
                exerciseTimer = new ExcerciseCountDownTimer(60000, 1000, 10000, ExcerciseCountDownTimer.HALFWAY_NOTIFICATION + ExcerciseCountDownTimer.QUEUE_INITIAL_NUMBER_WITH_SECOND, "Sprint");
                exerciseTimer.start();
                //-- Set image to "Take a Break";
                exerciseImageView.setImageDrawable(getResources().getDrawable(R.drawable.exercise_996));
                //-- Set Current Exercise Text to "Take a breather"
                currentExerciseTextView.setText("Take a Breather");
                //-- Set Next Exercise Text to "Hill Sprint"
                nextExerciseTextView.setText("Hill Sprint");
                break;
            case FINISHED:
                Log.d(TAG, "Processing phase for nextExercise().FINISHED");
                //-- Set image to "It's Over";
                exerciseImageView.setImageDrawable(getResources().getDrawable(R.drawable.exercise_995));
                //-- Set Current Exercise Text to "Congratulations"
                currentExerciseTextView.setText("Congratulations");
                //-- Set Next Exercise Text to "You've finished the workout"
                nextExerciseTextView.setText("You've finished the workout");
                break;
            default:
        }
    }

    private int nextPhase(int phase) {
        Log.d(TAG, "Entered nextPhase(int phase) ... phase = " + phase);
        switch (phase) {
            case NOT_STARTED:
                Log.d(TAG, "Processing phase for nextPhase().NOT_STARTED");
                phase = PRE_EXERCISE;
                break;
            case PRE_EXERCISE:
                Log.d(TAG, "Processing phase for nextPhase().PRE_EXERCISE");
                currentRep = 1;
                currentSet = 1;
                phase = HILL_SPRINT;
                break;
            case HILL_SPRINT:
                Log.d(TAG, "Processing phase for nextPhase().HILL_SPRINT");
                if (currentSet == totalSets && currentRep == totalReps) {
                    phase = LAST_EXERCISE;
                } else {
                    phase = DO_EXERCISE;
                }
                setNumberTextView.setText(currentSet + "/" + totalSets);
                repNumberTextView.setText(currentRep + "/" + totalReps);
                break;
            case DO_EXERCISE:
                Log.d(TAG, "Processing phase for nextPhase().DO_EXERCISE");
                if (currentRep == totalReps) {
                    phase = LONG_REST;
                } else {
                    phase = SHORT_REST;
                }
                break;
            case LAST_EXERCISE:
                Log.d(TAG, "Processing phase for nextPhase().LAST_EXERCISE");
                phase = FINISHED;
                break;
            case SHORT_REST:
                Log.d(TAG, "Processing phase for nextPhase().SHORT_REST");
                phase = HILL_SPRINT;
                currentRep++;
                break;
            case LONG_REST:
                Log.d(TAG, "Processing phase for nextPhase().LONG_REST");
                phase = HILL_SPRINT;
                currentRep = 1;
                currentSet++;
                chosenExerciseList.clear();
                chosenExerciseList = getSet(selectedExerciseList);
                break;
            case FINISHED:
                Log.d(TAG, "Processing phase for nextPhase().FINISHED");
                phase = NOT_STARTED;
                break;
            default:
        }
        return phase;
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
