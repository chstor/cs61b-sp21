package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Head implements Serializable {
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    private transient Commit commit;
    private transient Branch branch;
    private String commit_sha1;
    private String branch_sha1;

    public Head(Commit commit,Branch branch) {
        this.commit = commit;
        this.branch = branch;
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

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public String getBranch_sha1() {
        return branch_sha1;
    }

    public void setBranch_sha1(String branch_sha1) {
        this.branch_sha1 = branch_sha1;
    }

    public void createHead() {
        restrictedDelete(HEAD_FILE);
        File f = HEAD_FILE;
        try {
            f.createNewFile();
            writeObject(f,this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void restoreHead() {
        this.commit = findObjectBySha1(commit_sha1,Commit.class);
        this.branch = findObjectBySha1(branch_sha1,Branch.class);
    }

    public void setSha1() {
        this.commit_sha1 = sha1(commit);
        this.branch_sha1 = sha1(branch);
    }
}
