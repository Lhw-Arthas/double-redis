package per.lhw.doubleredis.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by lhwarthas on 17/8/15.
 */
@Service
public class SecondaryRedisService {

    @Autowired
    @Qualifier("secondaryStringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;

    private static final Logger log = LoggerFactory.getLogger(SecondaryRedisService.class);

    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Deprecated
    public Map<String, String> getPrefix(String prefix) {
        Map<String, String> onlineMap = new HashMap<>();
        Set<String> stringSet = stringRedisTemplate.keys(prefix + "*");
        log.info("keys执行完毕，大小为{}", stringSet.size());
        stringSet.forEach(key -> onlineMap.put(key, stringRedisTemplate.opsForValue().get(key)));
        log.info("value读取完毕，已组合成Map");
        return onlineMap;
    }

    public Map<String, String> multiGet(String prefix) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("multiGet");
        log.info("开始执行keys");
        Set<String> stringSet = stringRedisTemplate.keys(prefix + "*");
        log.info("keys执行完毕，大小为{}", stringSet.size());
        int size = stringSet.size();
        Map<String, String> kvMap = new HashMap<>();
        for (int i = 0; i < size; ) {
            List<String> strings = stringSet.stream().skip(i).limit(200000).collect(Collectors.toList());
            log.info("开始取redis数据，起始位置：{}，key的数量：{}", i, strings.size());
            List<String> values = stringRedisTemplate.opsForValue().multiGet(strings);
            for (int j = 0; j < strings.size(); j++) {
                kvMap.put(strings.get(j), values.get(j));
            }
            i += 200000;
        }
        stopWatch.stop();
        log.info("value读取完毕，已组合成Map");
        log.info("{}", stopWatch.prettyPrint());
        return kvMap;
    }

    public void multiSet(Map<String, String> map) {
        int size = map.size();
        Map<String, String> tempMap = new HashMap<>();
        int count = 1;
        List<Map.Entry<String, String>> a;
        for (int i = 0; i < size; ) {
            a = map.entrySet().stream().skip(i).limit(100000).collect(Collectors.toList());
            a.forEach(stringStringEntry -> {
                if (!StringUtils.isEmpty(stringStringEntry.getKey()) && !StringUtils.isEmpty(stringStringEntry.getValue())) {
                    tempMap.put(stringStringEntry.getKey(), stringStringEntry.getValue());
                } else {
                    log.warn("key or value null, Key is {} , Value is {} ", stringStringEntry.getKey(), stringStringEntry.getValue());
                }
            });
            stringRedisTemplate.opsForValue().multiSet(tempMap);
            log.info("写入第{}次100000条数据成功", count + 1);
            tempMap.clear();
            i += 100000;
            ++count;
        }
    }

}
