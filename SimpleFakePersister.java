package assignments;

import java.util.HashMap;
import java.util.Map;


public class SimpleFakePersister<K, T extends Cacheable<K>> implements Persister<K, T> {

    private Map<K,T>  localMap = new HashMap<>();

    @Override
    public void persistValue(T value) {
        localMap.put(value.getKey(), value);
    }

    @Override
    public T getValue(K key) {
        return localMap.get(key);
    }

    @Override
    public T removeValue(K key) {
        return localMap.remove(key);
    }
}
