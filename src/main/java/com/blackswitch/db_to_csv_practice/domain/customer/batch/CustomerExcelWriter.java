package com.blackswitch.db_to_csv_practice.domain.customer.batch;

import com.blackswitch.db_to_csv_practice.domain.customer.Customer;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.io.*;

public class CustomerExcelWriter implements ItemWriter<Customer> {

    private SXSSFWorkbook workbook;
    private SXSSFSheet sheet;
    private int fileCount = 0;
    private int rowCount = 0;

    public CustomerExcelWriter() {
        this.workbook = new SXSSFWorkbook();
        this.sheet = workbook.createSheet("Data");
    }

    @Override
    public void write(Chunk<? extends Customer> items) throws IOException {
        String fileName = "/Users/jhkang/tmp/excel-file-" + (fileCount++) + ".xlsx";
        try (FileOutputStream out = new FileOutputStream(fileName, true)) {
            for (Customer item : items) {
                Row row = sheet.createRow(rowCount++);
                row.createCell(0).setCellValue(item.getFirstName());
                row.createCell(1).setCellValue(item.getLastName());
                row.createCell(2).setCellValue(item.getBalance());
                row.createCell(3).setCellValue(item.getId());
            }
            workbook.write(out);
            workbook.dispose(); // 메모리 해제
            this.workbook = new SXSSFWorkbook();
            this.sheet = workbook.createSheet("Data");
            this.rowCount = 0;
        }
    }

//        for (Customer item : items) {
//            Row row = sheet.createRow(rowCount++);
//            row.createCell(0).setCellValue(item.getFirstName());
//            row.createCell(1).setCellValue(item.getLastName());
//            row.createCell(2).setCellValue(item.getBalance());
//            row.createCell(3).setCellValue(item.getId());
//        }
//
//        if (rowCount >= 1000) { // 예시로 20000 rows마다 저장
//            saveAndResetWorkbook();
//        }
//    }

//    private void saveAndResetWorkbook() throws IOException {
//        File outputFile = new File("/Users/jhkang/tmp/excel-file-" + (fileIndex++) + ".xlsx");
//        try (FileOutputStream out = new FileOutputStream(outputFile, true)) {
//            workbook.write(out);
//        }
//
//    }
}
