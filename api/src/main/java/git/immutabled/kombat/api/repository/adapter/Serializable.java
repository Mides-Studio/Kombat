package git.immutabled.kombat.api.repository.adapter;

public interface Serializable<T, K> {

    K serialize(T object);
}
