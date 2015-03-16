package uk.co.stevegiller.deadmeatgf.hillsprintsworkout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Giller_S on 16/03/2015.
 */
public class ExerciseAdapter extends ArrayAdapter<Exercise> {
    LayoutInflater layoutInflater;
    Context ctx;

    public ExerciseAdapter(Context context, int layoutResourceId, ArrayList<Exercise> exercises) {
        super(context, layoutResourceId, exercises);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.ctx = context;
    }

    private class ViewHolder {
        ImageView exerciseImageView;
        TextView exerciseTextView;
        CheckBox exerciseCheckBox;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Exercise exercise = getItem(position);

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.exercise_row, null);
            holder = new ViewHolder();
            holder.exerciseCheckBox = (CheckBox) convertView.findViewById(R.id.exerciseCheckBox);
            holder.exerciseTextView = (TextView) convertView.findViewById(R.id.exerciseTextView);
            holder.exerciseImageView = (ImageView) convertView.findViewById(R.id.exerciseImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.exerciseCheckBox.setSelected(exercise.isSelected());
        holder.exerciseTextView.setText(exercise.getName());
        holder.exerciseImageView.setImageDrawable(exercise.getThumb());

        return convertView;
    }
}
