
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FindFileInPath {

    public List<File> getListPath() {
        file = new File(this.pathFile);
        if (correctFilePath(this.pathFile)) {
            listPath.addAll(Arrays.asList(file.listFiles()));
        } else {
            System.err.println("File not found in path " + this.pathFile);
        }
        return listPath;
    }

    public void copyFile(String pathFileIn, String pathFileOut) {
        if (correctFilePath(pathFileIn) && !(correctFilePath(pathFileOut))) {
            try {
                Files.copy(Paths.get(pathFileIn), Paths.get(pathFileOut));
                System.out.println("File "+ pathFileIn + " parsing and send to path " + pathFileOut + " in " + new Date());
            } catch (IOException e) {
                System.err.println(e);
                e.printStackTrace();
            }
        }else {
            System.out.println("File exists in directory " + pathFileOut + " "+ new Date());
        }
    }

    public boolean correctFilePath(String pathFile) {

        if (!(pathFile.isEmpty()) && !(pathFile.equals(null)) && pathFile != "" && (Files.exists(Paths.get(pathFile)))) {
            return true;
        } else {
            return false;
        }

    }

    public boolean deleteFile(String pathDeleteFile, String pathFileExist) {
        boolean fileFlug = false;
        if (correctFilePath(pathFileExist)) {
            try {
                fileFlug = Files.deleteIfExists(Paths.get(pathDeleteFile));
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return fileFlug;
    }

    public void deleteFile(File pathDeleteFile) {

        //System.gc();
        Runtime.getRuntime().gc();
        pathDeleteFile.delete();
        //Files.deleteIfExists(Paths.get(pathDeleteFile.getAbsolutePath()));
//            Files.walk(Paths.get(pathDeleteFile.getAbsolutePath()) )
//                    .sorted(Comparator.reverseOrder())
//                    .map(Path::toFile)
//                    .forEach(File::delete);
    }


    private String pathFile;
    private File file;
    private List<File> listPath = new ArrayList<File>();


    public FindFileInPath(String pathFile) {
        this.pathFile = pathFile;
    }
}
