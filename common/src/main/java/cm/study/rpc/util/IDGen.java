package cm.study.rpc.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class IDGen {

    private static ConcurrentMap<String, AtomicLong> seqGen = new ConcurrentHashMap<>();

    public static AtomicLong getCursor(String namespace) {
        AtomicLong cursor = seqGen.get(namespace);
        if (cursor != null) {
            return cursor;
        } else {
            AtomicLong newCursor = new AtomicLong(0);
            AtomicLong oldCursor = seqGen.putIfAbsent(namespace, newCursor);
            if (oldCursor != null) {
                return oldCursor;
            } else {
                return newCursor;
            }
        }
    }

    public static long next(String namespace) {
        AtomicLong cursor = getCursor(namespace);
        return cursor.incrementAndGet();
    }
}
