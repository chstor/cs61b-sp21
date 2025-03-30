package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.TreeMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Tree implements Serializable {
    private TreeMap<String,String> blobs = new TreeMap<>();

    public Tree() {
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

    public void createTree() {
        String s = Utils.sha1(this.toString());
        String prefix = s.substring(0,2);
        String suffix = s.substring(3);
        File dir = join(OBJECTS_DIR, prefix);
        dir.mkdir();
        File f = join(dir, suffix);
        if(f.exists()) {
            return;
        }
        try {
            f.createNewFile();
            writeObject(f,this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
