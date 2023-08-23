package com.falcongames.experiments;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.FileWriter;
import java.io.IOException;

import com.falcongames.xlsx.XlsxProcessor;


public class XlsxExperiment {

    public static void experiment(String fileName, String fileSize, boolean isTrans) throws IOException, InvalidFormatException, InterruptedException {

        String defaultPath = "F:\\code java\\ProcessDocxProject\\src\\main\\resources\\";
        String inputPath = defaultPath + "data\\" + fileName;
        String outputPath = defaultPath + "results\\" + "trans-" + fileName;

        long startTime = System.nanoTime(); // Lấy thời điểm bắt đầu

        XlsxProcessor xlsxProcessor = new XlsxProcessor(inputPath, outputPath ,"vi","en", isTrans);

        xlsxProcessor.processXlsx();


        long endTime = System.nanoTime(); // Lấy thời điểm kết thúc

        long executionTime = endTime - startTime; // Tính thời gian thực hiện (đơn vị: nanoseconds)
        double executionTimeInSeconds = (int)((double) executionTime / 1_000_000)/1000.0; // Chuyển sang giây

//        System.out.println("Thời gian thực hiện: " + executionTime + " nanoseconds");
//        System.out.println("Thời gian thực hiện: " + executionTimeInSeconds + " seconds");

        // Mở tệp để ghi đè nội dung
        FileWriter fileWriter = new FileWriter(defaultPath + "results\\" + "note.txt", true);

        //Chuẩn hoá tên file:
        while(fileName.length() < 25) {
            fileName += " ";
        }
        //Chuẩn hoá  fileSize:
        while(fileName.length() < 6) {
            fileName = " " + fileName;
        }

        // Viết nội dung mới vào tệp
        String newContent ="Tên file: " + fileName + " Kích thước: "+ fileSize +
                "        Thời gian thực hiện: " + executionTimeInSeconds  + " seconds\n";
        fileWriter.write(newContent);

        // Đóng luồng ghi tệp
        fileWriter.close();
    }

}
