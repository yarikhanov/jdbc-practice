package org.example.service;

import org.example.model.Writer;
import org.example.repository.WriterRepo;
import org.example.repository.jdbc.JdbcWriterRepoImpl;

import java.util.List;

public class WriterService {
    private final WriterRepo writerRepo = new JdbcWriterRepoImpl();

    public Writer getById(Long id) {
        return writerRepo.getById(id);
    }

    public List<Writer> getAll() {
        return writerRepo.getAll();
    }

    public Writer save(Writer writer) {
        return writerRepo.save(writer);
    }

    public Writer update(Writer writer) {
        return writerRepo.save(writer);
    }

    public void delete(Long id) {
        writerRepo.deleteById(id);
    }
}
