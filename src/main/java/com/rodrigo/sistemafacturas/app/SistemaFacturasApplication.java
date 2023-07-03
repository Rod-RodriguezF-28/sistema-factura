package com.rodrigo.sistemafacturas.app;

import com.rodrigo.sistemafacturas.app.models.services.IUploadFileService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SistemaFacturasApplication implements CommandLineRunner {

    private final IUploadFileService uploadFileService;

    public SistemaFacturasApplication(IUploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }

    public static void main(String[] args) {
        SpringApplication.run(SistemaFacturasApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        uploadFileService.deleteAll();
        uploadFileService.init();
    }
}
