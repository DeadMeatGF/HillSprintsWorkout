package uk.co.stevegiller.deadmeatgf.hillsprintsworkout;

/**
 * Created by DeadMeatGF on 14/03/2015.
 */
public class Excercise {

    private String mName;
    private int mImage;
    private String mDescription;
    private int mSequence;

    public Excercise(String name, int image, String description, int sequence) {
        this.mName = name;
        this.mImage = image;
        this.mDescription = description;
        this.mSequence = sequence;
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
}
