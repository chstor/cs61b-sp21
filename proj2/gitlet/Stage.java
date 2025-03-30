package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.TreeMap;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Stage implements Serializable {
    public static final File Stage_File = join(GITLET_DIR, "index");

    private TreeMap<String,String> blobs;

    public Stage() {
        blobs = new TreeMap<>();
    }

    public TreeMap<String, String> getBlobs() {
        return blobs;
    }

    public void setBlobs(TreeMap<String, String> blobs) {
        this.blobs = blobs;
    }

    public static void createStage(){
        try {
             Stage_File.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void createStageBlob() {
        restrictedDelete(Stage_File);
        String s = Utils.sha1(this);
        String prefix = s.substring(0,2);
        String suffix = s.substring(3);
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
}
