package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.TreeMap;
import java.util.TreeSet;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Stage implements Serializable {
    public static final File Stage_File = join(GITLET_DIR, "index");

    private TreeMap<String,String> blobs;
    private TreeSet<String> rmblobs;
    public Stage() {
        blobs = new TreeMap<>();
        rmblobs = new TreeSet<>();
    }

    public TreeMap<String, String> getBlobs() {
        return blobs;
    }

    public void setBlobs(TreeMap<String, String> blobs) {
        this.blobs = blobs;
    }

    public TreeSet<String> getRmblobs() {
        return rmblobs;
    }

    public void setRmblobs(TreeSet<String> rmblobs) {
        this.rmblobs = rmblobs;
    }

    @Override
    public String toString() {
        return "Stage{" +
                "blobs=" + blobs +
                ", rmblobs=" + rmblobs +
                '}';
    }

    public static void createStage(){
        try {
             Stage_File.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void createStageBlob() {
        Stage_File.delete();
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
}
