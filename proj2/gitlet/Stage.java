package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Stage implements Serializable {
    public static final File Stage_File = join(GITLET_DIR, "index");

    private transient Tree tree;
    private String tree_sha1;

    public Stage() {
        tree = new Tree();
    }

    public Stage(Tree tree, String tree_sha1) {
        this.tree = tree;
        this.tree_sha1 = tree_sha1;
    }

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public String getTree_sha1() {
        return tree_sha1;
    }

    public void setTree_sha1(String tree_sha1) {
        this.tree_sha1 = tree_sha1;
    }

    public static void createStage(){
        try {
             Stage_File.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void createStageBlob() {
        restrictedDelete(Stage_File);
        String s = Utils.sha1(this);
        String prefix = s.substring(0,2);
        String suffix = s.substring(3);
        File dir = join(OBJECTS_DIR, prefix);
        dir.mkdir();
        File f = join(dir, suffix);
        try {
            f.createNewFile();
            writeContents(f,this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void restoreStage() {
        this.tree = tree_sha1 == null ? new Tree() : findObjectBySha1(this.tree_sha1, Tree.class);
    }
    public void setSha1(){
        this.tree_sha1 = sha1(this.tree);
    }
}
