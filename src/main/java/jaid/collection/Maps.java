package jaid.collection;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Maps {

    public static <K, V extends Comparable<? super V>> Map<K, V> sortDescByValue(Map<K, V> map) {
        final List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        final Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
}
