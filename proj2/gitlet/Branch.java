package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Branch implements Serializable {

    private String name;
    private transient Commit commit;
    private String commit_sha1;

    public Branch(String name, Commit commit, String commit_sha1) {
        this.name = name;
        this.commit = commit;
        this.commit_sha1 = commit_sha1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public String getCommit_sha1() {
        return commit_sha1;
    }

    public void setCommit_sha1(String commit_sha1) {
        this.commit_sha1 = commit_sha1;
    }

    public void createBranch() {
        File f = join(REFSHEADS_DIR,this.name);
        try {
            f.createNewFile();
            Utils.writeObject(f,commit_sha1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
