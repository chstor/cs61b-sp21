package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
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

    private transient Tree tree;
    private transient Commit parent;
    private String tree_sha1;
    private String parent_sha1;

    public Commit(String message, Date date, Tree tree, Commit parent, String tree_sha1, String parent_sha1) {
        this.message = message;
        this.date = date;
        this.tree = tree;
        this.parent = parent;
        this.tree_sha1 = tree_sha1;
        this.parent_sha1 = parent_sha1;
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

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public Commit getParent() {
        return parent;
    }

    public void setParent(Commit parent) {
        this.parent = parent;
    }

    public String getTree_sha1() {
        return tree_sha1;
    }

    public void setTree_sha1(String tree_sha1) {
        this.tree_sha1 = tree_sha1;
    }

    public String getParent_sha1() {
        return parent_sha1;
    }

    public void setParent_sha1(String parent_sha1) {
        this.parent_sha1 = parent_sha1;
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
}
