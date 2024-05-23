package org.example.controller;



import org.example.model.Label;
import org.example.repositoryImpl.LabelRepoImpl;
import org.example.repositoryInterface.LabelRepo;

import java.util.List;

public class LabelController {
    private final LabelRepo labelRepo = new LabelRepoImpl();

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
