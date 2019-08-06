package com.sunny.source.file;

import com.sunny.source.LoadSource;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLoadProperties implements LoadSource {

    private static final Logger log = LoggerFactory.getLogger(AbstractLoadProperties.class);

    @SuppressWarnings("unchecked")
    public Map<String, Object> convertToMap(Properties properties) {
        if (!properties.keys().hasMoreElements()) {
            return null;
        }
        Map<String, Object> res = new HashMap<>();
        // 处理字符串类型的属性
        for (Enumeration<?> e = properties.keys(); e.hasMoreElements(); ) {
            Object k = e.nextElement();
            Object v = properties.get(k);
            if (k instanceof String && v instanceof String) {
                String[] ks = ((String) k).split("\\.");
                Map<String, Object> cres = res;
                String ck = "";
                for (int i = 0; i < ks.length; i++) {
                    ck = ks[i];
                    if (i == ks.length - 1)
                        break;
                    cres.computeIfAbsent(ck, key->new HashMap<>());
                    cres = (Map<String, Object>) cres.get(ck);
                }
                cres.put(ck, v);
                if (log.isDebugEnabled()) {
                    log.debug(ck + ":" + v);
                }
            }
        }
        return res;
    }

}
