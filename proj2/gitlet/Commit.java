package gitlet;

// TODO: any imports you need here

import javax.xml.crypto.Data;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.Formatter;
import java.util.TreeMap;

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
    private String message;
    /** THe author of this Commit.*/
    private String author;
    /** THe timestamp of this Commit.*/
    private Date date;
    /** THe hash of this Commit.*/
    private String uid;
    /** THe hash of this Commit's parent 1.*/
    private String parents1;
    /** THe hash of this Commit's parent 2.*/
    private String parents2;
    /** THe blobs of this Commit.*/
    private TreeMap<String,String> blobs;


    /* TODO: fill in the rest of this class. */
    Commit(String message, Date date, String parents1, String parents2, TreeMap<String,String> blobs) {
        this.date = date;
        this.message = message;
        this.blobs = blobs;
        this.parents1 = parents1;
        this.parents2 = parents2;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public TreeMap<String,String> getBlobs() {
        return this.blobs;
    }
}