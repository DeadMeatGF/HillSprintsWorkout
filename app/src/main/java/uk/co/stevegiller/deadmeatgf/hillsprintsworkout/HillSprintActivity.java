package uk.co.stevegiller.deadmeatgf.hillsprintsworkout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
    private static final int SHORT_REST = 4;
    private static final int LONG_REST = 5;
    private static final int LAST_EXERCISE = 6;
    private static final int FINISHED = 7;

    private static final int APP_NOT_BEGUN = 0;
    private static final int APP_RUNNING = 1;
    private static final int APP_PAUSED = 2;
    private int app_status = APP_NOT_BEGUN;
    private Button instigatePainButton;
    private ImageView exerciseImageView;
    private TextView setNumberTextView;
    private TextView repNumberTextView;
    private TextView currentExerciseTextView;
    private TextView nextExerciseTextView;
    private ArrayList<Exercise> fullExerciseList;       //-- List of all exercises in the app.
    private ArrayList<Exercise> chosenExerciseList;     //-- Exercises chosen for the current set.
    private ArrayList<Exercise> availableExerciseList;   //-- Exercises selected for inclusion in the current workout.
    private ExerciseCountDownTimer exerciseTimer;
    private TextToSpeech countdownSpeaker;
    private boolean intermediate;
    private boolean expert;
    private int totalSets;
    private int currentSet;
    private int totalReps;
    private int currentRep;
    private int currentPhase;
    private int setRestLength;
    private int repRestLength;
    private int repLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hill_sprint);
        countdownSpeaker = new TextToSpeech(this, this);
        countdownSpeaker.setLanguage(Locale.UK);
        //-- Get Reps, Sets and intermediate/expert options from PreferencesActivity
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        totalReps = settings.getInt("prefs_number_of_reps", 6);
        totalSets = settings.getInt("prefs_number_of_sets", 3);
        intermediate = settings.getBoolean("prefs_intermediate_exercises", false);
        if (intermediate) {
            expert = settings.getBoolean("prefs_expert_settings", false);
        } else {
            expert = false;
        }
        setRestLength = settings.getInt("prefs_long_rest_length", 60);
        repRestLength = settings.getInt("prefs_short_rest_length", 30);
        repLength = settings.getInt("prefs_rep_length", 30);
        //-- End of preferences
        currentPhase = NOT_STARTED;
        currentSet = 0;
        currentRep = 0;

        getExercises();

        chosenExerciseList = new ArrayList<>();
        chosenExerciseList = getSet(availableExerciseList);

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
            Intent intent = new Intent(this, SprintSettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    private void getExercises() {
        fullExerciseList = new ArrayList<>();
        availableExerciseList = new ArrayList<>();

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
            availableExerciseList.add(new Exercise(standard_exercises[loop], standard_images.getResourceId(loop, 0), standard_thumbs.getResourceId(loop, 0), "", 0, true));
            Log.d(TAG, "Added " + standard_exercises[loop] + " to all and available exercises");
        }
        for (int loop = 0; loop < intermediate_exercises.length; loop++) {
            if(intermediate) {
                fullExerciseList.add(new Exercise(intermediate_exercises[loop], intermediate_images.getResourceId(loop, 0), intermediate_thumbs.getResourceId(loop, 0), "", 0, true));
                availableExerciseList.add(new Exercise(intermediate_exercises[loop], intermediate_images.getResourceId(loop, 0), intermediate_thumbs.getResourceId(loop, 0), "", 0, true));
                Log.d(TAG, "Added " + intermediate_exercises[loop] + " to all and available exercises");
            } else {
                fullExerciseList.add(new Exercise(intermediate_exercises[loop], intermediate_images.getResourceId(loop, 0), intermediate_thumbs.getResourceId(loop, 0), "", 0, false));
                Log.d(TAG, "Added " + intermediate_exercises[loop] + " to all exercises");
            }
        }
        for (int loop = 0; loop < expert_exercises.length; loop++) {
            if(expert) {
                fullExerciseList.add(new Exercise(expert_exercises[loop], expert_images.getResourceId(loop, 0), expert_thumbs.getResourceId(loop, 0), "", 0, true));
                availableExerciseList.add(new Exercise(expert_exercises[loop], expert_images.getResourceId(loop, 0), expert_thumbs.getResourceId(loop, 0), "", 0, true));
                Log.d(TAG, "Added " + expert_exercises[loop] + " to all and available exercises");
            } else {
                fullExerciseList.add(new Exercise(expert_exercises[loop], expert_images.getResourceId(loop, 0), expert_thumbs.getResourceId(loop, 0), "", 0, false));
                Log.d(TAG, "Added " + expert_exercises[loop] + " to all exercises");
            }
        }
    }

    private ArrayList<Exercise> getSet(ArrayList<Exercise> list) {
        ArrayList<Exercise> set = new ArrayList<>();
        for (int i = 0; i < totalReps; i++) {
            Random e = new Random(SystemClock.elapsedRealtime());
            int index = e.nextInt(list.size());
            set.add(list.get(index));
            Log.d(TAG, "Added " + list.get(index).getName() + " to chosen exercises");
            list.remove(index);
        }
        return set;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.instigatePainButton:
                app_status = APP_RUNNING;
                Log.d(TAG, "You've clicked the GO button - on your own head be it!");
                instigatePainButton.setEnabled(false);
                instigatePainButton.setText(R.string.button_inactive);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
    protected void onResume() {
        super.onResume();
        if (app_status != APP_RUNNING) {
            Log.d(TAG, "Grabbing preferences in onResume()");
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
            totalReps = settings.getInt("prefs_number_of_reps", 6);
            totalSets = settings.getInt("prefs_number_of_sets", 3);
            intermediate = settings.getBoolean("prefs_intermediate_exercises", false);
            if (intermediate) {
                expert = settings.getBoolean("prefs_expert_settings", false);
            } else {
                expert = false;
            }
            setRestLength = settings.getInt("prefs_long_rest_length", 60);
            repRestLength = settings.getInt("prefs_short_rest_length", 30);
            repLength = settings.getInt("prefs_rep_length", 30);

            getExercises();

            chosenExerciseList = new ArrayList<>();
            chosenExerciseList = getSet(availableExerciseList);

            setNumberTextView.setText((currentSet) + "/" + totalSets);
            repNumberTextView.setText((currentRep) + "/" + totalReps);
        } else {
            setNumberTextView.setText((currentSet + 1) + "/" + totalSets);
            repNumberTextView.setText((currentRep + 1) + "/" + totalReps);
        }
    }

    @Override
    protected void onDestroy() {
        countdownSpeaker.stop();
        countdownSpeaker.shutdown();
        super.onDestroy();
    }

    private void nextExercise() {
        Log.d(TAG, "Entered nextExercise() ... currentPhase = " + currentPhase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
        currentPhase = nextPhase(currentPhase);
        if (currentPhase == PRE_EXERCISE) {
            setNumberTextView.setText((currentSet) + "/" + totalSets);
            repNumberTextView.setText(currentRep + "/" + totalReps);
        } else if (currentPhase > LAST_EXERCISE) {
            setNumberTextView.setText("Completed");
            repNumberTextView.setText("Completed");
        } else {
            repNumberTextView.setText((currentRep + 1) + "/" + totalReps);
            setNumberTextView.setText((currentSet + 1) + "/" + totalSets);
        }
        switch (currentPhase) {
            case NOT_STARTED:
                Log.d(TAG, "Processing phase for nextExercise().NOT_STARTED");
                Log.e(TAG, "Something fucked up! You shouldn't ever be able to trigger this");
                break;
            case PRE_EXERCISE:
                Log.d(TAG, "Processing phase for nextExercise().PRE_EXERCISE ... currentPhase = " + currentPhase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                //-- Get Ready!
                countdownSpeaker.speak("Get Ready!", TextToSpeech.QUEUE_FLUSH, null);
                //-- 10 second countdown;
                exerciseTimer = new ExerciseCountDownTimer(10000, 1000, 5000, 0, "Sprint . . Then " + chosenExerciseList.get(currentRep).getName());
                exerciseTimer.start();
                //-- Set image to "Brace Yourself";
                exerciseImageView.setImageDrawable(getResources().getDrawable(R.drawable.exercise_998));
                //-- Set Current Exercise Text to "Get Ready"
                currentExerciseTextView.setText("Get Ready!");
                //-- Set Next Exercise Text to "Hill Sprint"
                nextExerciseTextView.setText("Hill Sprint");
                Log.d(TAG, "Exiting phase for nextExercise().PRE_EXERCISE ... currentPhase = " + currentPhase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                break;
            case HILL_SPRINT:
                Log.d(TAG, "Processing phase for nextExercise().HILL_SPRINT ... currentPhase = " + currentPhase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                //-- 10 second countdown;
                exerciseTimer = new ExerciseCountDownTimer(10000, 1000, 5000, 0, chosenExerciseList.get(currentRep).getName());
                exerciseTimer.start();
                //-- Set image to "Hill Sprint";
                exerciseImageView.setImageDrawable(getResources().getDrawable(R.drawable.exercise_997));
                //-- Set Current Exercise Text to "Hill Sprint"
                currentExerciseTextView.setText("Hill Sprint");
                //-- Set Next Exercise Text to current exercise name
                nextExerciseTextView.setText(chosenExerciseList.get(currentRep).getName());
                Log.d(TAG, "Exiting phase for nextExercise().HILL_SPRINT ... currentPhase = " + currentPhase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                break;
            case DO_EXERCISE:
                Log.d(TAG, "Processing phase for nextExercise().DO_EXERCISE ... currentPhase = " + currentPhase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                //-- 30 second countdown;
                exerciseTimer = new ExerciseCountDownTimer(repLength * 1000, 1000, 5000, ExerciseCountDownTimer.HALFWAY_NOTIFICATION + ExerciseCountDownTimer.QUEUE_INITIAL_NUMBER_WITH_SECOND, "Recover");
                exerciseTimer.start();
                //-- Set image to current exercise image;
                exerciseImageView.setImageDrawable(getResources().getDrawable(chosenExerciseList.get(currentRep).getImage()));
                //-- Set Current Exercise Text to current exercise name
                currentExerciseTextView.setText(chosenExerciseList.get(currentRep).getName());
                //-- Set Next Exercise Text to "Recover"
                nextExerciseTextView.setText("Recover");
                Log.d(TAG, "Exiting phase for nextExercise().DO_EXERCISE ... currentPhase = " + currentPhase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                break;
            case LAST_EXERCISE:
                Log.d(TAG, "Processing phase for nextExercise().LAST_EXERCISE ... currentPhase = " + currentPhase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                //-- 30 second countdown;
                exerciseTimer = new ExerciseCountDownTimer(repLength * 1000, 1000, 5000, ExerciseCountDownTimer.HALFWAY_NOTIFICATION + ExerciseCountDownTimer.QUEUE_INITIAL_NUMBER_WITH_SECOND, "You're Finished!");
                exerciseTimer.start();
                //-- Set image to current exercise image;
                exerciseImageView.setImageDrawable(getResources().getDrawable(chosenExerciseList.get(currentRep).getImage()));
                //-- Set Current Exercise Text to current exercise name
                currentExerciseTextView.setText(chosenExerciseList.get(currentRep).getName());
                //-- Set Next Exercise Text to "Finish"
                nextExerciseTextView.setText("Finish!");
                Log.d(TAG, "Exiting phase for nextExercise().LAST_EXERCISE ... currentPhase = " + currentPhase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                break;
            case SHORT_REST:
                Log.d(TAG, "Processing phase for nextExercise().SHORT_REST ... currentPhase = " + currentPhase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                currentRep++;
                //-- 30 second countdown;
                exerciseTimer = new ExerciseCountDownTimer(repRestLength * 1000, 1000, 5000, ExerciseCountDownTimer.HALFWAY_NOTIFICATION + ExerciseCountDownTimer.QUEUE_INITIAL_NUMBER_WITH_SECOND, "Sprint . . Then " + chosenExerciseList.get(currentRep).getName());
                exerciseTimer.start();
                //-- Set image to "Take a break";
                exerciseImageView.setImageDrawable(getResources().getDrawable(R.drawable.exercise_996));
                //-- Set Current Exercise Text to "Recover"
                currentExerciseTextView.setText("Recover");
                //-- Set Next Exercise Text to "Hill Sprint"
                nextExerciseTextView.setText("Hill Sprint");
                Log.d(TAG, "Exiting phase for nextExercise().SHORT_REST ... currentPhase = " + currentPhase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                break;
            case LONG_REST:
                Log.d(TAG, "Processing phase for nextExercise().LONG_REST ... currentPhase = " + currentPhase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                currentRep = 0;
                currentSet++;
                //-- 60 second countdown;
                exerciseTimer = new ExerciseCountDownTimer(setRestLength * 1000, 1000, 10000, ExerciseCountDownTimer.HALFWAY_NOTIFICATION + ExerciseCountDownTimer.QUEUE_INITIAL_NUMBER_WITH_SECOND, "Sprint . . Then " + chosenExerciseList.get(currentRep).getName());
                exerciseTimer.start();
                //-- Set image to "Take a Break";
                exerciseImageView.setImageDrawable(getResources().getDrawable(R.drawable.exercise_996));
                //-- Set Current Exercise Text to "Take a breather"
                currentExerciseTextView.setText("Take a Breather");
                //-- Set Next Exercise Text to "Hill Sprint"
                nextExerciseTextView.setText("Hill Sprint");
                Log.d(TAG, "Exiting phase for nextExercise().LONG_REST ... currentPhase = " + currentPhase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                break;
            case FINISHED:
                Log.d(TAG, "Processing phase for nextExercise().FINISHED ... currentPhase = " + currentPhase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                //-- Set image to "It's Over";
                exerciseImageView.setImageDrawable(getResources().getDrawable(R.drawable.exercise_995));
                //-- Set Current Exercise Text to "Congratulations"
                currentExerciseTextView.setText("Congratulations");
                //-- Set Next Exercise Text to "You've finished the workout"
                nextExerciseTextView.setText("You've finished the workout");
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                Log.d(TAG, "Exiting phase for nextExercise().FINISHED ... currentPhase = " + currentPhase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                instigatePainButton.setEnabled(true);
                instigatePainButton.setText(R.string.button_go);
                break;
            default:
        }
        Log.d(TAG, "Exiting nextExercise() ... currentPhase = " + currentPhase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
    }

    private int nextPhase(int phase) {
        Log.d(TAG, "Entered nextPhase(int phase) ... phase = " + phase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
        switch (phase) {
            case NOT_STARTED:
                Log.d(TAG, "Processing phase for nextPhase().NOT_STARTED ... phase = " + phase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                currentRep = 0;
                currentSet = 0;
                phase = PRE_EXERCISE;
                break;
            case PRE_EXERCISE:
                Log.d(TAG, "Processing phase for nextPhase().PRE_EXERCISE ... phase = " + phase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                phase = HILL_SPRINT;
                break;
            case HILL_SPRINT:
                Log.d(TAG, "Processing phase for nextPhase().HILL_SPRINT ... phase = " + phase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                if ((currentSet + 1) == totalSets && (currentRep + 1) == totalReps) {
                    phase = LAST_EXERCISE;
                } else {
                    phase = DO_EXERCISE;
                }
                break;
            case DO_EXERCISE:
                Log.d(TAG, "Processing phase for nextPhase().DO_EXERCISE ... phase = " + phase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                if ((currentRep + 1) == totalReps) {
                    phase = LONG_REST;
                    chosenExerciseList = getSet(availableExerciseList);
                } else {
                    phase = SHORT_REST;
                }
                break;
            case LAST_EXERCISE:
                Log.d(TAG, "Processing phase for nextPhase().LAST_EXERCISE ... phase = " + phase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                phase = FINISHED;
                break;
            case SHORT_REST:
                Log.d(TAG, "Processing phase for nextPhase().SHORT_REST ... phase = " + phase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                phase = HILL_SPRINT;
                break;
            case LONG_REST:
                Log.d(TAG, "Processing phase for nextPhase().LONG_REST ... phase = " + phase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                phase = HILL_SPRINT;
                break;
            case FINISHED:
                Log.d(TAG, "Processing phase for nextPhase().FINISHED ... phase = " + phase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
                phase = NOT_STARTED;
                break;
            default:
        }
        Log.d(TAG, "Exiting nextPhase(int phase) ... phase = " + phase + "|currentRep = " + currentRep + "/" + totalReps + "|currentSet = " + currentSet + "/" + totalSets);
        return phase;
    }

    private class ExerciseCountDownTimer {
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
        private boolean passedHalfway;

        public ExerciseCountDownTimer(long pMillisInFuture, long pCountDownInterval, long pCountDownStart, int pType, String sFinish) {
            this.millisInFuture = pMillisInFuture;
            this.countDownInterval = pCountDownInterval;
            this.countDownStart = pCountDownStart;
            this.finish = sFinish;
            this.type = pType;
            if ((type & HALFWAY_NOTIFICATION) == HALFWAY_NOTIFICATION) {
                this.halfwayMillis = millisInFuture / 2;
                passedHalfway = false;
            } else {
                this.halfwayMillis = millisInFuture + 1000;
                passedHalfway = true;
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
                        instigatePainButton.setText("... " + sec + " ...");
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
                        if (millisInFuture <= halfwayMillis && !passedHalfway) {
                            countdownSpeaker.speak("Halfway Point", TextToSpeech.QUEUE_FLUSH, null);
                            passedHalfway = true;
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
