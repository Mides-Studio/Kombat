package git.immutabled.kombat.core.repositories;

import com.google.gson.Gson;
import git.immutabled.kombat.api.repository.Repository;
import git.immutabled.kombat.api.repository.RepositoryType;
import git.immutabled.kombat.api.repository.adapter.JsonAdapter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class FlatFileRepository<K, T extends JsonAdapter<K, T>> implements Repository<K, T> {

    //TODO: Add Gson instance for reading and writing JSON files

    private final Gson gson;
    private final String name = "Flat File";
    private final RepositoryType type = RepositoryType.FLATFILE;

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
