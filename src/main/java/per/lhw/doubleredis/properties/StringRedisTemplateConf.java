package per.lhw.doubleredis.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Created by lhwarthas on 17/8/15.
 */
@Configuration
public class StringRedisTemplateConf {

    @Qualifier("primaryRedisConnectionFactory")
    @Autowired
    private PrimaryJedisConnectionFactory primaryJedisConnectionFactory;

    @Qualifier("secondaryRedisConnectionFactory")
    @Autowired
    private SecondaryJedisConnectionFactory secondaryJedisConnectionFactory;

    @Bean(name = "primaryStringRedisTemplate")
    public StringRedisTemplate primaryStringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(primaryJedisConnectionFactory);
        return stringRedisTemplate;
    }

    @Bean(name = "secondaryStringRedisTemplate")
    public StringRedisTemplate secondaryStringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(secondaryJedisConnectionFactory);
        return stringRedisTemplate;
    }

}
