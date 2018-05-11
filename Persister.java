package assignments;

public interface Persister<K, T extends Cacheable<K>> {
    /**
     * persist value to be retrieved later
     * @param value
     */
    void persistValue(T value);

    /**
     * Get value for key from persistent store
     * @param key
     * @return
     */
    T getValue(K key);

    /**
     * Remove value associated with key from persistent store
     * @param key
     * @return
     */
    T removeValue(K key);
}
