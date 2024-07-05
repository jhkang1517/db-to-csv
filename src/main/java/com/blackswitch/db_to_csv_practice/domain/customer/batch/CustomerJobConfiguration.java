package com.blackswitch.db_to_csv_practice.domain.customer.batch;

import com.blackswitch.db_to_csv_practice.domain.customer.Customer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Slf4j
@Configuration
@AllArgsConstructor
public class CustomerJobConfiguration {

    private final DataSource dataSource;
    private static final int chunkSize = 1000000;

    @Bean
    public JdbcCursorItemReader<Customer> reader() {
        return new JdbcCursorItemReaderBuilder<Customer>()
                .name("customer-reader")
                .dataSource(dataSource)
                .sql("SELECT id, first_name, last_name, balance FROM CUSTOMER LIMIT 1000")
                .fetchSize(chunkSize)
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .build();
    }

    @Bean
    public ItemProcessor<Customer, Customer> processor() {
        return data -> {
            // 데이터 가공 로직
            return data;
        };
    }

    @Bean
    @Qualifier("csvWriter")
    public ItemWriter<Customer> csvWriter() {
        return new CustomerCsvWriter();
    }

    @Bean
    @Qualifier("excelWriter")
    public ItemWriter<Customer> excelWriter() {
        return new CustomerExcelWriter();
    }

    @Bean
    public Job dbToCsvJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("dbToCsvJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .build();
    }

    @Bean
    @JobScope
    public Step step1(@Value("#{jobParameters[version]}") String version,
                      JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      JdbcCursorItemReader<Customer> reader,
                      ItemProcessor<Customer, Customer> processor,
                      ItemWriter<Customer> csvWriter) {
        log.info(">>>>> version = {}", version);
        return new StepBuilder("step1", jobRepository)
                .<Customer, Customer>chunk(chunkSize, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(csvWriter)
                .build();
    }

    @Bean
    public Job dbToExcelJob(JobRepository jobRepository, Step step2) {
        return new JobBuilder("dbToExcelJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step2)
                .build();
    }

    @Bean
    @JobScope
    public Step step2(@Value("#{jobParameters[version]}") String version,
                      JobRepository jobRepository,
                      PlatformTransactionManager transactionManager,
                      JdbcCursorItemReader<Customer> reader,
                      ItemProcessor<Customer, Customer> processor,
                      ItemWriter<Customer> excelWriter) {
        log.info(">>>>> version = {}", version);
        return new StepBuilder("step2", jobRepository)
                .<Customer, Customer>chunk(chunkSize, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(excelWriter)
                .build();
    }
}

