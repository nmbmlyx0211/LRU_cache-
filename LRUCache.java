
import java.util.*;

public class LRUCache<K,T extends Cacheable<K>> {
    private int size;
    private Persister persister;

    // Stores items for look up
    private final Map<K,Cacheable<K>> itemMap;

    // list in LRU order
    private final List<K> lruList;
    //private CacheKeyIterator<K> it;
    private int accessNo;
    private int faultNo;

    /**
     * To be implemented!!
     * Iterator class only for only keys of cached items; order should be in LRU order, most recently used first
     * @param <K>
     */
    private class CacheKeyIterator<K> implements Iterator<K> {
    	private int pos = 0;
        @Override
        public boolean hasNext() {
        	return pos < lruList.size();
        }

        @Override
        public K next() {
        	if(hasNext()){
        		pos++;
        		return (K) (lruList.get(pos));
        	}
        	else{
        		return null ;
        	}
        }

    }

    /**
     * Constructor
     * @param size initial size of the cache which can change later
     * @param persister persister instance to use for accessing/modifying evicted items
     */
    public LRUCache(int size, Persister<? extends K,? extends T> persister) {
        if (size == 0) {
            throw new IllegalArgumentException("The cache size cannot be 0.");
        }
        this.size = size;
        lruList = new LinkedList<>();
        itemMap = new HashMap<>(size);
        this.persister = persister;
        //it = (CacheKeyIterator<K>)lruList.iterator();
        accessNo = 0;
        faultNo = 0;
    }

    /**
     * Modify the cache size
     * @param newSize
     */
    public void modifySize(int newSize) {
        this.size = newSize;
    }


    /**
     * Get item with the key (need to get item even if evicted)
     * @param key
     * @return
     */
    public T getItem(K key) {
    	if (itemMap.containsKey(key)){
    		lruList.remove(key);
    		lruList.add(0, key);// add the most used to the front
    		return (T) itemMap.get(key);
    	}
    	else if (!(itemMap.containsKey(key)) && persister.getValue(key)!=null){
    		faultNo ++;
    		if (lruList.size() >= size){
    			itemMap.remove(lruList.get(lruList.size()-1));
    			lruList.remove(lruList.size()-1);//remove the least used from the rear		    	 
    		}
			lruList.add(0, key);
			itemMap.put(key, persister.getValue(key));
			return (T) persister.getValue(key);    				    			    		
    	}	    	
    	else
    		return null; 
    }

    /**
     * Add/Modify item with the key
     * @param item item to be put
     */
    public void putItem(T item) {
    	if (persister.getValue(item.getKey()) == null){
			persister.persistValue(item);
    		if (lruList.size() >= size){
		    	itemMap.remove(lruList.get(lruList.size()-1));//remove the least used from both list and map;
		    	lruList.remove(lruList.size()-1); 
    		}
			lruList.add(0, item.getKey());
			itemMap.put(item.getKey(), item);
    	}
    	else
    		accessNo ++;

    }


    /**
     * Remove an item with the key
     * @param key
     * @return item removed or null if it does not exist
     */
    public T removeItem(K key) {
		if(itemMap.get(key) != null){
			T item = (T) itemMap.get(key);
	    	itemMap.remove(key);
	    	lruList.remove(key);
	    	persister.removeValue(key);
	    	return item;	
		}
		else
			return null;
    }

    /**
     * Get cache keys
     * @return
     */
    public Iterator<K> getCacheKeys() {
        return (new CacheKeyIterator());
    }

    /**
     * Get fault rate (proportion of accesses (only for retrievals and modifications) not in cache)
     * @return
     */
    public double getFaultRatePercent() {
        double faultRatePercent = (double)faultNo / accessNo *100;
        return faultRatePercent;
    }

    /**
     * Reset fault rate stats counters
     */
    public void resetFaultRateStats() {
        faultNo = 0;
        accessNo = 0;
    }

    public static void main(String [] args) {
        LRUCache<String,SimpleCacheItem> cache = new LRUCache<>(20, new SimpleFakePersister<>());
        for (int i=0; i < 100; i++) {
            cache.putItem(new SimpleCacheItem("name"+i, (int) (Math.random()*200000)));
            String name = "name" + (int) (Math.random() * i);
            SimpleCacheItem cacheItem = cache.getItem(name);
            if (cacheItem != null) {
                System.out.println("Salary for " + name + "=" + cacheItem.getAnnualSalary());
            }
            cache.putItem(new SimpleCacheItem("name"+ (int) (Math.random()*i), (int) (Math.random()*200000)));
            name = "name" + (int) (Math.random() * i);
            //cache.removeItem(name);
            System.out.println("Fault rate percent=" + cache.getFaultRatePercent());
        }
        for (int i=0; i < 30; i++) {
            String name = "name" + (int) (Math.random()*i);
            cache.removeItem(name);
        }
        cache.resetFaultRateStats();
        cache.modifySize(50);
        for (int i=0; i < 100; i++) {
            cache.putItem(new SimpleCacheItem("name"+i, (int) (Math.random()*200000)));
            String name = "name" + (int) (Math.random()*i);
            SimpleCacheItem cacheItem = cache.getItem(name);
            if (cacheItem != null) {
                System.out.println("Salary for " + name + "=" + cacheItem.getAnnualSalary());
            }
            cache.putItem(new SimpleCacheItem("name"+ (int) (Math.random()*i), (int) (Math.random()*200000)));
            name = "name" + (int) (Math.random()*i);
            cache.removeItem(name);
            System.out.println("Fault rate percent=" + cache.getFaultRatePercent());
        }
    }

}

