package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    public static final File HEAD_FILE = join(GITLET_DIR, "head");
    public static final File BRANCHES_FILE = join(GITLET_DIR, "branches");

    public static void init() throws IOException {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }

        GITLET_DIR.mkdir();
        Commit initCommit = new Commit("initial commit", new Date(0), null, null, null);
        initCommit.setUid();
        String commitHash = initCommit.getUid();
        COMMIT_DIR.mkdir();
        File currentCommitFile = join(COMMIT_DIR, commitHash);
        currentCommitFile.createNewFile();
        writeObject(currentCommitFile, initCommit);

        /** Initial the directory and file */
        BLOB_DIR.mkdir();
        ADD_DIR.mkdir();
        RM_DIR.mkdir();
        HEAD_FILE.createNewFile();
        TreeMap<String,String> addStaged = new TreeMap<>();
        Utils.writeObject(ADD_FILE, addStaged);
        TreeMap<String,String> rmStaged = new TreeMap<>();
        Utils.writeObject(RM_FILE, rmStaged);
        TreeMap<String,String> branches = new TreeMap<>();
        branches.put("master", commitHash);
        Utils.writeObject(BRANCHES_FILE, branches);
        Utils.writeObject(HEAD_FILE, commitHash);
        branches.put("master", commitHash);
        Utils.writeObject(HEAD_FILE, branches);
    }

    public static void add(String fileName) throws IOException {
        hasGitletDir();

        File addFile = join(CWD, fileName);
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        TreeMap<String,String> addStaged = getAddStaged();
        TreeMap<String,String> rmStaged = getRmStaged();
        byte[] fileByte = readContents(addFile);
        String fileHash = Utils.sha1(fileByte);
        /** If the staged for addition has the file is same to the add file,don't add it */
        boolean sameFileInStage = addStaged.containsKey(fileName) && addStaged.get(fileName).equals(fileHash);
        if (sameFileInStage) return;
        Commit currentCommit = getCurrentCommit();
        /** If the current version has the file is same to the add file
         * however it's different to the file in the staged for addition
         * remove the file in the stage for addition
         * or there is no file in stage for addition have the same name to it
         * don't add it
         * */
        boolean sameFileInCommit = currentCommit.getBlobs().containsKey(fileName)
                                            && currentCommit.getBlobs().get(fileName).equals(fileHash);
        if (sameFileInCommit && addStaged.containsKey(fileName)) {
            addStaged.remove(fileName);
            Utils.writeObject(ADD_FILE, addStaged);
            File stagedFile = join(ADD_DIR, fileName);
            Utils.restrictedDelete(stagedFile);
            return;
        } else if(sameFileInCommit) return;
        /** If the staged for addition has the same file to the addFile,
         * remove it from the staged for addition
         * */
        if (rmStaged.containsKey(fileName)) {
            rmStaged.remove(fileName);
            Utils.writeObject(RM_FILE, rmStaged);
            File stagedFile = join(RM_DIR, fileName);
            Utils.restrictedDelete(stagedFile);
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

    public static void commit(String message) throws IOException {
        hasGitletDir();
        TreeMap<String,String> addStaged = getAddStaged();
        TreeMap<String,String> rmStaged = getRmStaged();
        if (addStaged.isEmpty() && rmStaged.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        String head = getHead();
        Date currentDate = new Date();
        /** Create a new map to store the new blobs for the new commit */
        Commit currentCommit = getCurrentCommit();
        TreeMap<String,String> newBlobs = new TreeMap<>(currentCommit.getBlobs());
        /** Update the map according to the stage area */
        for (String fileName : addStaged.keySet()) {
            String fileHash = addStaged.get(fileName);
            newBlobs.put(fileName, fileHash);
            File file = join(ADD_DIR, fileHash);
            Utils.restrictedDelete(file);
            File blob = join(BLOB_DIR, fileHash);
            if (!blob.exists()) {
                blob.createNewFile();
                byte[] fileContent = Utils.readContents(file);
                Utils.writeContents(blob, fileContent);
            }
        }
        for (String fileName : rmStaged.keySet()) {
            String fileHash = rmStaged.get(fileName);
            newBlobs.remove(fileName);
            File file = join(RM_DIR, fileHash);
            Utils.restrictedDelete(file);
        }
        /** Create the new commit object*/
        Commit newCommit = new Commit(message, currentDate, head, null, newBlobs);
        /** Write it to the file */
        newCommit.setUid();
        String commitHash = newCommit.getUid();
        File newCommitFile = join(COMMIT_DIR, commitHash);
        Utils.writeObject(newCommitFile, newCommit);
        /** Update the head */
        head = commitHash;
        Utils.writeObject(HEAD_FILE, head);
        /** Update the stage area */
        addStaged.clear();
        Utils.writeObject(ADD_FILE, addStaged);
        rmStaged.clear();
        Utils.writeObject(RM_FILE, rmStaged);
    }

    public static void rm(String fileName) {
        hasGitletDir();
        TreeMap<String,String> addStaged = getAddStaged();
        TreeMap<String,String> rmStaged = getRmStaged();
        Commit currentCommit = getCurrentCommit();
        TreeMap<String,String> trackedFiles = currentCommit.getBlobs();

        boolean isStaged = addStaged.containsKey(fileName);
        boolean isTrack = trackedFiles.containsKey(fileName);

        if (!isTrack && !isStaged) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        if (isStaged) {
            addStaged.remove(fileName);
            saveAddStaged(addStaged);
        }
        if (isTrack) {
            rmStaged.put(fileName, trackedFiles.get(fileName));
            saveRmStaged(rmStaged);
            File fileInCWD = join(CWD, fileName);
            if (fileInCWD.exists()) {
                restrictedDelete(fileInCWD);
            }
        }
    }

    public static void log() {
        Commit commit = getCurrentCommit();
        while (commit != null) {
            printLog(commit);
            commit = getParentCommit(commit);
        }
    }

    public static void globalLog() {
        List<String> commitList = Utils.plainFilenamesIn(COMMIT_DIR);
        for (int i = 0; i < commitList.size(); i++) {
            Commit commit = getCommit(commitList.get(i));
            printLog(commit);
        }
    }

    public static void find(String message) {
        List<String> commitList = Utils.plainFilenamesIn(COMMIT_DIR);
        boolean isFound = false;
        for (int i = 0; i < commitList.size(); i++) {
            Commit commit = getCommit(commitList.get(i));
            if (commit.getMessage().equals(message)) {
                isFound = true;
                System.out.println(commit.getUid());
            }
        }
        if (!isFound) System.out.println("Found no commit with that message.");
    }




    /** ----------------------------------Helper function------------------------------- */
    /** Judge if a .gitlet folder exists */
    private static void hasGitletDir() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
    /** To get the staged for addition */
    private static TreeMap<String, String> getAddStaged() {
        if (!ADD_FILE.exists()) return new TreeMap<>();
        return Utils.readObject(ADD_FILE, TreeMap.class);
    }

    /** To save the staged for addition */
    private static void saveAddStaged(TreeMap<String,String> addStaged) {
        Utils.writeObject(ADD_FILE, addStaged);
    }

    /** To get the staged for addition */
    private static TreeMap<String, String> getRmStaged() {
        if (!ADD_FILE.exists()) return new TreeMap<>();
        return Utils.readObject(RM_FILE, TreeMap.class);
    }

    /** To save the staged for removal */
    private static void saveRmStaged(TreeMap<String,String> rmStaged) {
        Utils.writeObject(RM_FILE, rmStaged);
    }

    /** To get the head pointer */
    private static String getHead() {
        if (!HEAD_FILE.exists()) return null;
        return Utils.readObject(HEAD_FILE, String.class);
    }

    /** To get the head pointer current*/
    private static Commit getCurrentCommit() {
        String currentCommitUid = Utils.readObject(HEAD_FILE, String.class);
        return getCommit(currentCommitUid);
    }

    /** Pass in a Commit object and return a object which is its parent */
    private static Commit getParentCommit(Commit commit) {
        String parentCommitUid = commit.getParent1();
        if (parentCommitUid == null) return null;
        File parentCommitFile = Utils.join(COMMIT_DIR, parentCommitUid);
        return Utils.readObject(parentCommitFile, Commit.class);
    }

    private static Commit getCommit(String commitUid) {
        if (commitUid == null) return null;
        File commitFile = Utils.join(COMMIT_DIR, commitUid);
        return Utils.readObject(commitFile, Commit.class);
    }
    /** Print the log for commit */
    private static void printLog(Commit commit) {
        System.out.print("===");
        System.out.print("commit " + commit.getUid());
        if (commit.getParent2() != null) {
            System.out.println("Merge: " + commit.getParent1().substring(0,7) + commit.getParent2().substring(0,7));
        }
        Formatter formatter = new Formatter(Locale.US);
        String dateStr = formatter.format(
                "%1$ta %1$tb %1$td %1$tT %1$tY %1$tz",
                commit.getDate()
        ).toString();
        System.out.print("Date: " + dateStr);
        System.out.println(commit.getMessage());
    }
}

