package gitlet;

import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author Jason Ho
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
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
    /** The remove staged File to store the staged for removal data */
    public static final File RM_FILE = join(RM_DIR, "rmStaged");
    /** The branch file to store every branch's data */
    public static final File BRANCHES_FILE = join(GITLET_DIR, "branches");
    /** The current branch file to store the current branch data */
    public static final File CURRENT_BRANCH_FILE = join(GITLET_DIR, "currentBranch");
    /** The file to store the remote information */
    public static final File REMOTE_FILE = join(GITLET_DIR, "remote");

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        Commit initCommit = new Commit("initial commit", new Date(0), null, null);
        initCommit.setUid();
        String commitHash = initCommit.getUid();
        COMMIT_DIR.mkdir();
        File currentCommitFile = join(COMMIT_DIR, commitHash);
        writeObject(currentCommitFile, initCommit);
        /** Initial the directory and file */
        BLOB_DIR.mkdir();
        ADD_DIR.mkdir();
        RM_DIR.mkdir();
        saveAddStaged(new TreeMap<String, String>());
        saveRmStaged(new TreeMap<String, String>());
        TreeMap<String, String> branches = new TreeMap<>();
        branches.put("master", commitHash);
        saveBranches(branches);
        setCurrentBranch("master");
    }

    public static void add(String fileName) {
        hasGitletDir();
        File addFile = join(CWD, fileName);
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        TreeMap<String, String> addStaged = getAddStaged();
        TreeMap<String, String> rmStaged = getRmStaged();
        String fileHash = getFileHash(addFile);
        /** If the staged for addition has the file is same to the add file, don't add it */
        boolean sameFileInStage = addStaged.containsKey(fileName)
                                    && addStaged.get(fileName).equals(fileHash);
        if (sameFileInStage) {
            return;
        }
        /** If the current version has the file is same to the add file
         *  however it's different to the file in the staged for addition,
         *  remove the file in the stage for addition,
         *  or if there is a file in the stage for addition with the same name,
         *  don't add it.
         */
        boolean sameFileInCommit = getCurrentCommitBlobs().containsKey(fileName)
                && getCurrentCommitBlobs().get(fileName).equals(fileHash);
        if (sameFileInCommit && addStaged.containsKey(fileName)) {
            String stagedHash = addStaged.get(fileName);
            addStaged.remove(fileName);
            saveAddStaged(addStaged);
            File stagedFile = join(ADD_DIR, stagedHash);
            stagedFile.delete();
            return;
        }
        /** If the staged for removal has the same file as the addFile,
         *  remove it from the staged for removal.
         */
        if (rmStaged.containsKey(fileName)) {
            String stagedHash = rmStaged.get(fileName);
            rmStaged.remove(fileName);
            saveRmStaged(rmStaged);
            File stagedFile = join(RM_DIR, stagedHash);
            stagedFile.delete();
        }
        if (sameFileInCommit) {
            return;
        }
        addStaged.put(fileName, fileHash);
        saveAddStaged(addStaged);
        /** If there is not a same content file in staged for addition,
         *  create a new file; otherwise the same content file will use the same hash file
         *  in staged for addition.
         */
        File addedFile = join(ADD_DIR, fileHash);
        if (!addedFile.exists()) {
            writeToFile(addFile, addedFile);
        }
    }

    public static void commit(String message) {
        hasGitletDir();
        TreeMap<String, String> addStaged = getAddStaged();
        TreeMap<String, String> rmStaged = getRmStaged();
        if (addStaged.isEmpty() && rmStaged.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        Date currentDate = new Date();
        /** Create a new map to store the new blobs for the new commit */
        String currentCommitUid = getCurrentCommitUid();
        TreeMap<String, String> newBlobs = new TreeMap<>(getCurrentCommitBlobs());
        /** Update the map according to the stage area */
        updateBlobs(newBlobs);
        /** Create the new commit object */
        ArrayList<String> parents = new ArrayList<>();
        parents.add(currentCommitUid);
        Commit newCommit = new Commit(message, currentDate, parents, newBlobs);
        /** Write it to the file */
        newCommit.setUid();
        saveCommit(newCommit);
        /** Update the branches */
        String currentBranchName = getCurrentBranchName();
        TreeMap<String, String> branches = getBranches();
        String commitHash = newCommit.getUid();
        branches.put(currentBranchName, commitHash);
        saveBranches(branches);
        /** Update the stage area */
        saveAddStaged(new TreeMap<String, String>());
        saveRmStaged(new TreeMap<String, String>());
    }

    public static void rm(String fileName) {
        hasGitletDir();
        TreeMap<String, String> addStaged = getAddStaged();
        TreeMap<String, String> rmStaged = getRmStaged();
        TreeMap<String, String> trackedFiles = getCurrentCommitBlobs();
        boolean isStaged = addStaged.containsKey(fileName);
        boolean isTracked = trackedFiles.containsKey(fileName);
        if (!isTracked && !isStaged) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        if (isStaged) {
            addStaged.remove(fileName);
            saveAddStaged(addStaged);
        }
        if (isTracked) {
            rmStaged.put(fileName, trackedFiles.get(fileName));
            saveRmStaged(rmStaged);
            removeFile(fileName);
        }
    }

    public static void log() {
        hasGitletDir();
        Commit commit = getCurrentCommit();
        while (commit != null) {
            printLog(commit);
            commit = getParentCommit(commit);
        }
    }

    public static void globalLog() {
        hasGitletDir();
        List<String> commitList = getCommitList();
        for (int i = 0; i < commitList.size(); i++) {
            Commit commit = getCommit(commitList.get(i));
            printLog(commit);
        }
    }

    public static void find(String message) {
        hasGitletDir();
        List<String> commitList = getCommitList();
        boolean isFound = false;
        for (int i = 0; i < commitList.size(); i++) {
            Commit commit = getCommit(commitList.get(i));
            if (commit.getMessage().equals(message)) {
                isFound = true;
                System.out.println(commit.getUid());
            }
        }
        if (!isFound) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        hasGitletDir();
        TreeMap<String, String> branches = getBranches();
        String currentBranch = getCurrentBranchName();
        TreeMap<String, String> stagedFiles = getAddStaged();
        TreeMap<String, String> rmFiles = getRmStaged();
        TreeMap<String, String> trackedFiles = getCurrentCommitBlobs();
        System.out.println("=== Branches ===");
        for (String branch : branches.keySet()) {
            if (branch.equals(currentBranch)) {
                System.out.print("*");
            }
            System.out.println(branch);
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (String fileName : stagedFiles.keySet()) {
            System.out.println(fileName);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String fileName : rmFiles.keySet()) {
            System.out.println(fileName);
        }
        System.out.println();
        List<String> workspaceFiles = getWorkspaceFiles();
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String fileName : workspaceFiles) {
            File file = Utils.join(CWD, fileName);
            boolean isTracked = trackedFiles.containsKey(fileName);
            boolean isAdded = stagedFiles.containsKey(fileName);
            String fileHash = getFileHash(file);
            boolean isModified = (isTracked && !fileHash.equals(trackedFiles.get(fileName)))
                    || (isAdded && !fileHash.equals(stagedFiles.get(fileName)));
            if (isModified) {
                System.out.println(fileName + " (modified)");
            }
        }
        for (String fileName : stagedFiles.keySet()) {
            if (!workspaceFiles.contains(fileName)) {
                System.out.println(fileName + " (deleted)");
            }
        }
        for (String fileName : trackedFiles.keySet()) {
            if (!workspaceFiles.contains(fileName) && !rmFiles.containsKey(fileName)) {
                System.out.println(fileName + " (deleted)");
            }
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        for (String fileName : workspaceFiles) {
            if (!stagedFiles.containsKey(fileName) && !trackedFiles.containsKey(fileName)) {
                System.out.println(fileName);
            }
        }
        System.out.println();
    }

    public static void checkout1(String fileName) {
        TreeMap<String, String> currentCommitBlobs = getCurrentCommitBlobs();
        if (!currentCommitBlobs.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String readFileHash = currentCommitBlobs.get(fileName);
        File readFile = Utils.join(BLOB_DIR, readFileHash);
        File writeFile = Utils.join(CWD, fileName);
        writeToFile(readFile, writeFile);
    }

    public static void checkout2(String uid1, String fileName) {
        String uid = resolveShortCommitID(uid1);
        Commit commit = getCommit(uid);
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        TreeMap<String, String> commitBlobs = commit.getBlobs();
        if (!commitBlobs.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String readFileHash = commitBlobs.get(fileName);
        File readFile = Utils.join(BLOB_DIR, readFileHash);
        File writeFile = Utils.join(CWD, fileName);
        writeToFile(readFile, writeFile);
    }

    public static void checkout3(String branchName) {
        TreeMap<String, String> branches = getBranches();
        String currentBranch = getCurrentBranchName();
        if (!branches.containsKey(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (branchName.equals(currentBranch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        String uid = branches.get(branchName);
        checkout(uid);
        setCurrentBranch(branchName);
        saveAddStaged(new TreeMap<String, String>());
        saveRmStaged(new TreeMap<String, String>());
    }

    public static void branch(String branchName) {
        TreeMap<String, String> branches = getBranches();
        if (branches.containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        branches.put(branchName, getCurrentCommitUid());
        saveBranches(branches);
    }

    public static void rmBranch(String branchName) {
        if (branchName.equals(getCurrentBranchName())) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        TreeMap<String, String> branches = getBranches();
        if (!branches.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        branches.remove(branchName);
        saveBranches(branches);
    }

    public static void reset(String uid) {
        Commit commit = getCommit(uid);
        if (commit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        checkout(uid);
        TreeMap<String, String> branches = getBranches();
        branches.put(getCurrentBranchName(), uid);
        saveBranches(branches);
    }

    public static void merge(String branchName) {
        TreeMap<String, String> addition = getAddStaged();
        TreeMap<String, String> removal = getRmStaged();
        if (!addition.isEmpty() || !removal.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        TreeMap<String, String> branches = getBranches();
        if (!branches.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String currentBranch = getCurrentBranchName();
        if (currentBranch.equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        String head1 = getCurrentCommitUid();
        String head2 = branches.get(branchName);
        String splitPoint = findSplitPoint(head1, head2);
        if (splitPoint.equals(head2)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        } else if (splitPoint.equals(head1)) {
            checkout(head2);
            System.out.println("Current branch fast-forwarded.");
            return;
        }
        TreeMap<String, String> splitBlobs = getCommit(splitPoint).getBlobs();
        TreeMap<String, String> currentBlobs = getCommit(head1).getBlobs();
        TreeMap<String, String> givenBlobs = getCommit(head2).getBlobs();
        boolean conflict = false; // to mark if any file is in conflict
        conflict = mergeHelper(splitBlobs, currentBlobs, givenBlobs);
        /** Update the new blobs for merge commit */
        TreeMap<String, String> newBlobs = new TreeMap<>(currentBlobs);
        updateBlobs(newBlobs);
        /** Create the merge commit */
        ArrayList<String> parents = new ArrayList<>();
        parents.add(head1);
        parents.add(head2);
        String mergeMessage = "Merged " + branchName + " into " + currentBranch + ".";
        Date currentDate = new Date();
        Commit mergeCommit = new Commit(mergeMessage, currentDate, parents, newBlobs);
        mergeCommit.setUid();
        saveCommit(mergeCommit);
        /** Update the branches */
        TreeMap<String, String> branchesUpdated = getBranches();
        branchesUpdated.put(currentBranch, mergeCommit.getUid());
        saveBranches(branchesUpdated);
        /** Clear the staged */
        saveAddStaged(new TreeMap<String, String>());
        saveRmStaged(new TreeMap<String, String>());
        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    public static void addRemote(String remoteName, String remoteFile) {
        remoteFile = remoteFile.replace("/", File.separator);
        TreeMap<String, String> remote = getRemote();
        if (remote.containsKey(remoteName)) {
            System.out.println("A remote with that name already exists.");
            System.exit(0);
        }
        remote.put(remoteName, remoteFile);
        saveRemote(remote);
    }

    public static void rmRemote(String remoteName) {
        TreeMap<String, String> remote = getRemote();
        if (!remote.containsKey(remoteName)) {
            System.out.println("A remote with that name does not exist.");
            System.exit(0);
        }
        remote.remove(remoteName);
        saveRemote(remote);
    }

    public static void push(String remoteName, String remoteBranchName) {
        TreeMap<String, String> remote = getRemote();
        if (!remote.containsKey(remoteName)) {
            System.out.println("That remote does not have that branch.");
            System.exit(0);
        }
        String remoteDir = remote.get(remoteName);
        File remoteGitletDir = new File(remoteDir);
        if (!remoteGitletDir.exists()) {
            System.out.println("Remote directory not found.");
            System.exit(0);
        }
        TreeMap<String, String> remoteBranches = getRemoteBranches(remoteGitletDir);
        String localHead = getCurrentCommitUid();
        String remoteHead = remoteBranches.get(remoteBranchName);
        if (remoteHead == null) {
            copyLocalToRemote(localHead, remoteDir);
            remoteBranches.put(remoteBranchName, localHead);
            saveRemoteBranches(remoteGitletDir, remoteBranches);
            return;
        }

        Set<String> localAncestors = new HashSet<>();
        gatherAncestors(localHead, localAncestors);
        if (!localAncestors.contains(remoteHead)) {
            System.out.println("Please pull down remote changes before pushing.");
            System.exit(0);
        }

        copyLocalToRemote(localHead, remoteDir);
        remoteBranches.put(remoteBranchName, localHead);
        saveRemoteBranches(remoteGitletDir, remoteBranches);
    }

    public static void fetch(String remoteName, String remoteBranchName) {
        TreeMap<String, String> remote = getRemote();
        if (!remote.containsKey(remoteName)) {
            System.out.println("That remote does not have that branch.");
            System.exit(0);
        }
        String remoteDir = remote.get(remoteName);
        File remoteGitletDir = new File(remoteDir);
        if (!remoteGitletDir.exists()) {
            System.out.println("Remote directory not found.");
            System.exit(0);
        }
        TreeMap<String, String> localBranches = getBranches();
        String localHead = localBranches.get(remoteName + File.separator + remoteBranchName);
        String remoteHead = getRemoteBranches(remoteGitletDir).get(remoteBranchName);
        if (localHead == null) {
            copyRemoteToLocal(remoteHead, remoteDir);
            localBranches.put(remoteName + File.separator + remoteBranchName, remoteHead);
            saveBranches(localBranches);
            return;
        }

        copyRemoteToLocal(remoteHead, remoteDir);
        localBranches.put(remoteBranchName, remoteHead);
        saveBranches(localBranches);
    }

    public static void pull(String remoteName, String remoteBranchName) {
        fetch(remoteName, remoteBranchName);
        String localRemoteBranchName = remoteName + File.separator + remoteBranchName;
        merge(localRemoteBranchName);
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
        if (!ADD_FILE.exists()) {
            return new TreeMap<>();
        }
        return Utils.readObject(ADD_FILE, TreeMap.class);
    }

    /** To save the staged for addition */
    private static void saveAddStaged(TreeMap<String, String> addStaged) {
        Utils.writeObject(ADD_FILE, addStaged);
    }

    /** To get the staged for removal */
    private static TreeMap<String, String> getRmStaged() {
        if (!RM_FILE.exists()) {
            return new TreeMap<>();
        }
        return Utils.readObject(RM_FILE, TreeMap.class);
    }

    /** To save the staged for removal */
    private static void saveRmStaged(TreeMap<String, String> rmStaged) {
        Utils.writeObject(RM_FILE, rmStaged);
    }

    /** To get the current branch */
    private static String getCurrentBranchName() {
        return Utils.readObject(CURRENT_BRANCH_FILE, String.class);
    }

    /** To get branches */
    private static TreeMap<String, String> getBranches() {
        return Utils.readObject(BRANCHES_FILE, TreeMap.class);
    }

    /** To get the head pointer current */
    private static Commit getCurrentCommit() {
        String currentBranch = getCurrentBranchName();
        TreeMap<String, String> branches = Utils.readObject(BRANCHES_FILE, TreeMap.class);
        String currentCommitUid = branches.get(currentBranch);
        return getCommit(currentCommitUid);
    }

    /** Pass in a Commit object and return a object which is its parent */
    private static Commit getParentCommit(Commit commit) {
        ArrayList<String> parents = commit.getParents();
        if (parents.isEmpty()) {
            return null;
        }
        String parentCommitUid = parents.get(0);
        if (parentCommitUid == null) {
            return null;
        }
        File parentCommitFile = Utils.join(COMMIT_DIR, parentCommitUid);
        return Utils.readObject(parentCommitFile, Commit.class);
    }

    /** Pass in the uid of the commit, return the corresponding commit */
    private static Commit getCommit(String commitUid) {
        if (commitUid == null || !getCommitList().contains(commitUid)) {
            return null;
        }
        File commitFile = Utils.join(COMMIT_DIR, commitUid);
        return Utils.readObject(commitFile, Commit.class);
    }

    /** To save a new commit to the target file */
    private static void saveCommit(Commit newCommit) {
        String commitHash = newCommit.getUid();
        File newCommitFile = join(COMMIT_DIR, commitHash);
        Utils.writeObject(newCommitFile, newCommit);
    }

    /** Print the log for commit */
    private static void printLog(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getUid());
        if (commit.getParents().size() > 1) {
            String p1 = commit.getParents().get(0).substring(0, 7);
            String p2 = commit.getParents().get(1).substring(0, 7);
            System.out.println("Merge: " + p1 + " " + p2);
        }
        Formatter formatter = new Formatter(Locale.US);
        String dateStr = formatter.format(
                "%1$ta %1$tb %1$td %1$tT %1$tY %1$tz", commit.getDate()).toString();
        System.out.println("Date: " + dateStr);
        System.out.println(commit.getMessage() + "\n");
    }

    /** To get the Workspace Files */
    private static List<String> getWorkspaceFiles() {
        return Utils.plainFilenamesIn(CWD);
    }

    /** To get a file's hash */
    private static String getFileHash(File file) {
        return sha1(Utils.readContents(file));
    }

    /** To write read file contents to write file */
    private static void writeToFile(File readFile, File writeFile) {
        String fileContent = Utils.readContentsAsString(readFile);
        Utils.writeContents(writeFile, fileContent);
    }

    /** To get the commit file as a list */
    private static List<String> getCommitList() {
        return Utils.plainFilenamesIn(COMMIT_DIR);
    }

    /** Set current branch */
    private static void setCurrentBranch(String branch) {
        Utils.writeObject(CURRENT_BRANCH_FILE, branch);
    }

    /** Save the branches */
    private static void saveBranches(TreeMap<String, String> branches) {
        Utils.writeObject(BRANCHES_FILE, branches);
    }

    /** Get the current commit's blobs */
    private static TreeMap<String, String> getCurrentCommitBlobs() {
        Commit currentCommit = getCurrentCommit();
        return currentCommit.getBlobs();
    }

    /** Get the current commit's uid */
    private static String getCurrentCommitUid() {
        Commit currentCommit = getCurrentCommit();
        return currentCommit.getUid();
    }

    /** Check out to the specified commit */
    private static void checkout(String uid) {
        List<String> untrackedFiles = new ArrayList<>();
        List<String> workspaceFiles = getWorkspaceFiles();
        TreeMap<String, String> currentCommitBlobs = getCurrentCommitBlobs();
        for (String file : workspaceFiles) {
            if (!currentCommitBlobs.containsKey(file)) {
                untrackedFiles.add(file);
            }
        }
        Commit targetCommit = getCommit(uid);
        TreeMap<String, String> targetBlobs = targetCommit.getBlobs();
        for (String fileName : untrackedFiles) {
            if (targetBlobs.containsKey(fileName)) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        for (String fileName : workspaceFiles) {
            removeFile(fileName);
        }
        for (String fileName : targetBlobs.keySet()) {
            checkoutFile(fileName, targetBlobs.get(fileName));
        }
        saveAddStaged(new TreeMap<String, String>());
        saveRmStaged(new TreeMap<String, String>());
    }
    /** To get the ancestors set */
    private static void gatherAncestors(String uid, Set<String> ancestors) {
        if (uid == null || ancestors.contains(uid)) {
            return;
        }
        ancestors.add(uid);
        Commit commit = getCommit(uid);
        for (String parent : commit.getParents()) {
            gatherAncestors(parent, ancestors);
        }
    }

    /** To find the spilt point */
    public static String findSplitPoint(String uid1, String uid2) {
        Set<String> commit1Ancestors = new HashSet<>();
        gatherAncestors(uid1, commit1Ancestors);
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(uid2);
        while (!queue.isEmpty()) {
            String uid = queue.poll();
            Commit commit = getCommit(uid);
            if (commit1Ancestors.contains(uid)) {
                return uid;
            }
            for (String parent : commit.getParents()) {
                if (parent != null && !visited.contains(parent)) {
                    queue.add(parent);
                    visited.add(parent);
                }
            }
        }
        return null;
    }

    /** Check out a blob, add it into staged for addition and update */
    private static void checkoutFile(String fileName, String blobHash) {
        List<String> untrackedFiles = new ArrayList<>();
        List<String> workspaceFiles = getWorkspaceFiles();
        TreeMap<String, String> currentCommitBlobs = getCurrentCommitBlobs();
        for (String file : workspaceFiles) {
            if (!currentCommitBlobs.containsKey(file)) {
                untrackedFiles.add(file);
            }
        }
        if (untrackedFiles.contains(fileName)) {
            System.out.println("There is an untracked file in the way;"
                    + " delete it, or add and commit it first.");
            System.exit(0);
        }
        File blobFile = join(BLOB_DIR, blobHash);
        File targetFile = join(CWD, fileName);
        writeToFile(blobFile, targetFile);
        TreeMap<String, String> addStaged = getAddStaged();
        addStaged.put(fileName, blobHash);
        saveAddStaged(addStaged);
    }

    /** Remove a file in Workspace */
    private static void removeFile(String fileName) {
        File targetFile = join(CWD, fileName);
        if (targetFile.exists()) {
            restrictedDelete(targetFile);
        }
    }

    /** Add the file into staged for removal and update */
    private static void stageRemoval(String fileName, String blobHash) {
        TreeMap<String, String> rmStaged = getRmStaged();
        rmStaged.put(fileName, blobHash);
        saveRmStaged(rmStaged);
    }

    /** Produce the conflict markers */
    private static void resolveConflict(String fileName, String curContent, String givContent) {
        String conflictContent = "<<<<<<< HEAD\n"
                                    + curContent + "=======\n"
                                        + givContent + ">>>>>>>\n";
        File targetFile = join(CWD, fileName);
        Utils.writeContents(targetFile, conflictContent);
        String conflictHash = Utils.sha1(conflictContent);
        TreeMap<String, String> addStaged = getAddStaged();
        addStaged.put(fileName, conflictHash);
        saveAddStaged(addStaged);
        File stagingFile = join(ADD_DIR, conflictHash);
        if (!stagingFile.exists()) {
            Utils.writeContents(stagingFile, conflictContent);
        }
    }

    /** Get the blob contents from specific blob */
    private static String getFileContent(String blobHash) {
        File blobFile = join(BLOB_DIR, blobHash);
        return Utils.readContentsAsString(blobFile);
    }
    /** Update the commit blobs */
    private static void updateBlobs(TreeMap<String, String> blobs) {
        TreeMap<String, String> addStaged = getAddStaged();
        TreeMap<String, String> rmStaged = getRmStaged();
        for (String fileName : addStaged.keySet()) {
            String blobHash = addStaged.get(fileName);
            blobs.put(fileName, blobHash);
            File blobFile = join(BLOB_DIR, blobHash);
            File stagingFile = join(ADD_DIR, blobHash);
            if (!blobFile.exists()) {
                writeToFile(stagingFile, blobFile);
            }
            stagingFile.delete();
        }
        for (String fileName : rmStaged.keySet()) {
            blobs.remove(fileName);
            File removalFile = join(RM_DIR, rmStaged.get(fileName));
            removalFile.delete();
        }
    }
    /** To help merge method */
    private static boolean mergeHelper(TreeMap<String, String> splitBlobs,
                                       TreeMap<String, String> currentBlobs,
                                       TreeMap<String, String> givenBlobs) {
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(splitBlobs.keySet());
        allFiles.addAll(currentBlobs.keySet());
        allFiles.addAll(givenBlobs.keySet());
        boolean conflict = false;
        for (String fileName : allFiles) {
            String spBlob = splitBlobs.get(fileName);
            String curBlob = currentBlobs.get(fileName);
            String givBlob = givenBlobs.get(fileName);
            if (spBlob != null) {
                if ((curBlob != null) && curBlob.equals(spBlob)
                        && (givBlob != null) && !givBlob.equals(spBlob)) {
                    checkoutFile(fileName, givBlob);
                    continue;
                }

                if ((givBlob != null) && givBlob.equals(spBlob)
                        && (curBlob != null) && !curBlob.equals(spBlob)) {
                    continue;
                }
                if ((curBlob != null) && curBlob.equals(spBlob) && (givBlob == null)) {
                    removeFile(fileName);
                    stageRemoval(fileName, spBlob);
                    continue;
                }
                if ((curBlob != null) && !curBlob.equals(spBlob)
                        && (givBlob != null) && !givBlob.equals(spBlob)) {
                    if (curBlob.equals(givBlob)) {
                        continue;
                    } else {
                        conflict = true;
                        String curContent = getFileContent(curBlob);
                        String givContent = getFileContent(givBlob);
                        resolveConflict(fileName, curContent, givContent);
                        continue;
                    }
                }
                if ((curBlob == null) && (givBlob != null) && givBlob.equals(spBlob)) {
                    continue;
                }
                if ((curBlob == null) && (givBlob != null) && !givBlob.equals(spBlob)) {
                    conflict = true;
                    String givContent = getFileContent(givBlob);
                    resolveConflict(fileName, "", givContent);
                    continue;
                }
                if ((givBlob == null) && (curBlob != null) && !curBlob.equals(spBlob)) {
                    conflict = true;
                    String curContent = getFileContent(curBlob);
                    resolveConflict(fileName, curContent, "");
                    continue;
                }
            } else {
                if ((curBlob == null) && (givBlob != null)) {
                    checkoutFile(fileName, givBlob);
                    continue;
                }
                if ((curBlob != null) && (givBlob == null)) {
                    continue;
                }
                if ((curBlob != null) && (givBlob != null)) {
                    if (!curBlob.equals(givBlob)) {
                        conflict = true;
                        String curContent = getFileContent(curBlob);
                        String givContent = getFileContent(givBlob);
                        resolveConflict(fileName, curContent, givContent);
                    }
                }
            }
        }
        return conflict;
    }
    /** Resolve the short id */
    public static String resolveShortCommitID(String shortID) {
        if (shortID.length() == 40) {
            return shortID;
        }
        List<String> commitIDs = getCommitList();
        List<String> matches = new ArrayList<>();
        for (String id : commitIDs) {
            if (id.startsWith(shortID)) {
                matches.add(id);
            }
        }
        if (matches.isEmpty()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        if (matches.size() > 1) {
            System.exit(0);
        }
        return matches.get(0);
    }
    /** Get the remote information */
    private static TreeMap<String, String> getRemote() {
        if (!REMOTE_FILE.exists()) {
            return new TreeMap<String, String>();
        }
        return readObject(REMOTE_FILE, TreeMap.class);
    }
    /** Save the remote information */
    private static void saveRemote(TreeMap<String,String> remote) {
        Utils.writeObject(REMOTE_FILE, remote);
    }
    /** Get remote branches */
    private static TreeMap<String,String> getRemoteBranches(File remoteDir) {
        File remoteBranchesFile = join(remoteDir, "branches");
        TreeMap<String, String> remoteBranches;
        if (remoteBranchesFile.exists()) {
            remoteBranches = Utils.readObject(remoteBranchesFile, TreeMap.class);
        } else {
            remoteBranches = new TreeMap<>();
        }
        return remoteBranches;
    }
    /** Copy the local branch to remote */
    private static void copyLocalToRemote(String localHead, String remoteDir) {
        File remoteCommitDir = join(remoteDir, "commits");
        File remoteBlobDir = join(remoteDir, "blobs");
        if (!remoteCommitDir.exists()) {
            remoteCommitDir.mkdir();
        }
        if (!remoteBlobDir.exists()) {
            remoteBlobDir.mkdir();
        }

        Set<String> localAncestors = new HashSet<>();
        gatherAncestors(localHead, localAncestors);
        for (String commitId : localAncestors) {
            File localCommitFile = join(COMMIT_DIR, commitId);
            File remoteCommitFile = join(remoteCommitDir, commitId);
            if (!remoteCommitFile.exists()) {
                Commit commit = Utils.readObject(localCommitFile, Commit.class);
                Utils.writeObject(remoteCommitFile, commit);
            }
        }

        List<String> localBlobs = Utils.plainFilenamesIn(BLOB_DIR);
        for (String blobId : localBlobs) {
            File localBlobFile = join(BLOB_DIR, blobId);
            File remoteBlobFile = join(remoteBlobDir, blobId);
            if (!remoteBlobFile.exists()) {
                byte[] content = Utils.readContents(localBlobFile);
                Utils.writeContents(remoteBlobFile, content);
            }
        }
    }
    /** Copy remote to local */
    private static void copyRemoteToLocal(String remoteHead, String remoteDir) {
        File remoteCommitDir = join(remoteDir, "commits");
        File remoteBlobDir = join(remoteDir, "blobs");

        Set<String> remoteAncestors = new HashSet<>();
        gatherAncestors(remoteHead, remoteAncestors);
        for (String commitId : remoteAncestors) {
            File localCommitFile = join(COMMIT_DIR, commitId);
            File remoteCommitFile = join(remoteCommitDir, commitId);
            if (!localCommitFile.exists()) {
                Commit commit = Utils.readObject(remoteCommitFile, Commit.class);
                Utils.writeObject(localCommitFile, commit);
            }
        }

        List<String> remoteBlobs = Utils.plainFilenamesIn(remoteBlobDir);
        for (String blobId : remoteBlobs) {
            File localBlobFile = join(BLOB_DIR, blobId);
            File remoteBlobFile = join(remoteBlobDir, blobId);
            if (!localBlobFile.exists()) {
                byte[] content = Utils.readContents(remoteBlobFile);
                Utils.writeContents(localBlobFile, content);
            }
        }
    }

    /** Save remote branches */
    private static void saveRemoteBranches(File remoteDir ,TreeMap<String, String> remoteBranches) {
        File remoteBranchesFile = join(remoteDir, "branches");
        Utils.writeObject(remoteBranchesFile, remoteBranches);
    }
}
