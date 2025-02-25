package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
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
    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");
    /** The blobs directory */
    public static final File BLOB_DIR = join(GITLET_DIR, "blobs");
    /** The staged for addition directory */
    public static final File ADD_DIR = join(GITLET_DIR, "add");
    /** The add staged File to store the staged for addition data */
    public static final File ADD_FILE = join(ADD_DIR, "addStaged");
    /** The staged for removal directory */
    public static final File RM_DIR = join(GITLET_DIR, "rm");
    /** The remove staged File to store the staged for addition data */
    public static final File RM_FILE = join(RM_DIR, "addStaged");

    /** The head pointer */
    public static String head;
    /** the branches */
    public static TreeMap<String,String> branches;
    public static String currentBranch;
    /** staged for additions */
    public static TreeMap<String,String> addStaged;
    /** staged for removal */
    public static TreeMap<String,String> rmStaged;


    public static void init() throws IOException {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }

        GITLET_DIR.mkdir();
        Commit initCommit = new Commit("initial commit", new Date(0), null, null, null);
        byte[] serializedCommit = Utils.serialize(initCommit);
        String commitHash = Utils.sha1(serializedCommit);
        initCommit.setUid(commitHash);

        COMMIT_DIR.mkdir();
        File currentCommitFile = join(COMMIT_DIR, commitHash);
        currentCommitFile.createNewFile();
        writeObject(currentCommitFile, initCommit);

        BLOB_DIR.mkdir();
        ADD_DIR.mkdir();
        RM_DIR.mkdir();
        addStaged = new TreeMap<>();
        rmStaged = new TreeMap<>();
        branches = new TreeMap<>();
        currentBranch = "master";
        head = commitHash;
        branches.put(currentBranch, commitHash);
    }

    public static void add(String fileName) throws IOException {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            return;
        }

        File addFile = join(CWD, fileName);
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        byte[] fileByte = readContents(addFile);
        String fileHash = Utils.sha1(fileByte);
        /** If the staged for addition has the file is same to the add file,don't add it */
        boolean sameFileInStage = addStaged.containsKey(fileName) && addStaged.get(fileName).equals(fileHash);
        if (sameFileInStage) return;
        File currentCommitFile = join(COMMIT_DIR, head);
        Commit currentCommit = Utils.readObject(currentCommitFile, Commit.class);
        /** If the current version has the file is same to the add file
         * however it's different to the file in the staged for addition
         * remove the file in the stage for addition
         * or there is no file in stage for addition have the same name to it
         * don't add it
         * */
        boolean sameFileInCommit = currentCommit.getBlobs() != null && currentCommit.getBlobs().containsKey(fileName) && currentCommit.getBlobs().get(fileName).equals(fileHash);
        if (sameFileInCommit && addStaged.containsKey(fileName)) {
            addStaged.remove(fileName);
            Utils.writeObject(ADD_FILE, addStaged);
            Utils.restrictedDelete(fileHash);
            return;
        } else if(sameFileInCommit) return;
        /** If the staged for addition has the same file to the addFile,
         * remove it from the staged for addition
         * */
        if (rmStaged.containsKey(fileName)) {
            rmStaged.remove(fileName);
            Utils.writeObject(RM_FILE, rmStaged);
            Utils.restrictedDelete(fileHash);
        }
        addStaged.put(fileName, fileHash);
        Utils.writeObject(ADD_FILE, addStaged);
        /** If there is not a same content file in staged for addition
         * created a new file,
         * otherwise the same content file will use the same hash file
         * in staged for addition
         * */
        File addedFile = join(ADD_DIR, fileHash);
        if (!addedFile.exists()) {
            addedFile.createNewFile();
            byte[] fileContent = Utils.readContents(addFile);
            Utils.writeContents(addedFile, fileContent);
        }
    }
}