package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Log implements Serializable {
    public static final File LOG_File = join(GITLET_DIR, "logs");

    private List<String> commit_blobs = new LinkedList<>();
    private  List<String> branch_blobs = new LinkedList<>();

    public Log() {
    }

    public List<String> getCommit_blobs() {
        return commit_blobs;
    }

    public void setCommit_blobs(List<String> commit_blobs) {
        this.commit_blobs = commit_blobs;
    }

    public List<String> getBranch_blobs() {
        return branch_blobs;
    }

    public void setBranch_blobs(List<String> branch_blobs) {
        this.branch_blobs = branch_blobs;
    }

    @Override
    public String toString() {
        return "Log{" +
                "commit_blobs=" + commit_blobs +
                '}';
    }

    public void createLog() {
        File f = LOG_File;
        if(f.exists()) {
            f.delete();
        }
        try {
            f.createNewFile();
            writeObject(f,this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}