package uk.co.stevegiller.deadmeatgf.hillsprintsworkout;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class HillSprintActivity extends ActionBarActivity {
    
    private ArrayList<Excercise> fullExerciseList;
    private ArrayList<Excercise> chosenExerciseList;
    
    private boolean intermediate;
    private boolean expert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hill_sprint);
        //-- These need to be collected from Preferences
        intermediate = false;
        expert = false;
        getExercises();
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
        int[] standard_images = getResources().getIntArray(R.array.standard_exercise_images);
        int[] intermediate_images = getResources().getIntArray(R.array.intermediate_exercise_images);
        int[] expert_images = getResources().getIntArray(R.array.expert_exercise_images);
        for(int loop = 0; loop < standard_exercises.length; loop++) {
            fullExerciseList.add(new Excercise(standard_exercises[loop], standard_images[loop], "", 0));
            chosenExerciseList.add(new Excercise(standard_exercises[loop], standard_images[loop], "", 0));
        }
        for (int loop = 0; loop < intermediate_exercises.length; loop++) {
            fullExerciseList.add(new Excercise(intermediate_exercises[loop], intermediate_images[loop], "", 0));
            if(intermediate) {
                chosenExerciseList.add(new Excercise(intermediate_exercises[loop], intermediate_images[loop], "", 0));
            }
        }
        for (int loop = 0; loop < expert_exercises.length; loop++) {
            fullExerciseList.add(new Excercise(expert_exercises[loop], expert_images[loop], "", 0));
            if(expert) {
                chosenExerciseList.add(new Excercise(expert_exercises[loop], expert_images[loop], "", 0));
            }
        }
    } 
}
