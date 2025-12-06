package git.immutabled.kombat.api.repository;

import git.immutabled.kombat.api.repository.adapter.JsonAdapter;

import java.util.List;
import java.util.UUID;

public interface Repository<K,T extends JsonAdapter<T, K>> {

    String getName();

    RepositoryType getType();

    List<T> findAll();
    T find(K id);

    void saveAll();
}
