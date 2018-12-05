
import java.awt.*;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class Main {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static boolean flug = true;
    private static String fileName;
    private static String csvFilePath;
    private static FindFileInPath fileInPath;

    public static void main(String[] args) throws IOException {
        Properties prop = new Properties();
        InputStream fileProperties = Main.class.getClassLoader().getResourceAsStream("config.properties");
        prop.load(fileProperties);

        String ftpAddress = prop.getProperty("ftpAddress");
        String login = prop.getProperty("login");
        String password = prop.getProperty("password");
        String fileIn = prop.getProperty("fileIn");
        String fileOut = prop.getProperty("fileOut");
        String changeFtpDirectory = prop.getProperty("changeFtpDirectory");
        int timePeriod = Integer.parseInt(prop.getProperty("timePeriod"));
        //Thread mainThread = Thread.currentThread();
        ParserCdrAxe parserAxe = new ParserCdrAxe();
//        ParserCdr parserCdr = new ParserCdr();
        InputStream fileIput;

        //TODO Вывод данных в консоль.
        Console openConsole = System.console();
        if (openConsole == null && !GraphicsEnvironment.isHeadless()) {
            String filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", "cmd", "/k", "java -jar \"" + filename + "\""});
        }

        //TODO Авторизация на FTP сервере
        SendFileFtp sendFile = new SendFileFtp(ftpAddress, login, password);

        //TODO Постоянны цикл для прослушивания директории, парсинга CDR файла и отправки по FTP.
        while (flug) {
            try {
                Thread.currentThread().sleep(timePeriod);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            fileInPath = new FindFileInPath(fileIn);

            for (File fileToProcess : fileInPath.getListPath()) {
                if (fileToProcess.getName().matches("(\\w+)$") && fileToProcess.isFile()) {
                    //TODO создаём путь и имя файлов
                    fileName = fileToProcess.getName() + "_" + sdf.format(System.currentTimeMillis()) + ".csv";
                    csvFilePath = fileOut + "\\" + fileToProcess.getName();
                    //TODO парсим файл CDR в CSV
                    parserAxe.readFile(fileToProcess);
                    parserAxe.parsingString();
//                    parserCdr.parsingString("E:\\AXE\\" + fileName);
                    fileIput = parserAxe.parsingString();
                    //TODO отправка файла
                    sendFile.ftpSend(changeFtpDirectory, fileName, fileIput);
                    fileIput.close();
                    sendFile.ftpDisconnect();
                    //TODO перемещение CDR в другую директорию
                    if (fileInPath.correctFilePath(fileToProcess.getAbsolutePath())) {
                        fileInPath.moveFile(fileToProcess.getAbsolutePath(), csvFilePath);
                    }
                }
            }
        }
    }
}
