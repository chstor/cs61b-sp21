package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Blob implements Serializable {
    private String context;
    public Blob(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void createBlob(){
        String s = Utils.sha1(context);
        String prefix = s.substring(0,2);
        String suffix = s.substring(2);
        File dir = join(OBJECTS_DIR, prefix);
        dir.mkdir();
        File f = join(dir, suffix);
        if(f.exists()){
            return;
        }
        try {
            f.createNewFile();
            writeObject(f,context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
