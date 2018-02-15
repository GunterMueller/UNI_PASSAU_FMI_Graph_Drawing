package org.graffiti.plugins.scripting.delegate;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import org.graffiti.plugins.scripting.reflect.MemberDesc;

public class DelegateManager {
    private static Map<Class<? extends ReflectiveDelegate>, DelegateEntry> map = new HashMap<Class<? extends ReflectiveDelegate>, DelegateEntry>();

    public static DelegateEntry getEntry(ReflectiveDelegate delegate) {
        return getEntry(delegate.getClass());
    }

    public static DelegateEntry getEntry(
            Class<? extends ReflectiveDelegate> delegateClass) {
        DelegateEntry entry = map.get(delegateClass);
        if (entry == null) {
            entry = new DelegateEntry(delegateClass, true);
            map.put(delegateClass, entry);
        }
        return entry;
    }

    public static SortedMap<String, MemberDesc> getMembers(
            Class<? extends ReflectiveDelegate> delegateClass, Object thisHint) {
        return getEntry(delegateClass).getMembers(thisHint);
    }
}
