import org.apache.commons.net.ftp.FTPClient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class SendFileFtp {

    private String ftpAddress;
    private String login;
    private String password;
    private FTPClient client = null;

    public SendFileFtp(String ftpAddress, String login, String password) {
        this.ftpAddress = ftpAddress;
        this.login = login;
        this.password = password;
        client = new FTPClient();
    }

    public void ftpSend(String workingDirectory, String fileName, InputStream inputStream) {
        try {
            client.connect(this.ftpAddress);
            client.enterLocalPassiveMode();
            client.login(this.login, this.password);
            if (workingDirectory != null && workingDirectory != "") {
                if (!(client.changeWorkingDirectory(workingDirectory)))
                    System.out.println("File transfer failed");;
            }
            client.storeFile(fileName, inputStream);
        } catch (FileNotFoundException f) {
            System.err.println(f);
            f.printStackTrace();
        } catch (IOException e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }

    public void ftpDisconnect() {
        try {
            client.logout();
            client.disconnect();
        } catch (IOException e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}
