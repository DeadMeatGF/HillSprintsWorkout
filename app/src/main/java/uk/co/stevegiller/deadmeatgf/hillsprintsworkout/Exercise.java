package uk.co.stevegiller.deadmeatgf.hillsprintsworkout;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import java.lang.reflect.Array;

/**
 * Created by DeadMeatGF on 14/03/2015.
 */
public class Exercise {

    private String mName;
    private int mImage;
    private int mThumb;
    private String mDescription;
    private int mSequence;
    private boolean mSelected;

    public Exercise(String name, int image, int thumb, String description, int sequence, boolean selected) {
        this.mName = name;
        this.mImage = image;
        this.mDescription = description;
        this.mThumb = thumb;
        this.mSequence = sequence;
        this.mSelected = selected;
    }

    @Override
    public String toString() {
        return mName;
    }

    public int getImage() {
        return mImage;
    }

    public void setImage(int mImage) {
        this.mImage = mImage;
    }

    public int getThumb() {
        return mThumb;
    }

    public void setThumb(int thumb) {
        this.mThumb = thumb;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public int getSequence() {
        return mSequence;
    }

    public void setSequence(int mSequence) {
        this.mSequence = mSequence;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        this.mSelected = selected;
    }
}
