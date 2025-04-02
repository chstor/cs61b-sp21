package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private String merge_message;
    private TreeMap<String,String> track = new TreeMap<>();;

    private transient ArrayList<Commit> parent;
    private transient Stage commit_stage;
    private ArrayList<String> parent_sha1 = new ArrayList<>();
    private String commit_stage_sha1;
    private String branch_sha1;

    public Commit() {

    }

    public Commit(String message, Date date, Stage commit_stage,TreeMap<String,String> track,String branch_sha1) {
        this.message = message;
        this.date = date;
        this.commit_stage = commit_stage;
        this.track = track;
        this.branch_sha1 = branch_sha1;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<Commit> getParent() {
        return parent;
    }

    public void setParent(ArrayList<Commit> parent) {
        this.parent = parent;
    }

    public ArrayList<String> getParent_sha1() {
        return parent_sha1;
    }

    public void setParent_sha1(ArrayList<String> parent_sha1) {
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

    public TreeMap<String, String> getTrack() {
        return track;
    }

    public void setTrack(TreeMap<String, String> track) {
        this.track = track;
    }

    public String getMerge_message() {
        return merge_message;
    }

    public void setMerge_message(String merge_message) {
        this.merge_message = merge_message;
    }

    public String getBranch_sha1() {
        return branch_sha1;
    }

    public void setBranch_sha1(String branch_sha1) {
        this.branch_sha1 = branch_sha1;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "message='" + message + '\'' +
                ", date=" + date +
                ", merge_message='" + merge_message + '\'' +
                ", track=" + track +
                ", parent_sha1='" + parent_sha1 + '\'' +
                ", commit_stage_sha1='" + commit_stage_sha1 + '\'' +
                ", branch_sha1='" + branch_sha1 + '\'' +
                '}';
    }

    public void createCommit() {
        String s = sha1(this.toString());
        String prefix = s.substring(0,2);
        String suffix = s.substring(2);
        File dir = join(OBJECTS_DIR, prefix);
        dir.mkdir();
        File f = join(dir, suffix);
        try {
            f.createNewFile();
            writeObject(f,this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void restoreCommit() {
        if(this.parent_sha1 != null){
            this.parent = new ArrayList<>();
            for(String parent_sha1: this.parent_sha1) {
                this.parent.add(findObjectBySha1(parent_sha1,Commit.class));
            }
        }
        if(this.commit_stage != null){
            this.commit_stage = findObjectBySha1(this.commit_stage_sha1,Stage.class);
        }
    }

    public void setSha1(){
        this.commit_stage_sha1 = sha1(this.commit_stage.toString());
        if(this.parent != null){
            for(Commit commit : this.parent){
                String sha1 = sha1(commit.toString());
                this.parent_sha1.add(sha1);
            }
        }
    }

}
