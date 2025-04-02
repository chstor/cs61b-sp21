package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Head.HEAD_FILE;
import static gitlet.Log.LOG_File;
import static gitlet.Stage.*;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
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
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File REFSHEADS_DIR = join(Repository.REFS_DIR, "heads");

    //create .gitlet dir
    //head->commit,branch->commit,default branch = master
    public static void init() {
        //if .gitlet dir is exist.
        if(GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        /*
            create .gitlet logs objects refs/heads dir.
        */
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        REFSHEADS_DIR.mkdirs();

        /*
        * 1、create commit class: message,date.
        * 2、create objects/sha1(commit).
        * */
        String message = "initial commit";
        Commit commit = new Commit();
        commit.setMessage(message);
        commit.setDate(new Date());
        commit.createCommit();

        //System.out.println(sha1(commit));

        /*
        * 1、create branch class: name,commit.
        * 2、setSha1->branch.
        * 3、create refs/heads/name file.
        * */
        Branch branch = new Branch("master",commit);
        branch.setSha1();
        branch.createBranch();

        /*
        * 1、create head class: commit,branch.
        * 2、setSha1->head.
        * 3、create HEAD file.
        * */
        Head head = new Head(commit,branch);
        head.setSha1();
        head.createHead();

        /*
        * create log : save commit,branch
        * */
        Log log = new Log();
        log.getCommit_blobs().add(sha1(commit.toString()));
        log.getBranch_blobs().add(sha1(branch.toString()));
        log.createLog();
//        head = readObject(HEAD_FILE, Head.class);
//        head.restoreHead();
//        System.out.println(sha1(head.getCommit()));
    }

    /*
    * add stage and trackTree
    * */
    public static void add(String fileName){
        //if file is not exist
        File f = join(CWD, fileName);
        if(!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        //read file.context
        String context = readContentsAsString(f);

        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        Commit commit = head.getCommit();
        if(commit.getTrack().containsKey(fileName)) {
            String text = findObjectBySha1(commit.getTrack().get(fileName), String.class);
            if(text.equals(context)) {
                return;
            }
        }

        //if stageFile is not exist
        Stage stage;
        if(!Stage_File.exists()){
            //if not exist,create stageFile and new Stage class
            Stage.createStage();
            stage = new Stage();
        }else{
            //if exist,read stageFile to stage class
            stage = readObject(Stage_File, Stage.class);
        }

        //add file to track_tree
        addToTrack(fileName,context);

        if(stage.getRmblobs().contains(fileName)){
            return;
        }

        //put fileName and sha1(context)
        TreeMap<String, String> blobs = stage.getBlobs();
        Blob blob = new Blob(context);
        blob.createBlob();
        blobs.put(fileName, sha1(context));

        writeObject(Stage_File,stage);

    }

    private static void addToTrack(String fileName, String context) {
        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        Commit commit = head.getCommit();
        if(!Stage_File.exists()){
            TreeMap<String, String> track = commit.getTrack();
            for(Map.Entry<String, String> entry : track.entrySet()){
                head.getTrack().put(entry.getKey(),entry.getValue());
            }
        }
        head.getTrack().put(fileName, sha1(context));
        writeObject(HEAD_FILE,head);
    }

    public static void commit(String message){
        //if message is blank
        if(message.equals("")){
            System.out.println("Please enter a commit message.");
            return;
        }
        //if stage_file is not exist
        if(!Stage_File.exists()){
            System.out.println("No changes added to the commit.");
            return;
        }
        //read stage_file to create stage class
        Stage stage = readObject(Stage_File, Stage.class);

        //delete stage_file and create stageBlob
        stage.createStageBlob();

        //head->commit
        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        Commit parent_commit = head.getCommit();
        parent_commit.restoreCommit();
        TreeMap<String,String> track = head.getTrack();
        //branch->commit
        Branch branch = head.getBranch();
        branch.restoreBranch();

        //create commit class
        Commit commit = new Commit(message,new Date(),stage,track,head.getBranch_sha1());
        commit.getParent_sha1().add(sha1(parent_commit.toString()));
        commit.setSha1();
        commit.createCommit();

        //if head->commit == branch->commit
        if(head.getCommit_sha1().equals(branch.getCommit_sha1())){
            branch.setCommit_sha1(sha1(commit.toString()));
            //change branch
            branch.createBranch();
        }
        //change head
        head.setCommit_sha1(sha1(commit.toString()));
        head.createHead();

        Log log = readObject(LOG_File, Log.class);
        log.getCommit_blobs().add(sha1(commit.toString()));
        log.createLog();
    }

    public static void rm(String fileName) {
        Stage stage;
        if(Stage_File.exists()){
            stage = readObject(Stage_File, Stage.class);
        }else{
            stage = new Stage();
        }
        if(stage.getBlobs().containsKey(fileName)) {
            stage.getBlobs().remove(fileName);
            writeObject(Stage_File,stage);
            return;
        }
        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        TreeMap<String, String> track = head.getTrack();
        if(track.containsKey(fileName)){
            track.remove(fileName);
            File f = join(CWD, fileName);
            f.delete();
            stage.getRmblobs().add(fileName);
        }else{
            System.out.println("No reason to remove the file.");
        }
        writeObject(HEAD_FILE,head);
        writeObject(Stage_File,stage);
    }

    public static void log(){
        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        //System.out.println(head.getCommit_sha1());
        Commit commit = head.getCommit();
        while(commit != null){
            commit.restoreCommit();
            System.out.println("===");
            System.out.println("commit " + sha1(commit.toString()));
            if(commit.getMerge_message()!=null){
                System.out.println("Merge: " +  commit.getMerge_message());
            }
            System.out.println("Date: " + commit.getDate());
            System.out.println(commit.getMessage());
            if(!commit.getParent().isEmpty()){
                commit = commit.getParent().get(0);
            }else{
                commit = null;
            }
            System.out.println();
        }
    }

    public static void globalLog() {
        Log log = readObject(LOG_File, Log.class);
        List<String> commitBlobs = log.getCommit_blobs();
        for(String commitBlob : commitBlobs){
            Commit commit = findObjectBySha1(commitBlob, Commit.class);
            System.out.println("===");
            System.out.println("commit " + sha1(commit.toString()));
            if(commit.getMerge_message()!=null){
                System.out.println("Merge: " +  commit.getMerge_message());
            }
            System.out.println("Date: " + commit.getDate());
            System.out.println(commit.getMessage());
        }
    }
    public static void find(String message) {
        Log log = readObject(LOG_File, Log.class);
        List<String> commitBlobs = log.getCommit_blobs();
        boolean found = false;
        for(String commitBlob : commitBlobs){
            Commit commit = findObjectBySha1(commitBlob, Commit.class);
            if(commit.getMessage().equals(message)){
                found = true;
                System.out.printf(commitBlob);
            }
        }
        if(!found){
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();

        TreeMap<String, String> track = head.getTrack();
        Branch currentBranch = head.getBranch();

        Log log = readObject(LOG_File, Log.class);
        System.out.println("=== Branches ===");
        List<String> branchBlobs = log.getBranch_blobs();
        for(String branchBlob : branchBlobs){
            Branch branch = findObjectBySha1(branchBlob, Branch.class);
            if(currentBranch.getName().equals(branch.getName())){
                System.out.print("*");
            }
            System.out.println(branch.getName());
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        Set<String> trackFiles = track.keySet();
        Stage stage;
        if(Stage_File.exists()){
            stage = readObject(Stage_File, Stage.class);
        }else{
            stage = new Stage();
        }
        for(String fileName : stage.getBlobs().keySet()){
            System.out.println(fileName);
        }
        System.out.println();

        List<String> fileNames = plainFilenamesIn(CWD);
        System.out.println("=== Removed Files ===");
        for(String fileName : stage.getRmblobs()){
            if(!fileNames.contains(fileName)){
                System.out.println(fileName);
            }
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        for(String fileName : trackFiles){
            if(!fileNames.contains(fileName)){
                System.out.println(fileName + "(deleted)");
            }else{
                File f = join(CWD,fileName);
                String context = readContentsAsString(f);
                String track_context= findObjectBySha1(track.get(fileName),String.class);
                if(!context.equals(track_context)){
                    System.out.println(fileName + "(modified)");
                }
            }
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        for(String fileName : fileNames){
            if(!trackFiles.contains(fileName)){
                System.out.println(fileName);
            }
        }
        System.out.println();
    }

    public static void checkoutBranch(String branchName) {
        //find branch's commit
        Log log = readObject(LOG_File, Log.class);
        List<String> branchBlobs = log.getBranch_blobs();
        if(!branchBlobs.contains(branchName)){
            System.out.println("No such branch exists.");
            return;
        }

        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        Commit commit = head.getCommit();
        TreeMap<String, String> currentTrack = commit.getTrack();

        Branch currentBranch = head.getBranch();
        if(branchName.equals(currentBranch.getName())){
            System.out.println("No need to checkout the current branch.");
            return;
        }

        Branch checkoutBranch = readObject(join(REFSHEADS_DIR,branchName),Branch.class);
        checkoutBranch.restoreBranch();
        Commit checkoutCommit = checkoutBranch.getCommit();
        TreeMap<String, String> checkoutTrack = checkoutCommit.getTrack();

        List<String> fileNames = plainFilenamesIn(CWD);
        for(String trackKey : checkoutTrack.keySet()){
            //if currentTrack not this file,but checkoutTrack have
            if(fileNames.contains(trackKey) && !currentTrack.containsKey(trackKey)){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }

        for(String fileName : currentTrack.keySet()){
            File f = join(CWD, fileName);
            if(f.exists()){
                f.delete();
            }
        }
        for(String trackKey : checkoutTrack.keySet()){
            String context = findObjectBySha1(checkoutTrack.get(trackKey), String.class);
            createCWDfile(trackKey,context);
        }

        //change head->branch->commit
        head.setCommit(checkoutCommit);
        head.setBranch(checkoutBranch);
        head.setTrack(checkoutTrack);
        head.setSha1();
        head.createHead();
    }

    public static void checkoutFile(String fileName) {
        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        Commit commit = head.getCommit();
        TreeMap<String, String> currentTrack = commit.getTrack();
        if(!currentTrack.containsKey(fileName)){
            System.out.println("File does not exist in that commit.");
        }
        String context = findObjectBySha1(currentTrack.get(fileName),String.class);
        createCWDfile(fileName,context);
    }

    public static void checkoutFileByCommitId(String commitId, String fileName) {
        Log log = readObject(LOG_File, Log.class);
        List<String> commitBlobs = log.getCommit_blobs();
        if(!commitBlobs.contains(commitId)){
            System.out.println("No commit with that id exists.");
        }

        Commit commit = findObjectBySha1(commitId, Commit.class);
        TreeMap<String, String> currentTrack = commit.getTrack();
        if(!currentTrack.containsKey(fileName)){
            System.out.println("File does not exist in that commit.");
        }

        String context = findObjectBySha1(currentTrack.get(fileName),String.class);
        //System.out.println(context);
        createCWDfile(fileName,context);
    }

    public static void createCWDfile(String fileName,String context){
        File f = join(CWD, fileName);
        if(f.exists()){
            f.delete();
        }
        try {
            f.createNewFile();
            writeContents(f,context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createBranch(String branchName) {
        Log log = readObject(LOG_File, Log.class);
        List<String> branchBlobs = log.getBranch_blobs();
        if(branchBlobs.contains(branchName)){
            System.out.println("A branch with that name already exists.");
            return;
        }
        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        Commit commit = head.getCommit();
        Branch branch = new Branch(branchName, commit);
        branch.setSha1();
        branch.createBranch();
    }

    public static void rmBranch(String branchName) {
        Log log = readObject(LOG_File, Log.class);
        List<String> branchBlobs = log.getBranch_blobs();
        if(!branchBlobs.contains(branchName)){
            System.out.println("A branch with that name does not exist.");
            return;
        }
        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        if(head.getBranch().getName().equals(branchName)){
            System.out.println("Cannot remove the current branch.");
            return;
        }
        File f = join(REFSHEADS_DIR,branchName);
        f.delete();
    }

    public static void resetByCommitId(String commitId) {
        //find commitId
        Log log = readObject(LOG_File, Log.class);
        List<String> commitBlobs = log.getCommit_blobs();
        if(!commitBlobs.contains(commitId)){
            System.out.println("No commit with that id exists.");
            return;
        }

        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        Commit commit = head.getCommit();
        TreeMap<String, String> currentTrack = commit.getTrack();

        Commit checkoutCommit = findObjectBySha1(commitId,Commit.class);
        TreeMap<String, String> checkoutTrack = checkoutCommit.getTrack();

        List<String> fileNames = plainFilenamesIn(CWD);
        for(String trackKey : checkoutTrack.keySet()){
            //if currentTrack not this file,but checkoutTrack have
            if(fileNames.contains(trackKey) && !currentTrack.containsKey(trackKey)){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }

        for(String fileName : currentTrack.keySet()){
            File f = join(CWD, fileName);
            if(f.exists()){
                f.delete();
            }
        }
        for(String trackKey : checkoutTrack.keySet()){
            String context = findObjectBySha1(checkoutTrack.get(trackKey), String.class);
            createCWDfile(trackKey,context);
        }

        //change head->branch->commit
        head.setCommit(checkoutCommit);
        head.setBranch(findObjectBySha1(commit.getBranch_sha1(), Branch.class));
        head.setTrack(checkoutTrack);
        head.setSha1();
        head.createHead();
    }

    public static void merge(String branchName) {
        //If there are staged additions or removals present
        if(Stage_File.exists()){
            System.out.println("You have uncommitted changes.");
            return;
        }

        //A branch with that name does not exist.
        Log log = readObject(LOG_File, Log.class);
        LinkedList<String> branchBlobs = log.getBranch_blobs();
        if(!branchBlobs.contains(branchName)){
            System.out.println("A branch with that name does not exist.");
            return;
        }

        //Cannot merge a branch with itself.
        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        Branch currentBranch = head.getBranch();
        currentBranch.restoreBranch();
        Commit currentCommit = currentBranch.getCommit();
        currentCommit.restoreCommit();
        if(branchName.equals(currentBranch.getName())){
            System.out.println("Cannot merge a branch with itself.");
            return;
        }

        Branch checkoutBranch = readObject(join(REFSHEADS_DIR,branchName),Branch.class);
        checkoutBranch.restoreBranch();
        Commit checkoutCommit = checkoutBranch.getCommit();
        checkoutCommit.restoreCommit();
        TreeMap<String, String> checkoutTrack = checkoutCommit.getTrack();
        Commit commit = head.getCommit();
        TreeMap<String, String> currentTrack = commit.getTrack();

        List<String> fileNames = plainFilenamesIn(CWD);
        for(String trackKey : checkoutTrack.keySet()){
            //if currentTrack not this file,but checkoutTrack have
            if(fileNames.contains(trackKey) && !currentTrack.containsKey(trackKey)){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                return;
            }
        }

        //find split point
        Commit split_commit = findlca(currentCommit,checkoutCommit);
        split_commit.restoreCommit();
        if(split_commit.equals(checkoutCommit)){
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        if(split_commit.equals(currentCommit)){
            currentBranch.setCommit_sha1(sha1(checkoutCommit.toString()));
            currentBranch.createBranch();
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        TreeMap<String, String> splitCommitTrack = split_commit.getTrack();
        TreeMap<String, String> currentCommitTrack = currentCommit.getTrack();
        /*
        * if a file is modified in the given branch
        * since the split point this means the version of the file
        * as it exists in the commit at the front of the given branch has different content
        * from the version of the file at the split point.
        * */
        for(String fileName : splitCommitTrack.keySet()){
            if(currentCommitTrack.containsKey(fileName) && checkoutTrack.containsKey(fileName)){
                String checkoutCommitContext = findObjectBySha1(checkoutTrack.get(fileName), String.class);
                String currentCommitContext = findObjectBySha1(currentCommitTrack.get(fileName), String.class);
                String splitCommitContext = findObjectBySha1(splitCommitTrack.get(fileName), String.class);
                if(splitCommitContext.equals(currentCommitContext) &&  !checkoutCommitContext.equals(currentCommitContext)){
                    createCWDfile(fileName,checkoutCommitContext);
                }
            }
            if(currentCommitTrack.containsKey(fileName) && !checkoutTrack.containsKey(fileName)){
                String currentCommitContext = findObjectBySha1(currentCommitTrack.get(fileName), String.class);
                String splitCommitContext = findObjectBySha1(splitCommitTrack.get(fileName), String.class);
                if(splitCommitContext.equals(currentCommitContext)){
                    rm(fileName);
                }
            }
        }

        /*
        * Any files that were not present at the split point
        * and are present only in the given branch should be checked out and staged.
        * */
        for(String fileName : checkoutTrack.keySet()){
            if(!splitCommitTrack.containsKey(fileName) && !currentCommitTrack.containsKey(fileName)){
                String context = checkoutTrack.get(fileName);
                createCWDfile(fileName,context);
                add(fileName);
            }
            if(currentCommitTrack.containsKey(fileName)){
                String currentCommitContext = findObjectBySha1(currentCommitTrack.get(fileName), String.class);
                String checkoutCommitContext = findObjectBySha1(checkoutTrack.get(fileName), String.class);
                if(!checkoutCommitContext.equals(currentCommitContext)){
                    currentCommitContext = "<<<<<<< HEAD"
                                            + currentCommitContext
                                            + "======="
                                            + checkoutCommitContext
                                            + ">>>>>>>";
                    createCWDfile(fileName,currentCommitContext);
                    System.out.println("Encountered a merge conflict.");
                    return;
                }
            }
        }
        commitMerge(String.format("Merged %s into %s.", branchName,currentBranch.getName()),checkoutCommit);
    }

    public static void commitMerge(String message,Commit checkoutCommit){
        //if message is blank
        if(message.equals("")){
            System.out.println("Please enter a commit message.");
            return;
        }
        //if stage_file is not exist
        if(!Stage_File.exists()){
            System.out.println("No changes added to the commit.");
            return;
        }
        //read stage_file to create stage class
        Stage stage = readObject(Stage_File, Stage.class);

        //delete stage_file and create stageBlob
        stage.createStageBlob();

        //head->commit
        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        Commit parent_commit = head.getCommit();
        parent_commit.restoreCommit();
        TreeMap<String,String> track = head.getTrack();
        //branch->commit
        Branch branch = head.getBranch();
        branch.restoreBranch();

        //create commit class
        Commit commit = new Commit(message,new Date(),stage,track,head.getBranch_sha1());
        commit.getParent_sha1().add(sha1(parent_commit.toString()));
        commit.getParent_sha1().add(sha1(checkoutCommit.toString()));
        commit.setSha1();
        commit.createCommit();

        //if head->commit == branch->commit
        if(head.getCommit_sha1().equals(branch.getCommit_sha1())){
            branch.setCommit_sha1(sha1(commit.toString()));
            //change branch
            branch.createBranch();
        }
        //change head
        head.setCommit_sha1(sha1(commit.toString()));
        head.createHead();

        Log log = readObject(LOG_File, Log.class);
        log.getCommit_blobs().add(sha1(commit.toString()));
        log.createLog();
    }
    private static Commit findlca(Commit commitA, Commit commitB) {
        //bfs commmitA
        Set<String> visited = new HashSet<>();
        Queue<Commit> queue = new LinkedList<>();
        queue.offer(commitA);
        while(!queue.isEmpty()){
            Commit commit = queue.poll();
            visited.add(sha1(commit));
            commit.restoreCommit();
            queue.addAll(commit.getParent());
        }
        queue.offer(commitB);
        while(!queue.isEmpty()){
            Commit commit = queue.poll();
            commit.restoreCommit();
            if(visited.contains(sha1(commit))){
                return commit;
            }
            queue.addAll(commit.getParent());
        }
        return null;
    }

}
