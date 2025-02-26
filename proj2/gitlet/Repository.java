package gitlet;

import java.io.File;
import java.util.Date;
import java.util.Formatter;
import java.util.TreeMap;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Jason Ho
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /* TODO: fill in the rest of this class. */
    /** The commits directory */
    public static final File commitsFile = join(GITLET_DIR, "commits");
    /** The blobs directory */
    public static final File blobsFile = join(GITLET_DIR, "blobs");
    /** The addStaged directory */
    public static final File addFile = join(GITLET_DIR, "add");
    /** The rmStaged directory */
    public static final File rmFile = join(GITLET_DIR, "rm");
    /** The head pointer */
    public static String head;
    /** the branches */
    public static TreeMap<String,String> branches;
    public static String currentBranch;
    /** staged for additions */
    public static TreeMap<String,String> addStaged;
    /** staged for removal */
    public static TreeMap<String,String> rmStaged;


    public static void init() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            Commit initCommit = new Commit("initial commit", new Date(0), null, null, null);
            byte[] serializedCommit = Utils.serialize(initCommit);
            String commitHash = Utils.sha1(serializedCommit);
            initCommit.setUid(commitHash);
            File currentCommitFile = join(commitsFile, commitHash);
            writeObject(currentCommitFile, initCommit);
            branches = new TreeMap<>();
            currentBranch = "master";
            head = commitHash;
            branches.put(currentBranch, commitHash);
            blobsFile.mkdir();
            addFile.mkdir();
            rmFile.mkdir();
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }

    public static void add(String fileName) {
        File addFile = join(CWD, fileName);
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        byte[] fileByte = readContents(addFile);
        String fileHash = Utils.sha1(fileByte);
        /** If the staged for addition has the file is same to the add file,don't add it */
        boolean sameFileInStage = addStaged.containsKey(fileName) && addStaged.get(fileName).equals(fileHash);
        if (sameFileInStage) return;
        File currentCommitFile = join(commitsFile, head);
        Commit currentCommit = Utils.readObject(currentCommitFile, Commit.class);
        boolean sameFileInCommit = currentCommit.getBlobs().containsKey(fileName) && currentCommit.getBlobs().get(fileName).equals(fileHash);
        if (sameFileInCommit && addStaged.containsKey(fileName)) {
            addStaged.remove(fileName);
            Utils.writeObject(addFile, addStaged);
            return;
        } else if(sameFileInCommit) return;
        rmStaged.remove(fileName);
        Utils.writeObject(addFile, rmStaged);
        addStaged.put(fileName, fileHash);
        Utils.writeObject(addFile, addStaged);
    }
}