
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Main {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static boolean flug = true;

    public static void main(String[] args) throws IOException {
        Properties prop = new Properties();
        InputStream fileProperties = Main.class.getClassLoader().getResourceAsStream("config.properties");
                //new FileInputStream("config.properties");
        prop.load(fileProperties);
        String ftpAddress = prop.getProperty("ftpAddress");
        String login = prop.getProperty("login");
        String password = prop.getProperty("password");
        String fileIn = prop.getProperty("fileIn");
        String fileOut = prop.getProperty("fileOut");
        int timePeriod = Integer.parseInt(prop.getProperty("timePeriod"));


        final Thread mainThread = Thread.currentThread();

        //TODO Вывод данных в консоль.
        Console openConsole = System.console();
        if (openConsole == null && !GraphicsEnvironment.isHeadless()) {
            String filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "cmd", "/k", "java -jar \"" + filename + "\""});
        }

        Runtime.getRuntime().addShutdownHook(
                new Thread() {
                    public void run() {
                        try {
                            flug = false;
                            mainThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        //TODO Авторизация на FTP сервере
        FtpSendFile ftpSendFile = new FtpSendFile(ftpAddress, login, password);

        List<String> listDeleteFile = new ArrayList<String>();


        //TODO Постоянны цикл для прослушивания директории, парсинга CDR файла и отправки по FTP.
        while (flug) {

            try {
                Thread.currentThread().sleep(timePeriod);
                System.gc();
            } catch (InterruptedException e) {
                Runtime.getRuntime().gc();
                e.printStackTrace();
            }

            FindFileInPath fileInPath = new FindFileInPath(fileIn);
            for (File fileToProcess : fileInPath.getListPath()) {
                if (fileToProcess.getName().matches("(\\w+)$") && fileToProcess.isFile()) {
                    String fileName = fileToProcess.getAbsolutePath() + "_" + sdf.format(System.currentTimeMillis()) + ".csv";
                    String csvFilePath = fileOut + "\\" + fileToProcess.getName();
                    fileInPath.copyFile(fileToProcess.getAbsolutePath(), csvFilePath);
                    ParserCdr parserCdr = new ParserCdr();
                    parserCdr.setFileIn(fileToProcess);
                    parserCdr.readFile();
                    parserCdr.parsingString(fileName);
                    fileInPath.deleteFile(fileToProcess.getAbsolutePath(), csvFilePath);

                    ftpSendFile.setFileAddress(fileName);
                    ftpSendFile.ftpSend();

                    listDeleteFile.add(fileName);
                }
            }
            for (File fileToProcess : fileInPath.getListPath()) {
                if ((fileToProcess.getName()).matches("([^\\s]+(\\.(?i)(csv))$)") && fileToProcess.isFile()) {
                    fileInPath.deleteFile(fileToProcess);
                }
            }
        }
    }
}
