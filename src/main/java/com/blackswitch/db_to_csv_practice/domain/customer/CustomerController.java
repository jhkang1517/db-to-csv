package com.blackswitch.db_to_csv_practice.domain.customer;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/customers")
@AllArgsConstructor
public class CustomerController {

    private final JobLauncher jobLauncher;
    private final Job dbToExcelJob;
    private final Job dbToCsvJob;


    @GetMapping("/excel")
    public String getExcel(@RequestParam("version") String version) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("version", version)
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(dbToExcelJob, jobParameters);
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            return "ok";
        } else {
            return "fail";
        }
    }

    @GetMapping("/csv")
    public String getCsv(@RequestParam("version") String version) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("version", version)
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(dbToCsvJob, jobParameters);
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            return "ok";
        } else {
            return "fail";
        }
    }
}
