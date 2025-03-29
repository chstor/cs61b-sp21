package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.TreeMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Tree implements Serializable {
    private TreeMap<String,String> blobs = new TreeMap<>();
    private String childTree_sha1;

    public Tree() {
        blobs = new TreeMap<>();
    }

    public Tree(TreeMap<String, String> blobs) {
        this.blobs = blobs;
    }

    public TreeMap<String, String> getBlobs() {
        return blobs;
    }

    public void setBlobs(TreeMap<String, String> blobs) {
        this.blobs = blobs;
    }

    public String getChildTree_sha1() {
        return childTree_sha1;
    }

    public void setChildTree_sha1(String childTree_sha1) {
        this.childTree_sha1 = childTree_sha1;
    }
    public void createTree() {
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
