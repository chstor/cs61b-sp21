package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Head implements Serializable {

    private transient Commit commit;
    private String commit_sha1;

    public Head(Commit commit, String commit_sha1) {
        this.commit = commit;
        this.commit_sha1 = commit_sha1;
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

    public void createHead() {
        File f = join(GITLET_DIR,"HEAD");
        try {
            f.createNewFile();
            writeContents(f,commit_sha1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
