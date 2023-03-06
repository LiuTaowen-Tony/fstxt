package filesystems;


import java.util.*;
import java.util.stream.Collectors;

public final class DocDirectory extends DocFile {
    private final Map<String, DocFile> subFiles = new HashMap<>();

    public DocDirectory(String name) {
        super(name);
    }

    @Override
    public int getSize() {
        return getName().length();
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean isDataFile() {
        return false;
    }

    @Override
    public DocDirectory asDirectory() {
        return this;
    }

    @Override
    public DocDataFile asDataFile() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DocFile duplicate() {
        var result = new DocDirectory(getName());
        for (var file : subFiles.values()) {
            result.addFile(file.duplicate());
        }
        return result;
    }

    public void addFile(DocFile file) {
        if (subFiles.containsKey(file.getName() )) {
            throw new IllegalArgumentException();
        }
        subFiles.put(file.getName(), file);
    }

    public Set<DocFile> getAllFiles() {
        return new HashSet<>(subFiles.values());
    }

    public boolean containsFile(String name) {
        return subFiles.containsKey(name);
    }

    public Set<DocDirectory> getDirectories() {
        return subFiles.values().stream().filter(DocFile::isDirectory).map(DocFile::asDirectory).collect(Collectors.toSet());
    }

    public Set<DocDataFile> getDataFiles() {
        return subFiles.values().stream().filter(DocFile::isDataFile).map(DocFile::asDataFile).collect(Collectors.toSet());
    }

    public DocFile getFile(String name) {
        return subFiles.get(name);
    }

    public boolean removeFile(String name) {
        return subFiles.remove(name) != null;
    }
}
