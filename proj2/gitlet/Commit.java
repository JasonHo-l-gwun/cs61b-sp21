package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/** Represents a Gitlet commit object.
 * This class stores commit message, date, parent commit hashes, and a mapping
 * from filenames to blob hashes.
 *
 * @author Jason Ho
 */
public class Commit implements Serializable {

    /** The message of this commit. */
    private final String message;
    /** The timestamp of this commit. */
    private final Date date;
    /** The hash (UID) of this commit. */
    private String uid;
    /** The hash of this commit's parent. */
    private final ArrayList<String> parent;
    /** The blobs mapping (filename -> blob hash) of this commit. */
    private final TreeMap<String, String> blobs;

    Commit(String message, Date date, ArrayList<String> parent, TreeMap<String, String> blobs) {
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

    public TreeMap<String, String> getBlobs() {
        return (blobs == null) ? new TreeMap<>() : blobs;
    }

    public ArrayList<String> getParents() {
        if (this.parent == null) {
            return new ArrayList<>();
        }
        return this.parent;
    }

    public String getMessage() {
        return this.message;
    }

    public Date getDate() {
        return this.date;
    }
}