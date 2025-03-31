package gitlet;

import java.io.File;
import java.util.Date;
import java.util.TreeMap;

import static gitlet.Head.HEAD_FILE;
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
    public static final File LOGS_DIR = join(GITLET_DIR, "logs");
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
        LOGS_DIR.mkdir();
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
        //System.out.println(sha1(commit.toString()));

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

        head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        System.out.println(sha1(head.getCommit().toString()));
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


        //read file.context
        String context = readContentsAsString(f);

        //put fileName and sha1(context)
        TreeMap<String, String> blobs = stage.getBlobs();
        Blob blob = new Blob(context);
        blob.createBlob();
        blobs.put(fileName, sha1(context));

        writeObject(Stage_File,stage);

        //add file to track_tree
        addToTrack(fileName,context);
    }

    private static void addToTrack(String fileName, String context) {
        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        Commit commit = head.getCommit();
        commit.restoreCommit();
        commit.getTrack().put(fileName, context);
        commit.createCommit();
        head.setSha1();
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
        TreeMap<String,String> track = parent_commit.getTrack();
        //branch->commit
        Branch branch = head.getBranch();
        branch.restoreBranch();

        //create commit class
        Commit commit = new Commit(message,new Date(),parent_commit,stage,track);
        commit.setSha1();
        commit.createCommit();

        //if head->commit == branch->commit
        if(head.getCommit_sha1().equals(branch.getCommit_sha1())){
            branch.setCommit_sha1(sha1(commit));
            //change branch
            branch.createBranch();
        }
        //change head
        head.setCommit_sha1(sha1(commit.toString()));
        head.createHead();
    }

    public static void rm(String fileName) {
        File f = join(CWD, fileName);
        if(!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        if(!Stage_File.exists()){
            System.out.println("No changes added to the commit.");
            return;
        }
        Stage stage = readObject(Stage_File, Stage.class);

        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        Commit commit = head.getCommit();
        TreeMap<String, String> track = commit.getTrack();

        if(track.containsKey(fileName)){
            track.remove(fileName);
            if(stage.getBlobs().containsKey(fileName)) {
                stage.getBlobs().remove(fileName);
            }
            restrictedDelete(fileName);
            writeObject(Stage_File,stage);
            //head->new_commit
            head.setSha1();
            writeObject(HEAD_FILE,head);
        }
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
            System.out.println("Date: " + commit.getDate());
            System.out.println(commit.getMessage());
            commit = commit.getParent();
        }
    }
}
