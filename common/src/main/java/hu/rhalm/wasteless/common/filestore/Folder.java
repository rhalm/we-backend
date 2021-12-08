package hu.rhalm.wasteless.common.filestore;

import java.util.UUID;

public class Folder {
    public final String name;

    public Folder(String name) {
        this.name = name;
    }

    public String withPath(String path) {
        return this.name + "/" + path;
    }

    public String withPath(UUID path) {
        return withPath(path.toString());
    }

    public static Folder ads = new Folder("ads");

    public static Folder profiles = new Folder("profiles");
}
