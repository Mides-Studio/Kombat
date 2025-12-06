package git.immutabled.kombat.core.repositories;

import com.mongodb.async.client.MongoDatabase;
import git.immutabled.kombat.api.repository.Repository;
import git.immutabled.kombat.api.repository.RepositoryType;
import git.immutabled.kombat.api.repository.adapter.JsonAdapter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.JedisPool;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class MongoRepository<K, T extends JsonAdapter<K, T>> implements Repository<K, T> {

    private final MongoDatabase connection;
    private final String name = "Mongo";
    private final RepositoryType type = RepositoryType.MONGODB;

    @Override
    public List<T> findAll() {
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
