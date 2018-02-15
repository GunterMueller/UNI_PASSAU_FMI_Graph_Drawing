package org.graffiti.plugins.scripting;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Stack;

import org.graffiti.plugins.scripting.delegate.DelegateManager;
import org.graffiti.plugins.scripting.delegate.ReflectiveDelegate;
import org.graffiti.plugins.scripting.reflect.EvaluatingDesc;
import org.graffiti.plugins.scripting.reflect.FieldDesc;
import org.graffiti.plugins.scripting.reflect.FunctionDesc;
import org.graffiti.plugins.scripting.reflect.HelpFormatter;
import org.graffiti.plugins.scripting.reflect.MemberDesc;
import org.graffiti.util.Pair;

/**
 * Provides code completion for the console. The code completion feature is
 * currently experimental and only fragmentary implemented.
 * 
 * @author Andreas Glei&szlig;ner
 */
public class CodeCompletion {
    public static class Entry {
        private String caption;
        private String insertion;
        private String summary;
        private String description;
        private boolean isFunction;

        private Entry(Map.Entry<String, MemberDesc> entry, int length) {
            caption = entry.getKey();
            MemberDesc info = entry.getValue();
            insertion = caption.substring(length);
            if (info instanceof FunctionDesc) {
                insertion += '(';
                isFunction = true;
            } else {
                isFunction = false;
            }
            summary = info.getSummary();
            description = info.getDescription(new HelpFormatter());
        }

        public String getCaption() {
            return caption;
        }

        public String getInsertion() {
            return insertion;
        }

        public String getSummary() {
            return summary;
        }

        public String getDescription() {
            return description;
        }

        public boolean isFunction() {
            return isFunction;
        }
    }

    public static List<Entry> getCompletions(Scope scope, String prefix) {
        prefix = prefix.trim();
        LinkedList<Pair<String, Boolean>> list = split(prefix);
        if (list == null || list.isEmpty())
            return Collections.emptyList();
        prefix = list.removeLast().getFirst();
        SortedMap<String, MemberDesc> members = scope.getMembers();
        Iterator<Pair<String, Boolean>> iter = list.iterator();
        while (iter.hasNext()) {
            Pair<String, Boolean> pair = iter.next();
            String name = pair.getFirst();
            boolean usedBrackets = pair.getSecond();
            MemberDesc info = members.get(name);
            if (info == null || usedBrackets && !(info instanceof FunctionDesc))
                return Collections.emptyList();
            if (usedBrackets || (info instanceof FieldDesc)) {
                Class<?> returnType = ((EvaluatingDesc) info).getReturnType();
                if (!ReflectiveDelegate.class.isAssignableFrom(returnType))
                    return Collections.emptyList();
                members = DelegateManager.getMembers(returnType
                        .asSubclass(ReflectiveDelegate.class), null);
            } else {
                members = info.getMembers();
            }
        }

        int length = prefix.length();
        LinkedList<Entry> result = new LinkedList<Entry>();
        for (Map.Entry<String, MemberDesc> entry : members.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                result.add(new Entry(entry, length));
            }
        }
        return result;
    }

    private static LinkedList<Pair<String, Boolean>> split(String string) {

        // . foo () .
        // 22111000000
        int state = 1;

        LinkedList<Pair<String, Boolean>> list = new LinkedList<Pair<String, Boolean>>();
        Stack<Character> brackets = new Stack<Character>();
        StringBuffer buffer = new StringBuffer();
        boolean usedBrackets = false;
        int len = string.length();
        for (int i = len - 1; i >= 0; i--) {
            char ch = string.charAt(i);
            if (ch == ')' || ch == ']' || ch == '}') {
                if (state != 0)
                    return list;
                brackets.push(ch);
                usedBrackets = true;
                continue;
            } else if (ch == '(' || ch == '[' || ch == '{') {
                if (brackets.isEmpty())
                    return list;
                char chr = brackets.pop();
                if (ch == '(' && chr != ')' || ch == '[' && chr != ']'
                        || ch == '{' && chr != '}')
                    return null;
                continue;
            }
            if (!brackets.isEmpty()) {
                continue;
            }
            if (state == 0) {
                if (Character.isWhitespace(ch)) {
                    continue;
                } else if (Character.isJavaIdentifierPart(ch)) {
                    buffer.append(ch);
                    state = 1;
                } else
                    return list;
            } else if (state == 1) {
                if (Character.isWhitespace(ch)) {
                    list.addFirst(Pair.create(buffer.reverse().toString(),
                            usedBrackets));
                    usedBrackets = false;
                    state = 2;
                } else if (Character.isJavaIdentifierPart(ch)) {
                    buffer.append(ch);
                } else if (ch == '.') {
                    list.addFirst(Pair.create(buffer.reverse().toString(),
                            usedBrackets));
                    usedBrackets = false;
                    buffer = new StringBuffer();
                    state = 0;
                } else {
                    list.addFirst(Pair.create(buffer.reverse().toString(),
                            usedBrackets));
                    usedBrackets = false;
                    return list;
                }
            } else // state == 2
            {
                if (Character.isWhitespace(ch)) {
                    continue;
                } else if (ch == '.') {
                    buffer = new StringBuffer();
                    state = 0;
                } else
                    return list;
            }
        }
        if (state == 1) {
            list.addFirst(Pair
                    .create(buffer.reverse().toString(), usedBrackets));
            usedBrackets = false;
        }
        return list;
    }
}
