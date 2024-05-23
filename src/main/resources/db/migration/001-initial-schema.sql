CREATE TABLE writers
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    status     VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE posts
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    title     VARCHAR(100),
    content   VARCHAR(1000),
    status    VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    writer_id BIGINT,
    CONSTRAINT fk_posts_on_writers FOREIGN KEY (writer_id) REFERENCES writers (id)
);

CREATE TABLE labels
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    status  VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE',
    post_id BIGINT,
    CONSTRAINT fk_labels_on_posts FOREIGN KEY (post_id) REFERENCES posts (id)
);