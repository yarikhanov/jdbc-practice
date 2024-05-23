package org.example.view;


import org.example.controller.LabelController;
import org.example.model.Label;

import java.util.List;
import java.util.Scanner;

public class LabelView {

    private final LabelController labelController = new LabelController();
    private final Scanner scanner = new Scanner(System.in);


    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("Выберите опцию для работы с метками:");
            System.out.println("1 - Создать новую метку");
            System.out.println("2 - Редактировать метку");
            System.out.println("3 - Удалить метку");
            System.out.println("4 - Просмотреть детали метки");
            System.out.println("5 - Показать все метки");
            System.out.println("0 - Выход");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createLabel();
                    break;
                case 2:
                    updateLabel();
                    break;
                case 3:
                    deleteLabel();
                    break;
                case 4:
                    showLabelDetails();
                    break;
                case 5:
                    listAllLabels();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Некорректный ввод. Пожалуйста, попробуйте ещё раз.");
            }
        }
    }

    private void createLabel() {
        System.out.println("Введите имя метки:");
        String name = scanner.nextLine();

        Label label = new Label();
        label.setName(name);
        Label savedLabel = labelController.save(label);

        System.out.println("Новая метка создана: " + savedLabel);
    }

    private void updateLabel() {
        System.out.println("Введите ID метки для редактирования:");
        Long id = scanner.nextLong();

        Label label = labelController.getById(id);

        if (label == null) {
            System.out.println("Метка с указанным ID не найдена.");
            return;
        }

        System.out.println("Текущее имя метки: " + label.getName());
        System.out.println("Введите новое имя метки:");
        String name = scanner.nextLine();
        label.setName(name);

        Label updatedLabel = labelController.update(label);

        System.out.println("Метка обновлена: " + updatedLabel);
    }

    private void deleteLabel() {
        System.out.println("Введите ID метки для удаления:");
        Long id = scanner.nextLong();

        labelController.delete(id);
        System.out.println("Метка с ID " + id + " удалена.");
    }

    private void showLabelDetails() {
        System.out.println("Введите ID метки для просмотра:");
        Long id = scanner.nextLong();

        Label label = labelController.getById(id);

        if (label != null) {
            System.out.println("Детали метки: " + label);
        } else {
            System.out.println("Метка с ID " + id + " не найдена.");
        }
    }

    private void listAllLabels() {
        List<Label> labels = labelController.getAll();

        if (labels.isEmpty()) {
            System.out.println("Список меток пуст.");
        } else {
            labels.forEach(System.out::println);
        }
    }
}
