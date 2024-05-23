package org.example.controller;

import org.example.model.Writer;
import org.example.repositoryImpl.WriterRepoImpl;
import org.example.repositoryInterface.WriterRepo;

import java.util.List;

public class WriterController {
    private final WriterRepo writerRepo = new WriterRepoImpl();

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
