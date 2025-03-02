package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Jason Ho
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private final String message;
    /** THe author of this Commit.*/
    private String author;
    /** THe timestamp of this Commit.*/
    private final Date date;
    /** THe hash of this Commit.*/
    private String uid;
    /** THe hash of this Commit's parent 1.*/
    private final ArrayList<String> parent;
    /** THe hash of this Commit's parent 2.*/
    // private final String parents2;
    /** THe blobs of this Commit.*/
    private final TreeMap<String,String> blobs;


    /* TODO: fill in the rest of this class. */
    Commit(String message, Date date, ArrayList<String> parent, TreeMap<String,String> blobs) {
        this.date = date;
        this.message = message;
        this.blobs = blobs;
        this.parent = parent;
    }

    public void setUid() {
        List<Object> list = new ArrayList<>();
        list.add(Utils.serialize(message));
        list.add(Utils.serialize(date));
        list.add(Utils.serialize(parent));
        list.add(Utils.serialize(blobs));
        this.uid = Utils.sha1(list);
    }

    public String getUid() {
        return uid;
    }

    public TreeMap<String,String> getBlobs() {
        return blobs == null ? new TreeMap<>() : blobs;
    }

    public ArrayList<String> getParents() {
        if (this.parent == null) return new ArrayList<String>();
        return this.parent;
    }

    public String getMessage() {
        return this.message;
    }

    public Date getDate() {
        return  this.date;
    }
}