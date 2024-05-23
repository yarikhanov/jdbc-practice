package org.example.view;


import org.example.controller.LabelController;
import org.example.controller.PostController;
import org.example.controller.WriterController;
import org.example.model.Label;
import org.example.model.Post;
import org.example.model.Writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WriterView {
    private final WriterController writerController = new WriterController();

    private final PostController postController = new PostController();

    private final LabelController labelController = new LabelController();
    private final Scanner scanner = new Scanner(System.in);


    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("Выберите действие:");
            System.out.println("1. Добавить писателя");
            System.out.println("2. Редактировать писателя");
            System.out.println("3. Удалить писателя");
            System.out.println("4. Найти писателя по ID");
            System.out.println("5. Показать всех писателей");
            System.out.println("0. Выход");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createWriter();
                    break;
                case 2:
                    updateWriter();
                    break;
                case 3:
                    deleteWriter();
                    break;
                case 4:
                    findWriterById();
                    break;
                case 5:
                    findAllWriters();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Неверный выбор. Пожалуйста, попробуйте снова.");
            }
        }
    }

    private void createWriter() {
        System.out.println("Введите имя писателя:");
        String firstName = scanner.nextLine();

        System.out.println("Введите фамилию писателя:");
        String lastName = scanner.nextLine();
        List<Post> postList = new ArrayList<>();
        while (true) {
            Post post = new Post();
            System.out.println("Для выхода введите wq");
            System.out.println("Введите название статьи писателя: ");
            String postFiledOrExit = scanner.nextLine();
            post.setTitle(postFiledOrExit);
            System.out.println("Введите содержание статьи писателя: ");
            postFiledOrExit = scanner.nextLine();
            post.setContent(postFiledOrExit);
            List<Label> labelList = new ArrayList<>();
            while (true) {
                Label label = new Label();
                System.out.println("Для выхода введите wq");
                System.out.println("Введите название ярлыка статьи: ");
                String labelFieldOrExit = scanner.nextLine();
                label.setName(labelFieldOrExit);
                if (labelFieldOrExit.equals("wq")) {
                    break;
                }
                labelList.add(labelController.save(label));
            }
            if (postFiledOrExit.equals("wq")) {
                break;
            }
            postList.add(postController.save(post));
        }
        Writer writer = new Writer();
        writer.setLastName(lastName);
        writer.setFirstName(firstName);
        writer.setPosts(postList);
        writer = writerController.save(writer);
        System.out.println("Писатель создан успешно: " + writer);
    }

    private void updateWriter() {
        System.out.println("Введите id писателя для обновления:");
        Long id = scanner.nextLong();

        Writer byId = writerController.getById(id);

        if (byId != null) {
            System.out.println("Введите обновленное имя писателя:");
            String firstName = scanner.nextLine();

            System.out.println("Введите обновленное фамилию писателя:");
            String lastName = scanner.nextLine();

            List<Post> postList = new ArrayList<>();
            while (true) {
                System.out.println("Введите id статьи писателя для обновления: ");
                Long postId = scanner.nextLong();
                Post post = postController.getById(postId);
                System.out.println("Для выхода введите wq");
                System.out.println("Введите обновленное название статьи писателя: ");
                String postFiledOrExit = scanner.nextLine();
                post.setTitle(postFiledOrExit);
                System.out.println("Введите обновленное содержание статьи писателя: ");
                postFiledOrExit = scanner.nextLine();
                post.setContent(postFiledOrExit);
                List<Label> labelList = new ArrayList<>();
                while (true) {
                    System.out.println("Введите id метки статьи писателя для обновления: ");
                    Long labelId = scanner.nextLong();
                    Label label = labelController.getById(labelId);
                    System.out.println("Для выхода введите wq");
                    System.out.println("Введите обновленное название ярлыка статьи: ");
                    String labelFieldOrExit = scanner.nextLine();
                    label.setName(labelFieldOrExit);
                    if (labelFieldOrExit.equals("wq")) {
                        break;
                    }
                    labelList.add(labelController.save(label));
                }
                if (postFiledOrExit.equals("wq")) {
                    break;
                }
                postList.add(postController.save(post));
            }
            byId.setLastName(lastName);
            byId.setFirstName(firstName);
            byId.setPosts(postList);
            byId = writerController.update(byId);
            System.out.println("Писатель обновлен успешно: " + byId);
        }
    }

    private void deleteWriter() {
        System.out.println("Введите ID писателя для удаления:");
        Long id = scanner.nextLong();
        scanner.nextLine();
        writerController.delete(id);
        System.out.println("Писатель с ID " + id + " был удален (или помечен как удаленный).");
    }

    private void findWriterById() {
        System.out.println("Введите ID писателя для поиска:");
        Long id = scanner.nextLong();
        scanner.nextLine();
        Writer writer = writerController.getById(id);

        if (writer != null) {
            System.out.println(writer);
        } else {
            System.out.println("Писатель с ID " + id + " не найден.");
        }
    }

    private void findAllWriters() {
        System.out.println("Список всех писателей:");
        List<Writer> writers = writerController.getAll();
        writers.forEach(System.out::println);
    }
}
