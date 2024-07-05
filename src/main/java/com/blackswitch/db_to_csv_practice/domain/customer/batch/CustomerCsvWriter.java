package com.blackswitch.db_to_csv_practice.domain.customer.batch;

import com.blackswitch.db_to_csv_practice.domain.customer.Customer;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class CustomerCsvWriter implements ItemWriter<Customer> {
    private int fileCount = 0;

    @Override
    public void write(Chunk<? extends Customer> items) throws Exception {
        String fileName = "/Users/jhkang/tmp/csv-file-" + (fileCount++) + ".csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Customer item : items) {
                writer.write(item.getId() + "," + item.getBalance());
                writer.newLine();
            }
        }
    }
}
