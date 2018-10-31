import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FtpSendFile {

    private String ftpAddress;
    private String login;
    private String password;
    private String fileAddress;
    private FTPClient client = null;
    private File file;
    private FileInputStream fileIn;
    private String nameFile = "";

    public FtpSendFile(String ftpAddress, String login, String password) {
        this.ftpAddress = ftpAddress;
        this.login = login;
        this.password = password;
        client = new FTPClient();
    }

    public void ftpSend() {
        if (!(this.fileAddress.isEmpty())) {
            file = new File(this.fileAddress);
            this.nameFile = new File(fileAddress).getName();
        } else {
            System.err.println("File not found " + this.fileAddress);
        }

        try {
            fileIn = new FileInputStream(file);
            client.connect(this.ftpAddress);
            client.enterLocalPassiveMode();
            client.login(this.login, this.password);
            client.storeFile(nameFile, fileIn);
            client.logout();
            client.disconnect();
        } catch (FileNotFoundException f) {
            System.err.println(f);
            f.printStackTrace();
        } catch (IOException e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }

    public void setFileAddress(String fileAddress) {
        this.fileAddress = fileAddress;
    }
}
