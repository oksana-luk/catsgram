package ru.yandex.practicum.catsgram.dal;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.model.Post;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository extends BaseRepository<Post> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM posts";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM posts WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO posts(author_id, description, post_date)" +
            "VALUES (?, ?, ?) returning id";
    private static final String UPDATE_QUERY = "UPDATE posts SET author_id = ?, description = ?, post_date = ? WHERE id = ?";

    public PostRepository(JdbcTemplate jdbc, RowMapper<Post> mapper) {
        super(jdbc, mapper);
    }

    public List<Post> findAll(int size, long from, String sort) {
        StringBuilder query = new StringBuilder(FIND_ALL_QUERY);
        if (from > 0) {
            query.append(" WHERE id >= ");
            query.append(from);
        }
        if (sort.equals("desc")) {
            query.append(" ORDER BY id DESC");
        }
        query.append(" LIMIT");
        query.append(size);
        return findMany(query.toString());
    }

    public Optional<Post> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Post save(Post post) {
        long id = insert(INSERT_QUERY,
                post.getAuthorId(),
                post.getDescription(),
                Timestamp.from(post.getPostDate()));
        post.setId(id);
        return post;
    }

    public Post update(Post post) {
        update(UPDATE_QUERY,
                post.getAuthorId(),
                post.getDescription(),
                Timestamp.from(post.getPostDate()),
                post.getId());
        return post;
    }


}
