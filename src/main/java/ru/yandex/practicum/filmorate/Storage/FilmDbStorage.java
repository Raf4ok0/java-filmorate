package ru.yandex.practicum.filmorate.Storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@Slf4j
@Qualifier
@AllArgsConstructor
@Primary
@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        final String sqlQuery = "INSERT INTO films (name, description, release_date, duration) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder generatedId = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setLong(4, film.getDuration());
            return statement;
        }, generatedId);
        film.setId(Objects.requireNonNull(generatedId.getKey()).longValue());

        insertLikes(film.getLikes(), film.getId());
        insertMpa(film.getMpa(), film.getId());
        insertGenres(film.getGenres(), film.getId());

        return film;
    }

    @Override
    public List<Film> getAllFilmsList() {
        final String sqlQuery = "select * from films";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    private void insertMpa(Mpa mpa, long filmId) {
        jdbcTemplate.update("DELETE FROM films_mpa WHERE films_Id=?", filmId);
        if (mpa == null) {
            return;
        }
        jdbcTemplate.update("INSERT INTO films_mpa (films_Id,rating_mpa_id) VALUES (?,?)",
                filmId, mpa.getId()
        );
    }

    private void insertGenres(Set<Genre> genres, long filmId) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_Id=?", filmId);
        if (genres == null || genres.size() == 0) {
            return;
        }
        List<Genre> list = List.copyOf(genres);
        jdbcTemplate.batchUpdate("INSERT INTO film_genre (film_Id,genres_Id) VALUES (?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, filmId);
                        ps.setLong(2, list.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return genres.size();
                    }
                }
        );
    }

    private void insertLikes(Set<Long> likes, long filmId) {
        jdbcTemplate.update("DELETE FROM likes WHERE films_Id=?", filmId);
        if (likes == null || likes.size() == 0) {
            return;
        }
        List<Long> list = List.copyOf(likes);
        jdbcTemplate.batchUpdate("INSERT INTO likes (films_Id,users_id) VALUES (?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, filmId);
                        ps.setLong(2, list.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return likes.size();
                    }
                }
        );
    }

    @Override
    public Film updateFilm(Film film) {
        final String updateQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?" +
                "WHERE film_id = ?";
        int cnt = jdbcTemplate.update(updateQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());
        if (cnt == 0) {
            log.info("Фильм не найден", film.getId());
            throw new NotFoundException("Фильм не найден");
        }

        insertLikes(film.getLikes(), film.getId());
        insertMpa(film.getMpa(), film.getId());
        insertGenres(film.getGenres(), film.getId());

        film.setLikes(findLikes(film.getId()));
        film.setMpa(findMpa(film.getId()));
        film.setGenres(findGenres(film.getId()));
        log.info("Фильм обновлен ", film.getId());
        return film;
    }

    @Override
    public Film getById(long id) {
        try {
            final String sqlQuery = "SELECT * FROM films WHERE film_id = ?";
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
            return film;
        } catch (DataAccessException e) {
            log.info("Фильм не найден ", id);
            throw new NotFoundException("Фильм не найден");
        }
    }

    @Override
    public Film deleteFilm(long id) {
        Film film = getById(id);
        final String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
        return film;
    }

    public Film addLike(long filmId, long userId) {
        checkIfExist(filmId, userId);
        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "SELECT * FROM likes WHERE films_id=? AND users_id=?",
                filmId, userId
        );
        if (rs.next()) {
            log.info("Лайк от пользователя " + userId + " фильму " + filmId + " уже есть");
        } else {
            final String sqlQuery = "INSERT INTO likes (films_id, users_id) values (?,?)";
            jdbcTemplate.update(sqlQuery, filmId, userId);
            log.info("Лайк от пользователя " + userId + " поставлен фильму " + filmId);
        }
        return getById(filmId);
    }

    public Film removeLike(long filmId, long userId) {
        final String sqlQuery = "DELETE FROM likes " +
                "WHERE films_id = ? AND users_id = ?";
        int cnt = jdbcTemplate.update(sqlQuery, filmId, userId);
        if (cnt > 0) {
            log.info("Лайк от пользователя " + userId + " удален у фильма " + filmId);
        } else {
            log.info("Лайк от пользователя " + userId + " не найден у фильма " + filmId);
        }
        return getById(filmId);
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        final long id = resultSet.getLong("film_id");
        final String name = resultSet.getString("name");
        final String description = resultSet.getString("description");
        final LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        int duration = resultSet.getInt("duration");
        return new Film(id, name, description, releaseDate, duration,
                findLikes(id), findMpa(id), findGenres(id));
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        final long id = rs.getLong("genre_id");
        final String name = rs.getString("name");
        return new Genre(id, name);
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        final long id = rs.getLong("mpa_id");
        final String name = rs.getString("name");
        return new Mpa(id, name);
    }

    private Mpa findMpa(long filmId) {
        final String mpaSqlQuery = "SELECT mpa_id, name " +
                "FROM mpa " +
                "LEFT JOIN films_mpa MF ON mpa.mpa_id = mf.rating_mpa_id " +
                "WHERE films_id = ?";
        return jdbcTemplate.queryForObject(mpaSqlQuery, this::makeMpa, filmId);
    }

    private Set<Genre> findGenres(long filmId) {
        final String genresSqlQuery = "SELECT genre.genre_id, name " +
                "FROM genre " +
                "LEFT JOIN film_genre fg on genre.genre_id = fg.genres_id " +
                "WHERE film_id = ?";
        return new HashSet(jdbcTemplate.query(genresSqlQuery, this::makeGenre, filmId));
    }

    private Set<Long> findLikes(long filmId) {
        return new HashSet(jdbcTemplate.queryForList("SELECT users_id FROM likes WHERE films_id=?",
                new Object[]{filmId}, Long.class));
    }

    private void checkIfExist(long filmId, long userId) {
        final String checkUserFilmQuery = "SELECT 1 where exists(select * FROM users WHERE user_id = ?) " +
                "and exists(select * FROM films WHERE film_id = ?)";
        SqlRowSet filmUserRows = jdbcTemplate.queryForRowSet(checkUserFilmQuery, userId, filmId);
        if (!filmUserRows.next()) {
            log.info("Фильм или пользователь не найден.", userId, filmId);
            throw new NotFoundException("Фильм или пользователь не найден");
        }
    }
}
