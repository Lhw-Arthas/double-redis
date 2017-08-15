package per.lhw.doubleredis.properties;

import org.springframework.beans.factory.annotation.Autowired;
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
@EnableConfigurationProperties(SecondaryRedisProperties.class)
public class SecondaryRedisAutoConf {

    @Autowired
    private SecondaryRedisProperties secondaryRedisProperties;

    @Bean(name = "secondaryRedisConnectionFactory")
    public SecondaryJedisConnectionFactory redisConnectionFactory()
            throws UnknownHostException {
        return applyProperties(createJedisConnectionFactory());
    }

    protected final SecondaryJedisConnectionFactory applyProperties(
            SecondaryJedisConnectionFactory factory) {
        configureConnection(factory);
        if (this.secondaryRedisProperties.isSsl()) {
            factory.setUseSsl(true);
        }
        factory.setDatabase(this.secondaryRedisProperties.getDatabase());
        if (this.secondaryRedisProperties.getTimeout() > 0) {
            factory.setTimeout(this.secondaryRedisProperties.getTimeout());
        }
        return factory;
    }

    private void configureConnection(SecondaryJedisConnectionFactory factory) {
        if (StringUtils.hasText(this.secondaryRedisProperties.getUrl())) {
            configureConnectionFromUrl(factory);
        } else {
            factory.setHostName(this.secondaryRedisProperties.getHost());
            factory.setPort(this.secondaryRedisProperties.getPort());
            if (this.secondaryRedisProperties.getPassword() != null) {
                factory.setPassword(this.secondaryRedisProperties.getPassword());
            }
        }
    }

    private void configureConnectionFromUrl(SecondaryJedisConnectionFactory factory) {
        String url = this.secondaryRedisProperties.getUrl();
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

    private SecondaryJedisConnectionFactory createJedisConnectionFactory() {
        JedisPoolConfig poolConfig = this.secondaryRedisProperties.getPool() != null
                ? jedisPoolConfig() : new JedisPoolConfig();
        return new SecondaryJedisConnectionFactory(poolConfig);
    }


    private JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        SecondaryRedisProperties.Pool props = this.secondaryRedisProperties.getPool();
        config.setMaxTotal(props.getMaxActive());
        config.setMaxIdle(props.getMaxIdle());
        config.setMinIdle(props.getMinIdle());
        config.setMaxWaitMillis(props.getMaxWait());
        return config;
    }


}
