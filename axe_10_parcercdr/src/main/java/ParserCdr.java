import java.io.*;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ParserCdr {
    private String a;
    //private int num = 0;
    private boolean sig = true;
    private StringBuffer bufer;
    private String signal = "";
    private String listword;
    private String numbe = "";
    private String word;
    private String numA;
    private String numAInter;
    private String numB;

    private int numStart = 0;
    private int numEnd = 0;

    //private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private File fileIn;
    //private String filePath = "E:\\AXE\\";
    //private String fileName = filePath + fileIn.getName() + "_" + sdf.format(System.currentTimeMillis()) + ".csv";
    private List<StringBuffer> stringNumber = new ArrayList<StringBuffer>();

    public void setFileIn(File fileIn) {
        this.fileIn = fileIn;
    }

    public void readFile() {
        bufer = new StringBuffer();
        try (BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(this.fileIn), "UTF-8"))) {
            while ((a = buf.readLine()) != null) {
                byte[] b = a.getBytes();
                for (byte by : b) {
                    if (by != 0 && by != 10 && by != 13) {
                        bufer.append((char) by);
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.err.println(e);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println(e);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e);
        }
    }

    public void parsingString(String fileName) {

        try {
            listword = bufer.toString();
            while (sig) {
                listword = listword.trim();
                if (listword.length() >= 258) {
                    numbe = listword.substring(2, 3);
                } else {
                    signal = "";
                    sig = false;
                    continue;
                }

                if (numbe.equals("0") || numbe.equals("4")) {
                    signal = listword.substring(0, 2);
                } else if (listword.length() >= 258) {
                    listword = listword.substring(1, listword.length());
                    continue;
                } else {
                    signal = "";
                    sig = false;
                    continue;
                }

                switch (signal) {
                    case "00":
                        sig = true;
                        //numEnd = 258;
                        numEnd = listword.length() >= 258 ? 258 : listword.length();
                        word = listword.substring(numStart, numEnd);
                        listword = listword.substring(numEnd, listword.length());
                        stringNumber.add(new StringBuffer(word));
                        break;
                    case "02":
                        sig = true;
                        //numEnd = 300;
                        numEnd = listword.length() >= 300 ? 300 : listword.length();
                        word = listword.substring(numStart, numEnd);
                        listword = listword.substring(numEnd, listword.length());
                        stringNumber.add(new StringBuffer(word));
                        break;
                    case "04":
                        sig = true;
                        //numEnd = 335;
                        numEnd = listword.length() >= 335 ? 335 : listword.length();
                        word = listword.substring(numStart, numEnd);
                        listword = listword.substring(numEnd, listword.length());
                        stringNumber.add(new StringBuffer(word));
                        break;
                    case "09":
                        sig = true;
                        numEnd = listword.length() >= 320 ? 320 : listword.length();
                        word = listword.substring(numStart, numEnd);
                        listword = listword.substring(numEnd, listword.length());
                        stringNumber.add(new StringBuffer(word));
                        break;
                    default:
                        sig = false;
                        break;
                }
            }

            PrintWriter printCSV = new PrintWriter(new File(fileName));
            StringBuffer csvFile = new StringBuffer();

            SimpleDateFormat newDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            SimpleDateFormat newTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date dateFormat;
            Date timeFormat;
            Time time;

            for (StringBuffer string : stringNumber) {
                signal = string.substring(0, 2);
                switch (signal) {
                    case "00":
                        csvFile.append(string.substring(5, 13) + ";");

                        try {
                            dateFormat = new SimpleDateFormat("yyMMdd").parse(string.substring(132, 138));
                            timeFormat = new SimpleDateFormat("HHmmss").parse(string.substring(138, 144));
                            csvFile.append(newDateFormat.format(dateFormat) + " " + newTimeFormat.format(timeFormat) + ";");
                        } catch (ParseException p) {
                            dateFormat = new Date();
                            time = new Time(new Date().getTime());
                            csvFile.append(newDateFormat.format(dateFormat) + " " + time.toString() + ";");
                        }

                        csvFile.append(Integer.parseInt(string.substring(156, 162)) + ";");

                        numA = (string.substring(27, 37)).trim();
                        csvFile.append(numA + ";");

                        numAInter = (string.substring(75, 95)).trim();
                        csvFile.append(numAInter + ";");

                        numB = ((string.substring(48, 66)).trim()).replaceAll("F", "");
                        numB = numB.replaceFirst("^(810|860|8)", "");
                        csvFile.append(numB + ";");

                        csvFile.append((string.substring(226, 233)).trim() + ";");
                        csvFile.append((string.substring(233, 240)).trim() + ";");

                        numA = numA.matches("^\\d+") ? "7" + numA : numAInter;
                        csvFile.append(numA + ";");
                        numB = numB.matches("^\\d+") ? "7" + numB : numB;
                        csvFile.append(numB + "\n");
                        break;
                    case "02":
                        csvFile.append(string.substring(5, 13) + ";");

                        try {
                            dateFormat = new SimpleDateFormat("yyMMdd").parse(string.substring(132, 138));
                            timeFormat = new SimpleDateFormat("HHmmss").parse(string.substring(138, 144));
                            csvFile.append(newDateFormat.format(dateFormat) + " " + newTimeFormat.format(timeFormat) + ";");
                        } catch (ParseException p) {
                            dateFormat = new Date();
                            time = new Time(new Date().getTime());
                            csvFile.append(newDateFormat.format(dateFormat) + " " + time.toString() + ";");
                        }

                        csvFile.append(Integer.parseInt(string.substring(156, 162)) + ";");

                        numA = (string.substring(27, 37)).trim();
                        csvFile.append(numA + ";");

                        numAInter = (string.substring(75, 95)).trim();
                        csvFile.append(numAInter + ";");

                        numB = ((string.substring(48, 66)).trim()).replaceAll("F", "");
                        numB = numB.replaceFirst("^(810|860|8)", "");
                        csvFile.append(numB + ";");

                        csvFile.append((string.substring(233, 240)).trim() + ";");
                        csvFile.append((string.substring(240, 247)).trim() + ";");

                        numA = numA.matches("^\\d+") ? "7" + numA : numAInter;
                        csvFile.append(numA + ";");
                        numB = numB.matches("^\\d+") ? "7" + numB : numB;
                        csvFile.append(numB + "\n");

                        break;
                    case "04":
                        csvFile.append(string.substring(5, 13) + ";");

                        try {
                            dateFormat = new SimpleDateFormat("yyMMdd").parse(string.substring(176, 182));
                            timeFormat = new SimpleDateFormat("HHmmss").parse(string.substring(181, 188));
                            csvFile.append(newDateFormat.format(dateFormat) + " " + newTimeFormat.format(timeFormat) + ";");
                        } catch (ParseException p) {
                            dateFormat = new Date();
                            time = new Time(new Date().getTime());
                            csvFile.append(newDateFormat.format(dateFormat) + " " + time.toString() + ";");
                        }

                        csvFile.append(Integer.parseInt(string.substring(200, 206)) + ";");

                        numA = (string.substring(27, 37)).trim();
                        csvFile.append(numA + ";");

                        numAInter = (string.substring(76, 95)).trim();
                        csvFile.append(numAInter + ";");

                        numB = ((string.substring(48, 66)).trim()).replaceAll("F", "");
                        numB = numB.replaceFirst("^(810|860|8)", "");
                        csvFile.append(numB + ";");

                        csvFile.append((string.substring(270, 277)).trim() + ";");
                        csvFile.append((string.substring(277, 284)).trim() + ";");

                        numA = numA.matches("^\\d+") ? "7" + numA : numAInter;
                        csvFile.append(numA + ";");
                        numB = numB.matches("^\\d+") ? "7" + numB : numB;
                        csvFile.append(numB + "\n");
                        break;
                    case "09":
                        csvFile.append(string.substring(5, 13) + ";");

                        try {
                            dateFormat = new SimpleDateFormat("yyMMdd").parse(string.substring(132, 138));
                            timeFormat = new SimpleDateFormat("HHmmss").parse(string.substring(138, 144));
                            csvFile.append(newDateFormat.format(dateFormat) + " " + newTimeFormat.format(timeFormat) + ";");
                        } catch (ParseException p) {
                            dateFormat = new Date();
                            time = new Time(new Date().getTime());
                            csvFile.append(newDateFormat.format(dateFormat) + " " + time.toString() + ";");
                        }

                        csvFile.append(Integer.parseInt(string.substring(156, 162)) + ";");

                        numA = (string.substring(27, 37)).trim();
                        csvFile.append(numA + ";");

                        numAInter = (string.substring(75, 95)).trim();
                        csvFile.append(numAInter + ";");

                        numB = ((string.substring(48, 66)).trim()).replaceAll("F", "");
                        numB = numB.replaceFirst("^(810|860|8)", "");
                        csvFile.append(numB + ";");

                        csvFile.append((string.substring(233, 240)).trim() + ";");
                        csvFile.append((string.substring(240, 247)).trim() + ";");

                        numA = numA.matches("^\\d+") ? "7" + numA : numAInter;
                        csvFile.append(numA + ";");
                        numB = numB.matches("^\\d+") ? "7" + numB : numB;
                        csvFile.append(numB + "\n");
                        break;
                }
            }
            printCSV.write(csvFile.toString());
            printCSV.flush();
            printCSV.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
