package uk.co.stevegiller.deadmeatgf.hillsprintsworkout;

import android.graphics.drawable.Drawable;

/**
 * Created by DeadMeatGF on 14/03/2015.
 */
public class Exercise {

    private String mName;
    private Drawable mImage;
    private Drawable mThumb;
    private String mDescription;
    private int mSequence;
    private boolean mSelected;

    public Exercise(String name, Drawable image, Drawable thumb, String description, int sequence, boolean selected) {
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

    public Drawable getImage() {
        return mImage;
    }

    public void setImage(Drawable mImage) {
        this.mImage = mImage;
    }

    public Drawable getThumb() {
        return mThumb;
    }

    public void setThumb(Drawable thumb) {
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
