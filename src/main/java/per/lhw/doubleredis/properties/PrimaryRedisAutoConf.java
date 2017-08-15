package per.lhw.doubleredis.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

/**
 * Created by lhwarthas on 17/8/15.
 */
@Primary
@Configuration
@EnableConfigurationProperties(PrimaryRedisProperties.class)
public class PrimaryRedisAutoConf {

    @Autowired
    private PrimaryRedisProperties primaryRedisProperties;

    @Primary
    @Bean(name = "primaryRedisConnectionFactory")
    public PrimaryJedisConnectionFactory redisConnectionFactory()
            throws UnknownHostException {
        return applyProperties(createJedisConnectionFactory());
    }

    protected final PrimaryJedisConnectionFactory applyProperties(
            PrimaryJedisConnectionFactory factory) {
        configureConnection(factory);
        if (this.primaryRedisProperties.isSsl()) {
            factory.setUseSsl(true);
        }
        factory.setDatabase(this.primaryRedisProperties.getDatabase());
        if (this.primaryRedisProperties.getTimeout() > 0) {
            factory.setTimeout(this.primaryRedisProperties.getTimeout());
        }
        return factory;
    }

    private void configureConnection(PrimaryJedisConnectionFactory factory) {
        if (StringUtils.hasText(this.primaryRedisProperties.getUrl())) {
            configureConnectionFromUrl(factory);
        } else {
            factory.setHostName(this.primaryRedisProperties.getHost());
            factory.setPort(this.primaryRedisProperties.getPort());
            if (this.primaryRedisProperties.getPassword() != null) {
                factory.setPassword(this.primaryRedisProperties.getPassword());
            }
        }
    }

    private void configureConnectionFromUrl(PrimaryJedisConnectionFactory factory) {
        String url = this.primaryRedisProperties.getUrl();
        if (url.startsWith("rediss://")) {
            factory.setUseSsl(true);
        }
        try {
            URI uri = new URI(url);
            factory.setHostName(uri.getHost());
            factory.setPort(uri.getPort());
            if (uri.getUserInfo() != null) {
                String password = uri.getUserInfo();
                int index = password.lastIndexOf(":");
                if (index >= 0) {
                    password = password.substring(index + 1);
                }
                factory.setPassword(password);
            }
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Malformed 'spring.redis.url' " + url,
                    ex);
        }
    }

    private PrimaryJedisConnectionFactory createJedisConnectionFactory() {
        JedisPoolConfig poolConfig = this.primaryRedisProperties.getPool() != null
                ? jedisPoolConfig() : new JedisPoolConfig();
        return new PrimaryJedisConnectionFactory(poolConfig);
    }


    private JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        RedisProperties.Pool props = this.primaryRedisProperties.getPool();
        config.setMaxTotal(props.getMaxActive());
        config.setMaxIdle(props.getMaxIdle());
        config.setMinIdle(props.getMinIdle());
        config.setMaxWaitMillis(props.getMaxWait());
        return config;
    }

}
