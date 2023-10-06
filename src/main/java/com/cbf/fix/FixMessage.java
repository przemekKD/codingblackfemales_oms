package com.cbf.fix;

import java.util.HashMap;
import java.util.Map;

public class FixMessage {
    private final Map<FixTag, String> tagToVal = new HashMap<>();

    public void parse(String message) {
        String body = message.substring("FIX:".length());
        String[] keyValPairs = body.split("\\|");
        for (String keyValPair : keyValPairs){
            String[] keyVal = keyValPair.split("=");
            tagToVal.put(FixTag.valueOf(keyVal[0]), keyVal[1]);
        }
    }

    public String get(FixTag tag) {
        return tagToVal.get(tag);
    }

    public int getInt(FixTag tag) {
        return Integer.parseInt(get(tag));
    }

    public long getLong(FixTag tag) {
        return Long.parseLong(get(tag));
    }

    public long getDecimalAsLong(FixTag tag) {
        String val = get(tag);
        long result = 0;
        for(int i = 0; i < val.length(); i++){
            char valAt = val.charAt(i);
            if(valAt=='.'){
                continue;
            }else{
                result = result * 10 + (valAt - 48);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "FixMessage{" +
                "tagToVal=" + tagToVal +
                '}';
    }
}
