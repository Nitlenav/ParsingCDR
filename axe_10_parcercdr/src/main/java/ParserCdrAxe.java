import java.io.*;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ParserCdrAxe {

    private int countNum = 10;
    private int numStart = 0;
    private int numEnd = 0;
    private byte[] byteFile;
    private boolean sig = true;

    private String a;
    private StringBuilder bufer;
    private StringBuffer csvFile;
    private String signal = "";
    private String listword;
    private String numbe = "";
    private String word;
    private String numA;
    private String numAInter;
    private String numB;

    private List<StringBuffer> stringNumber;
    private InputStream inputStream;
    private SimpleDateFormat newDateFormat;
    SimpleDateFormat newTimeFormat;

    public void readFile(File fileIn) {
        bufer = new StringBuilder("");
        try (BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(fileIn), "UTF-8"))) {
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

    public InputStream parsingString() {
        stringNumber = new ArrayList<StringBuffer>();
        csvFile = new StringBuffer();
        sig = true;
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
                    numEnd = listword.length() >= 258 ? 258 : listword.length();
                    word = listword.substring(numStart, numEnd);
                    listword = listword.substring(numEnd, listword.length());
                    stringNumber.add(new StringBuffer(word));
                    break;
                case "02":
                    sig = true;
                    numEnd = listword.length() >= 300 ? 300 : listword.length();
                    word = listword.substring(numStart, numEnd);
                    listword = listword.substring(numEnd, listword.length());
                    stringNumber.add(new StringBuffer(word));
                    break;
                case "04":
                    sig = true;
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

        newDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        newTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        for (StringBuffer string : stringNumber) {
            signal = string.substring(0, 2);
            switch (signal) {
                case "00":
                    parsing(string,
                            132, 138,
                            138, 144,
                            156, 162,
                            27, 37,
                            75, 95,
                            48, 66,
                            226, 233,
                            233, 240);
                    break;
                case "02":
                    parsing(string,
                            132, 138,
                            138, 144,
                            156, 162,
                            27, 37,
                            75, 95,
                            48, 66,
                            233, 240,
                            240, 247);
                    break;
                case "04":
                    parsing(string,
                            176, 182,
                            182, 188,
                            200, 206,
                            27, 37,
                            76, 95,
                            48, 66,
                            270, 277,
                            277, 284);
                    break;
                case "09":
                    parsing(string,
                            132, 138,
                            138, 144,
                            156, 162,
                            27, 37,
                            75, 95,
                            48, 66,
                            233, 240,
                            240, 247);
                    break;
            }
        }


        byteFile = csvFile.toString().getBytes();
        inputStream = new ByteArrayInputStream(byteFile, 0, byteFile.length);
        return inputStream;
    }

    private void parsing(StringBuffer string,
                         int dateformatStart, int dateFormatEnd,
                         int timeFormatStart, int timeFormatEnd,
                         int timeStart, int timeEnd,
                         int numAStart, int numAEnd,
                         int numAinterStart, int numAInterEnd,
                         int numBStart, int numBEnd,
                         int inputStart, int inputEnd,
                         int outputStart, int outputEnd) {
        Date dateFormat;
        Date timeFormat;
        Time time;
        String output;
//Номер
        csvFile.append(string.substring(5, 13) + ";");
//Дата
        try {
            dateFormat = new SimpleDateFormat("yyMMdd").parse(string.substring(dateformatStart, dateFormatEnd));
            timeFormat = new SimpleDateFormat("HHmmss").parse(string.substring(timeFormatStart, timeFormatEnd));
            csvFile.append(newDateFormat.format(dateFormat) + " " + newTimeFormat.format(timeFormat) + ";");
        } catch (ParseException p) {
            dateFormat = new Date();
            time = new Time(new Date().getTime());
            csvFile.append(newDateFormat.format(dateFormat) + " " + time.toString() + ";");
        }
//Время
        csvFile.append(Integer.parseInt(string.substring(timeStart, timeEnd)) + ";");
//Номер А
        numA = (string.substring(numAStart, numAEnd)).trim();
        csvFile.append(numA + ";");
//Номер А промежуточный
        numAInter = (string.substring(numAinterStart, numAInterEnd)).trim();
        csvFile.append(numAInter + ";");
//Номер В начало обработки
        numB = ((string.substring(numBStart, numBEnd)).trim()).replaceAll("F", "");
        output = numAInter.matches("^(860810|860)") ?
                (numAInter.matches("^(860810)") ? "SIP_MN" : "SIP")
                : (string.substring(outputStart, outputEnd)).trim();
//Номер В конец обработки
        numB = numB.replaceFirst("^(860|8)", "");
        csvFile.append(numB + ";");
//Направление Input
        csvFile.append((string.substring(inputStart, inputEnd)).trim() + ";");
//Направление Output
        csvFile.append(output + ";");
//Номер А, предпоследняя колонка
        numA = numAInter.matches("^\\d+") ? "7" + numAInter : "7" + numA;
        csvFile.append(numA + ";");
//Номер В, последняя колонка, условие для отслеживания международных звонков
        if (!(numB.matches("^(860810|810)"))){
            numB = numB.matches("^\\d+") ? (numB.length() >= countNum ? "7" + numB : "7495" + numB) : numB;
        }else {
            numB = numB.replaceFirst("^(860810|810)", "");
        }
        csvFile.append(numB + "\n");
    }
}
