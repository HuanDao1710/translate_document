package com.falcongames.txt;

import com.falcongames.utils.TranslatorUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TxtProcessor {

    private boolean isTrans;
    private String inputPath;
    private String outputPath;
    private  String langFrom;

    private  String langTo;
    public TxtProcessor(String inputPath, String outputPath , String langFrom, String langTo, boolean isTrans) {

        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.langFrom = langFrom;
        this.langTo = langTo;
        this.isTrans = isTrans;
    }


    public  void  translateTxt() {

        try (FileInputStream fis = new FileInputStream(inputPath);

             // Đọc file txt
             InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
             BufferedReader reader = new BufferedReader(isr)) {

            // dịch
            List<String> listLineTrans = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                String lineTrans = TranslatorUtil.translate(this.langFrom,this.langTo,line);
                System.out.println(lineTrans);
                listLineTrans.add(lineTrans);
            }

            // ghi file txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputPath))) {
                for (String _line : listLineTrans) {
                    writer.write(_line);
                    writer.newLine(); // Xuống dòng sau mỗi dòng
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
//        String inputPath = "F:\\code java\\ProcessDocxProject\\src\\main\\resources\\results\\note.txt";
//        String outputPath = "F:\\code java\\ProcessDocxProject\\src\\main\\resources\\results\\" + "trans" + "-note.txt";
//
//        translateTxt(inputPath, outputPath);


    }

}
