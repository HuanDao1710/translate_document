package com.falcongames.xlsx;

import com.falcongames.constant.SystemConstant;
import com.falcongames.utils.TranslatorUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRElt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;
import org.apache.poi.ss.usermodel.CellType;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class XlsxProcessor {
    private XSSFWorkbook workbook;
    private boolean isTrans;
    private String inputPath;
    private String outputPath;
    private  String langFrom;
    private  String langTo;
    private final String specialToken = "[SEP]";

    public XlsxProcessor(String inputPath, String outputPath, String  langFrom, String langTo, boolean isTrans) {

        this.langFrom = langFrom;
        this.langTo = langTo;
        this.isTrans = isTrans;
        this.outputPath = outputPath;
        this.inputPath = inputPath;

        readFileXlsx();


    }

    private void readFileXlsx() {
        FileInputStream file = null;
        try {

            file = new FileInputStream(this.inputPath);

        } catch (FileNotFoundException e) {

            throw new RuntimeException(e);

        }
        try {

            this.workbook = new XSSFWorkbook(file);

        } catch (IOException e) {

            throw new RuntimeException(e);

        }

    }


    public void processSheet(XSSFSheet sheet) throws IOException {

        // Iterate through each rows one by one
        Iterator<Row> rowIterator = sheet.iterator();
    
            

        // Till there is an element condition holds true
        while (rowIterator.hasNext()) {

            Row row = rowIterator.next();

            // columns
            Iterator<Cell> cellIterator  = row.cellIterator();

            while (cellIterator.hasNext()) {

                Cell cell = cellIterator.next();
                if(cell.getCellType().equals(CellType.STRING)) {
                    translateCell(cell);
                }

            }

        }

    }

    public void processSheetMultiThread(XSSFSheet sheet) throws IOException {

        List<Cell> listCell = new ArrayList<>();

        // Iterate through each rows one by one
        Iterator<Row> rowIterator = sheet.iterator();

        // Till there is an element condition holds true
        while (rowIterator.hasNext()) {

            Row row = rowIterator.next();

            // columns
            Iterator<Cell> cellIterator  = row.cellIterator();

            while (cellIterator.hasNext()) {

                Cell cell = cellIterator.next();
                if(cell.getCellType().equals(CellType.STRING)) {
//                    translateCell(cell);
                    listCell.add(cell);
                }

            }

        }


        // Tạo ThreadPool với số luồng tương ứng
        ExecutorService executorService = Executors.newFixedThreadPool(SystemConstant.numThreads);

        for (Cell cell : listCell) {
            // Gửi tác vụ dịch vào ThreadPool
            executorService.execute(() -> {
                try {
                    translateCell(cell);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // Đóng ThreadPool sau khi hoàn thành
        executorService.shutdown();

        try {
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void translateCell(Cell cell) throws IOException {
        XSSFRichTextString richText = (XSSFRichTextString) cell.getRichStringCellValue();

        CTRst ctRst = richText.getCTRst();

        List<CTRElt> runs = ctRst.getRList();

        if( runs.size()!= 0)   {


            StringBuilder stringBuilder = new StringBuilder();

            for (CTRElt r : runs) {
                String text = r.getT();
                stringBuilder.append(text);
                stringBuilder.append(specialToken);
            }

            System.out.println("==========TextBefore== " + stringBuilder +"  ===endTextBefore========");

            String textTranslate;
            if (isTrans) {
                textTranslate = (TranslatorUtil
                        .translate(langFrom, langTo, stringBuilder.toString()));

            } else {
                textTranslate = stringBuilder.toString();
            }


            System.out.println("==========TextAfterTrans== " + textTranslate + "  ===endTextAfter=========");

            System.out.println("runsize: " + runs.size());
            List<String> listTextTranslates = List.of(textTranslate.split("\\[SEP\\]"));
            System.out.println("transsize: " + listTextTranslates.size());

            int minSize  = runs.size() <= listTextTranslates.size() ? runs.size() : listTextTranslates.size();


            for (int i = 0; i < minSize; i++) {
                runs.get(i).setT(listTextTranslates.get(i));
            }


        } else {


            String newText = cell.getStringCellValue();
            Font cellFont = workbook.getFontAt(cell.getCellStyle().getFontIndex());


            System.out.println("==========TextBefore== " + newText +"  ===endTextBefore========");
            String textTranslate;
            if (isTrans) {
                textTranslate = (TranslatorUtil
                        .translate(langFrom, langTo, newText));

            } else {
                textTranslate = newText;
            }


            System.out.println("==========TextAfterTrans== " + textTranslate + "  ===endTextAfter=========");


            RichTextString _richText = new XSSFRichTextString(textTranslate);

            _richText.applyFont(cellFont);

            cell.setCellValue(_richText);


        }

    }

    public void processXlsx()   {

        int numberOfSheets = workbook.getNumberOfSheets();

    
        for(int i = 0 ; i < numberOfSheets; i ++) {
            try {
                //processSheet(workbook.getSheetAt(i));
                processSheetMultiThread(workbook.getSheetAt(i));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            workbook.write(new FileOutputStream(outputPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args)  {

        String fileName = "Cơm trưa Falcon Team -  Xứ Đông.xlsx";
        
        String inputPath = "F:\\code java\\ProcessDocxProject\\src\\main\\resources\\data\\" + fileName;
        String outputPath = "F:\\code java\\ProcessDocxProject\\src\\main\\resources\\results\\" + "trans-" + fileName;

        XlsxProcessor xlsxProcessor = new XlsxProcessor(inputPath, outputPath, "vi", "en", true);

        xlsxProcessor.processXlsx();
    }

}
