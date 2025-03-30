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

    @Override
    public String toString() {
        return "Branch{" +
                "name='" + name + '\'' +
                ", commit_sha1='" + commit_sha1 + '\'' +
                '}';
    }

    public void createBranch() {
        File f = join(REFSHEADS_DIR,this.name);
        if(f.exists()) {
            restrictedDelete(f);
        }
        try {
            f.createNewFile();
            writeObject(f,this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        createBranchBlob();
    }
    public void createBranchBlob(){
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
    public void setSha1(){
        this.commit_sha1 = sha1(commit.toString());
    }

    public void restoreBranch() {
        this.commit = findObjectBySha1(commit_sha1,Commit.class);
    }
}
