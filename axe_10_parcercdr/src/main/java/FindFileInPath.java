
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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


    public boolean correctFilePath(String pathFile) {

        if (!(pathFile.isEmpty()) && !(pathFile.equals(null)) && pathFile != "" && (Files.exists(Paths.get(pathFile)))) {
            return true;
        } else {
            return false;
        }

    }

    public boolean moveFile(String PathFileOut, String PathFileIn) {
        boolean fileFlug = false;
        if (correctFilePath(PathFileOut) && !(correctFilePath(PathFileIn))) {
            System.out.println("File " + PathFileOut + " parsing and send to path " + PathFileIn + " in " + new Date());
            try {
                Files.move(Paths.get(PathFileOut), Paths.get(PathFileIn), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println(e);
                fileFlug = false;
                e.printStackTrace();
            }
            fileFlug = true;
        } else {
            System.out.println("File exists in directory " + PathFileIn + " " + new Date());
            try {
                    Files.move(Paths.get(PathFileOut), Paths.get(PathFileIn), StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                System.err.println(e);
                fileFlug = false;
                e.printStackTrace();
            }
            fileFlug = true;
        }

        return fileFlug;
    }

    private String pathFile;
    private File file;
    private List<File> listPath = new ArrayList<File>();

    public FindFileInPath(String pathFile) {
        this.pathFile = pathFile;
    }
}
