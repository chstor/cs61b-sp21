package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.TreeMap;

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

    public static Head HEAD;
    public static Stage stage;

    public static void init() {
        if(GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        LOGS_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        REFSHEADS_DIR.mkdirs();

        String message = new String("initial commit");
        Date date = new Date();
        Commit commit = new Commit(message,date,null,null,null,null);
        commit.createCommit();

        Branch branch = new Branch("master",commit,sha1(commit));
        branch.createBranch();

        HEAD = new Head(commit,sha1(commit));
        HEAD.createHead();
    }
    public static void add(String fileName){
        File f = join(CWD, fileName);
        if(!f.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        if(!Stage_File.exists()){
            Stage.createStage();
            stage = new Stage(null,null);
        }else{
            stage = readObject(Stage_File, Stage.class);
        }
        String treeSha1 = stage.getTree_sha1();
        Tree stageTree;
        if(treeSha1 != null){
            stageTree = findBlobBySha1(treeSha1,Tree.class);
        }else{
            stageTree = new Tree(null,null,null);
        }
        String[] files = fileName.split("/");
        for(String file: files){
            if(file != fileName){
                if(stageTree.getChild() == null){
                    stageTree.setChild(new Tree(null,null,null));
                }
                stageTree = stageTree.getChild();
            }else{
                if(stageTree.getBlobs() == null){
                    stageTree.setBlobs(new TreeMap<>());
                }
                stageTree.getBlobs().put(file,sha1(new Blob(readContentsAsString(f))));
            }
        }
    }
    public static void commit(String message){

    }
}
