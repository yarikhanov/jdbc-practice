package org.example.controller;

import org.example.model.Writer;
import org.example.repository.jdbc.JdbcWriterRepoImpl;
import org.example.repository.WriterRepo;
import org.example.service.WriterService;

import java.util.List;

public class WriterController {
    private final WriterService writerService = new WriterService();

    public Writer getById(Long id) {
        return writerService.getById(id);
    }

    public List<Writer> getAll() {
        return writerService.getAll();
    }

    public Writer save(Writer writer) {
        return writerService.save(writer);
    }

    public Writer update(Writer writer) {
        return writerService.save(writer);
    }

    public void delete(Long id) {
        writerService.delete(id);
    }
}
