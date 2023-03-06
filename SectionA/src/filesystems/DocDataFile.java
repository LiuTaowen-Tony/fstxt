package filesystems;

import java.util.Arrays;

public final class DocDataFile extends DocFile {
    private final byte[] content;
    DocDataFile(String name, byte[] content) {
        super(name);
        this.content = content;
    }

    @Override
    public int getSize() {
        return content.length + getName().length();
    }


    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isDataFile() {
        return true;
    }

    @Override
    public DocDirectory asDirectory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DocDataFile asDataFile() {
        return this;
    }

    @Override
    public DocFile duplicate() {
        return new DocDataFile(getName(), Arrays.copyOf(content, content.length));
    }

    public boolean containsByte(byte target) {
        for (var ch : content) {
            if (target == ch) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (! (other instanceof DocDataFile)) {
            return false;
        }
        var that = (DocDataFile) other;

        return Arrays.equals(content, that.content) && getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(content) + getName().hashCode();
    }
}
