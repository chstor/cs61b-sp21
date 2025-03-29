package gitlet;

import antlr.StringUtils;
import org.eclipse.jetty.util.StringUtil;

import java.io.File;
import java.io.IOException;
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

    public static void init() {
        if(GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        LOGS_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        REFSHEADS_DIR.mkdirs();

        String message = "initial commit";
        Commit commit = new Commit();
        commit.setMessage(message);
        commit.setDate(new Date());
        commit.createCommit();

        Branch branch = new Branch("master",commit);
        branch.setSha1();
        branch.createBranch();

        Head head = new Head(commit,branch);
        head.setSha1();
        head.createHead();
    }
    public static void add(String fileName){
        File f = join(CWD, fileName);
        if(!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        Stage stage;
        if(!Stage_File.exists()){
            Stage.createStage();
            stage = new Stage();
        }else{
            stage = readObject(Stage_File, Stage.class);
        }
        stage.restoreStage();
        Tree stageTree = stage.getTree();
        TreeMap<String, String> blobs = stageTree.getBlobs();
        Blob blob = new Blob(readContentsAsString(f));
        if(blobs.containsKey(fileName) && blobs.get(fileName).equals(sha1(blob))){
            return;
        }
        blob.createBlob();
        blobs.put(fileName, sha1(blob));
        stageTree.createTree();
        stage.setSha1();
        writeObject(Stage_File,stage);

        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        Commit commit = head.getCommit();
        String commit_sha1 = sha1(commit);
        commit.restoreCommit();
        Tree trackTree = commit.getTrack_tree();
        trackTree.getBlobs().put(fileName, sha1(blob));
        trackTree.createTree();
        commit.setSha1();
        writeContentsBySha1(commit_sha1,commit);

    }
    public static void commit(String message){
        if(StringUtil.isBlank(message)){
            System.out.println("Please enter a commit message.");
            return;
        }
        if(!Stage_File.exists()){
            System.out.println("No changes added to the commit.");
            return;
        }
        Stage stage = readObject(Stage_File, Stage.class);
        stage.restoreStage();
        stage.createStageBlob();

        Head head = readObject(HEAD_FILE, Head.class);
        head.restoreHead();
        Commit parent_commit = head.getCommit();
        parent_commit.restoreCommit();
        Tree trackTree = parent_commit.getTrack_tree();

        Branch branch = head.getBranch();
        branch.restoreBranch();

        Commit commit = new Commit(message,new Date(),parent_commit,stage,trackTree);
        commit.setSha1();
        commit.createCommit();

        if(head.getCommit_sha1().equals(branch.getCommit_sha1())){
            branch.setCommit_sha1(sha1(commit));
            branch.createBranch();
        }
        head.setCommit_sha1(sha1(commit));
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

    }
}
