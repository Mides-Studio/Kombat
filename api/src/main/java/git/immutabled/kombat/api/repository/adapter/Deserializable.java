package git.immutabled.kombat.api.repository.adapter;

public interface Deserializable<T, K> {

    public T deserialize(K data);
}
