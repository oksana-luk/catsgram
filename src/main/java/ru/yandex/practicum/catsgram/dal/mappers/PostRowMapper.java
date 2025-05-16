package ru.yandex.practicum.catsgram.dal.mappers;

import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.catsgram.model.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class PostRowMapper implements RowMapper<Post> {

    @Override
    public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setAuthorId(rs.getLong("author_id"));
        post.setDescription(rs.getString("description"));

        Timestamp registrationDate = rs.getTimestamp("post_date");
        post.setPostDate(registrationDate.toInstant());

        return post;
    }
}
