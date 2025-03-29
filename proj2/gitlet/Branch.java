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

    public Branch(String name, Commit commit) {
        this.name = name;
        this.commit = commit;
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
        restrictedDelete(f);
        try {
            f.createNewFile();
            Utils.writeObject(f,this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setSha1(){
        this.commit_sha1 = sha1(commit);
    }

    public void restoreBranch() {
        this.commit = findObjectBySha1(commit_sha1,Commit.class);
    }
}
