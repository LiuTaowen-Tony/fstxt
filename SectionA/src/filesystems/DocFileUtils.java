package filesystems;

import java.util.Optional;

public class DocFileUtils {

  /**
   * Compute the total size, in bytes, of the directory and all of its contents.
   * @param directory A directory.
   * @return The size of this directory plus, the sum of the sizes of all files contained directly
   *         or indirectly in this directory.
   */
  public static int getTotalDirectorySize(DocDirectory directory) {
    return directory.getDirectories().stream().mapToInt(DocFileUtils::getTotalDirectorySize).sum() +
            directory.getDataFiles().stream().mapToInt(DocDataFile::getSize).sum() +
            directory.getSize();

  }

  /**
   * Copy a named file between directories.
   * @param src A source directory.
   * @param dst A destination directory.
   * @param filename The name of a file to be copied.
   * @return False if the source directory does not contain a file with the given name, or
   *         if the destination directory already contains a file with the given name.  Otherwise,
   *         create a duplicate of the file with the given name in the source directory and add
   *         this duplicate to the destination directory.
   */
  public static boolean copy(DocDirectory src, DocDirectory dst, String filename) {
    // TODO: implement as part of Question 4
    if (!src.containsFile(filename) || dst.containsFile(filename)) {
      return false;
    }
    var newFile = src.getFile(filename).duplicate();
    dst.addFile(newFile);
    return true;
  }

  /**
   * Locate a file containing a given byte and lying at or beneath a given file, if one exists.
   * @param root A file, to be recursively searched.
   * @param someByte A byte to be searched for.
   * @return An empty optional if no file at or beneath the given root file contains the
   *         given byte.  Otherwise, return an optional containing any such file.
   */
  public static Optional<DocDataFile> searchForByte(DocFile root, byte someByte) {
    // TODO: implement as part of Question 4
    if (root.isDataFile()) {
      DocDataFile dataFile = root.asDataFile();
      if (dataFile.containsByte(someByte)){
        return Optional.of(dataFile);
      }
      else {
        return Optional.empty();
      }
    }
    DocDirectory directory = root.asDirectory();
    for (var datafile : directory.getAllFiles()) {
      Optional<DocDataFile> fileOptional = searchForByte(datafile, someByte);
      if (fileOptional.isPresent()) {
        return fileOptional;
      }
    }
    return Optional.empty();
  }

}
