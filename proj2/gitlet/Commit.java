package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.TreeMap;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date date;

    private transient Commit parent;
    private transient Stage commit_stage;
    private transient Tree track_tree;
    private String parent_sha1;
    private String commit_stage_sha1;
    private String track_tree_sha1;

    public Commit() {
    }

    public Commit(String message, Date date, Commit parent, Stage commit_stage,Tree track_tree) {
        this.message = message;
        this.date = date;
        this.parent = parent;
        this.commit_stage = commit_stage;
        this.track_tree = track_tree;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Commit getParent() {
        return parent;
    }

    public void setParent(Commit parent) {
        this.parent = parent;
    }

    public String getParent_sha1() {
        return parent_sha1;
    }

    public void setParent_sha1(String parent_sha1) {
        this.parent_sha1 = parent_sha1;
    }

    public Stage getCommit_stage() {
        return commit_stage;
    }

    public void setCommit_stage(Stage commit_stage) {
        this.commit_stage = commit_stage;
    }

    public String getCommit_stage_sha1() {
        return commit_stage_sha1;
    }

    public void setCommit_stage_sha1(String commit_stage_sha1) {
        this.commit_stage_sha1 = commit_stage_sha1;
    }

    public Tree getTrack_tree() {
        return track_tree;
    }

    public void setTrack_tree(Tree track_tree) {
        this.track_tree = track_tree;
    }

    public String getTrack_tree_sha1() {
        return track_tree_sha1;
    }

    public void setTrack_tree_sha1(String track_tree_sha1) {
        this.track_tree_sha1 = track_tree_sha1;
    }

    public void createCommit() {
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
    public void restoreCommit() {
        this.parent = parent_sha1==null ? new Commit() : findObjectBySha1(this.parent_sha1, Commit.class);
        this.commit_stage = commit_stage_sha1 == null ? new Stage() : findObjectBySha1(this.commit_stage_sha1,Stage.class);
        this.track_tree = track_tree_sha1 == null ? new Tree() : findObjectBySha1(this.track_tree_sha1,Tree.class);
    }

    public void setSha1(){
        this.commit_stage_sha1 = sha1(this.commit_stage);
        this.parent_sha1 = sha1(this.parent);
        this.track_tree_sha1 = sha1(this.track_tree);
    }

}
