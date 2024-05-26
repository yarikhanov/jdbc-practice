package org.example.service;

import org.example.model.Label;
import org.example.repository.LabelRepo;
import org.example.repository.jdbc.JdbcLabelRepoImpl;

import java.util.List;

public class LabelService {
    private final LabelRepo labelRepo = new JdbcLabelRepoImpl();

    public Label getById(Long id) {
        return labelRepo.getById(id);
    }

    public List<Label> getAll() {
        return labelRepo.getAll();
    }

    public Label save(Label label) {
        return labelRepo.save(label);
    }

    public Label update(Label label) {
        return labelRepo.save(label);
    }

    public void delete(Long id) {
        labelRepo.deleteById(id);
    }
}
