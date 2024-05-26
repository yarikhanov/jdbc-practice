package org.example.controller;



import org.example.model.Label;
import org.example.repository.jdbc.JdbcLabelRepoImpl;
import org.example.repository.LabelRepo;
import org.example.service.LabelService;
import org.example.service.WriterService;

import java.util.List;

public class LabelController {
    private final LabelService labelService = new LabelService();

    public Label getById(Long id) {
        return labelService.getById(id);
    }

    public List<Label> getAll() {
        return labelService.getAll();
    }

    public Label save(Label label) {
        return labelService.save(label);
    }

    public Label update(Label label) {
        return labelService.save(label);
    }

    public void delete(Long id) {
        labelService.delete(id);
    }
}
