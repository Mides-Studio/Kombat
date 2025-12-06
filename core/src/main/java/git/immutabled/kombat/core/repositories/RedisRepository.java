package git.immutabled.kombat.core.repositories;

import git.immutabled.kombat.api.repository.Repository;
import git.immutabled.kombat.api.repository.RepositoryType;
import git.immutabled.kombat.api.repository.adapter.JsonAdapter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class RedisRepository<K, T extends JsonAdapter<K, T>> implements Repository<K, T> {

    private final JedisPool connection;
    private final String name = "Redis";
    private final RepositoryType type = RepositoryType.REDIS;


    @Override
    public List<T> findAll() {
//        return this.connection.get

        return List.of();
    }

    @Override
    public T find(K id) {
        return null;
    }

    @Override
    public void saveAll() {

    }
}
