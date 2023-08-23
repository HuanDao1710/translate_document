package com.falcongames;
import java.io.*;

import com.falcongames.experiments.DocExperiment;
import com.falcongames.experiments.XlsxExperiment;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class Main {


    public static void main(String[] args) throws InvalidFormatException, IOException, InterruptedException {

        System.out.println("Start Program!");

        String directoryPath = "F:\\code java\\ProcessDocxProject\\src\\main\\resources\\" + "data\\";; // Đường dẫn thư mục

        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
//                System.out.println("Các tệp trong thư mục:");
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        double size = file.length() / 1024.0;
                        String fileSize = "";
                        if(size >= 1024) {
                            fileSize = (int)(size * 100/ 1024.0)/100.0 + " MB";
                        } else {
                            fileSize = (int)(size * 100)/100.0 + " KB";
                        }

//                        System.out.println(fileName + " - " + fileSize);
                        System.out.println("========================== Processing File: " + fileName + " ===============================");
                        DocExperiment.experiment(fileName, fileSize, true);
                    }
                }
            }
        } else {
            System.out.println("Thư mục không tồn tại hoặc không phải là thư mục.");
        }

    }

}