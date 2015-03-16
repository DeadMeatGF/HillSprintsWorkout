package uk.co.stevegiller.deadmeatgf.hillsprintsworkout;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class HillSprintActivity extends ActionBarActivity {
    public static final String TAG = "HillSprintActivity";

    private ListView allExercisesListView;
    private ListView chosenExercisesListView;
    
    private ArrayList<Exercise> fullExerciseList;
    private ArrayList<Exercise> chosenExerciseList;
    
    private boolean intermediate;
    private boolean expert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hill_sprint);
        allExercisesListView = (ListView) findViewById(R.id.listView);
        chosenExercisesListView = (ListView) findViewById(R.id.listView2);
        //-- These need to be collected from Preferences
        intermediate = false;
        expert = false;
        getExercises();
        ExerciseAdapter allExercisesAdapter = new ExerciseAdapter(this, R.layout.exercise_row, fullExerciseList);
        ExerciseAdapter chosenExercisesAdapter = new ExerciseAdapter(this, R.layout.exercise_row, chosenExerciseList);
        allExercisesListView.setAdapter(allExercisesAdapter);
        chosenExercisesListView.setAdapter(chosenExercisesAdapter);
        allExercisesAdapter.notifyDataSetChanged();
        chosenExercisesAdapter.notifyDataSetChanged();
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
        int[] standard_images = getResources().getIntArray(R.array.standard_exercise_images);
        int[] intermediate_images = getResources().getIntArray(R.array.intermediate_exercise_images);
        int[] expert_images = getResources().getIntArray(R.array.expert_exercise_images);
        for(int loop = 0; loop < standard_exercises.length; loop++) {
            fullExerciseList.add(new Exercise(standard_exercises[loop], standard_images[loop], "", 0, true));
            chosenExerciseList.add(new Exercise(standard_exercises[loop], standard_images[loop], "", 0, true));
            Log.d(TAG, "Added " + standard_exercises[loop] + " to both lists ...");
        }
        for (int loop = 0; loop < intermediate_exercises.length; loop++) {
            if(intermediate) {
                fullExerciseList.add(new Exercise(intermediate_exercises[loop], intermediate_images[loop], "", 0, true));
                chosenExerciseList.add(new Exercise(intermediate_exercises[loop], intermediate_images[loop], "", 0, true));
                Log.d(TAG, "Added " + intermediate_exercises[loop] + " to both lists ...");
            } else {
                fullExerciseList.add(new Exercise(intermediate_exercises[loop], intermediate_images[loop], "", 0, false));
                Log.d(TAG, "Added " + intermediate_exercises[loop] + " to full list but not chosen list ...");
            }
        }
        for (int loop = 0; loop < expert_exercises.length; loop++) {
            if(expert) {
                fullExerciseList.add(new Exercise(expert_exercises[loop], expert_images[loop], "", 0, true));
                chosenExerciseList.add(new Exercise(expert_exercises[loop], expert_images[loop], "", 0, true));
                Log.d(TAG, "Added " + expert_exercises[loop] + " to both lists ...");
            } else {
                fullExerciseList.add(new Exercise(expert_exercises[loop], expert_images[loop], "", 0, false));
                Log.d(TAG, "Added " + expert_exercises[loop] + " to full list but not chosen list ...");
            }
        }
    } 
}
