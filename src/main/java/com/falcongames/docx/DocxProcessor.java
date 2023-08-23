package com.falcongames.docx;

import com.falcongames.constant.SystemConstant;
import com.falcongames.utils.TranslatorUtil;
import org.apache.poi.xwpf.usermodel.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DocxProcessor {

    private boolean isTrans;
    private String inputPath;
    private String outputPath;
    private  String langFrom;

    private  String langTo;

    private final String specialToken = "[AA]";


    public DocxProcessor(String inputPath, String outputPath , String langFrom, String langTo, boolean isTrans) {

        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.langFrom = langFrom;
        this.langTo = langTo;
        this.isTrans = isTrans;
    }


    public  void processDocx()
            throws IOException, InterruptedException {

        FileInputStream fileInput = new FileInputStream(inputPath);
        XWPFDocument doc = new XWPFDocument(fileInput);

        //Paragraph
        translateParagrapsMultiThread(doc.getParagraphs());
//        for (XWPFParagraph p : doc.getParagraphs()) {
//            translateParagraph(p);
//        }

        //Table
        for (XWPFTable tbl : doc.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    cell.getTables();

                    translateParagrapsMultiThread(cell.getParagraphs());
//                    for (XWPFParagraph p : cell.getParagraphs()) {
//
//                        translateParagraph(p);
//
//                    }
                }
            }
        }
        doc.write(new FileOutputStream(outputPath));
        fileInput.close();

    }

    private void translateParagrapsMultiThread(List<XWPFParagraph> listParagraph) throws InterruptedException {
        // Tạo mảng để lưu các luồng
       /* Thread[] threads = new Thread[SystemConstant.numThreads];

        for (int i = 0; i < SystemConstant.numThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                for (int j = threadIndex; j < listParagraph.size(); j += SystemConstant.numThreads) {
                    try {
                        translateParagraph(listParagraph.get(j));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            threads[i].start();
        }

        // Chờ cho tất cả luồng hoàn thành
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        // Tạo ThreadPool với số luồng tương ứng
        ExecutorService executorService = Executors.newFixedThreadPool(SystemConstant.numThreads);

        for (XWPFParagraph p : listParagraph) {
            // Gửi tác vụ dịch vào ThreadPool
            executorService.execute(() -> {
                try {
                    translateParagraph(p);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // Đóng ThreadPool sau khi hoàn thành
        executorService.shutdown();

        executorService.awaitTermination(1, TimeUnit.HOURS);

    }

    private void translateParagraph(XWPFParagraph p) throws IOException {

        List<XWPFRun> runs = p.getRuns();

        if (runs != null) {
            StringBuilder stringBuilder = new StringBuilder();

            for (XWPFRun r : runs) {
                String text = r.getText(0);
                stringBuilder.append(text);
                stringBuilder.append(specialToken);
            }

            System.out.println(stringBuilder);

            String textTranslate;
            if(isTrans) {
                textTranslate = (TranslatorUtil
                        .translate(langFrom,langTo, stringBuilder.toString()));

            } else {
                textTranslate = stringBuilder.toString();
            }
            System.out.println(textTranslate);

            System.out.println("runsize: " + runs.size());
            //List<String> listTextTranslate = List.of(textTranslate.split("\\[SEP\\]"));
            List<String> listTextTranslate = List.of(textTranslate.split("\\[AA\\]"));

            System.out.println("transsize: " + listTextTranslate.size());

            int minSize = runs.size() <= listTextTranslate.size() ? runs.size() : listTextTranslate.size();

            for(int i = 0 ; i < minSize; i++) {
                replaceTextRun(runs.get(i),listTextTranslate.get(i).replace("[SEP]",""));
            }

        }

    }


    private void replaceTextRun(XWPFRun run, String text) {
        String _text = run.getText(0);
        if (_text != null) {
            run.setText(text , 0);
        }
    }

    private int countTextLength(List<XWPFRun> runs, int first, int last) {
        String text;
        int target = 0;
        for (int i = first; i <= last; i++) {
            text = runs.get(i).getText(0);
            if (text != null) {
                target += text.length();
            }
        }
        return target;
    }

    private void removeRuns(XWPFParagraph paragraph, int first, int last) {
        for (int i = last; i >= first; i--) {
            paragraph.removeRun(i);
        }
    }


}
