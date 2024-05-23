package org.example.view;


import org.example.controller.LabelController;
import org.example.controller.PostController;
import org.example.model.Label;
import org.example.model.Post;
import org.example.model.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class PostView {

    private final PostController postController = new PostController();

    private final LabelController labelController = new LabelController();

    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        boolean running = true;
        while (running) {
            System.out.println("Выберите опцию для работы с постами:");
            System.out.println("1 - Добавить новый пост");
            System.out.println("2 - Редактировать пост");
            System.out.println("3 - Удалить пост");
            System.out.println("4 - Просмотреть детали поста");
            System.out.println("5 - Список всех постов");
            System.out.println("0 - Вернуться назад");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    createPost();
                    break;
                case 2:
                    updatePost();
                    break;
                case 3:
                    deletePost();
                    break;
                case 4:
                    showPostDetails();
                    break;
                case 5:
                    listAllPosts();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Некорректный ввод. Пожалуйста, попробуйте снова.");
            }
        }
    }

    private void createPost() {
        System.out.println("Введите заголовок поста:");
        String title = scanner.nextLine();

        System.out.println("Введите содержимое поста:");
        String content = scanner.nextLine();

        List<Label> labels = new ArrayList<>();
        while (true){
            Label label = new Label();
            System.out.println("Введите название ярлыка:");
            System.out.println("Для выхода введите wq:");
            String labelFieldOrExit = scanner.nextLine();

            if (labelFieldOrExit.equals("wq")){
                break;
            }
            label.setName(labelFieldOrExit);
            labels.add(labelController.save(label));
        }
        Post post = new Post();
        post.setContent(content);
        post.setTitle(title);
        post.setLabels(labels);

        Post savedPost = postController.save(post);
        System.out.println("Пост был успешно добавлен: " + savedPost);
    }

    private void updatePost() {
        System.out.println("Введите ID поста для редактирования:");
        Long id = Long.parseLong(scanner.nextLine());
        Post post = postController.getById(id);
        if (post == null) {
            System.out.println("Пост с ID " + id + " не найден.");
            return;
        }

        System.out.println("Введите новый заголовок поста:");
        post.setTitle(scanner.nextLine());

        System.out.println("Введите новое содержимое поста:");
        post.setContent(scanner.nextLine());

        List<Label> labels = new ArrayList<>();
        while (true){
            List<Label> postLabels = post.getLabels();
            System.out.println("Введите id для обновления ярлыка:");
            System.out.println("Для выхода введите wq:");
            Long labelFieldOrExit = scanner.nextLong();
            Optional<Label> labelForUpdate = postLabels.stream()
                    .filter(label -> labelFieldOrExit.equals(label.getId()) && label.getStatus() == Status.ACTIVE)
                    .findFirst();

            if (labelFieldOrExit.equals("wq")){
                break;
            }

            if (labelForUpdate.isPresent()) {
                System.out.println("Введите новое имя ярлыка:");
                String name = scanner.nextLine();
                labelForUpdate.get().setName(name);
                labels.add(labelController.update(labelForUpdate.get()));
            }else {
                System.out.println("Такого ярлыка нет у статьи");
            }
        }
        post.setLabels(labels);

        Post updatedPost = postController.update(post);
        System.out.println("Пост обновлен: " + updatedPost);
    }

    private void deletePost() {
        System.out.println("Введите ID поста для удаления:");
        Long id = Long.parseLong(scanner.nextLine());
        postController.delete(id);
        System.out.println("Пост с ID " + id + " был удален.");
    }

    private void showPostDetails() {
        System.out.println("Введите ID поста для просмотра:");
        Long id = Long.parseLong(scanner.nextLine());
        Post post = postController.getById(id);
        if (post != null) {
            System.out.println(post);
        } else {
            System.out.println("Пост с ID " + id + " не найден.");
        }
    }

    private void listAllPosts() {
        System.out.println("Список всех постов:");
        List<Post> posts = postController.getAll();
        if (posts.isEmpty()) {
            System.out.println("Список постов пуст.");
        } else {
            posts.forEach(System.out::println);
        }
    }
}
