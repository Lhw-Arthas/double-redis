package per.lhw.doubleredis.properties;

import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;

/**
 * Created by lhwarthas on 17/8/15.
 */

public class PrimaryJedisConnectionFactory extends JedisConnectionFactory {

    public PrimaryJedisConnectionFactory() {
    }

    public PrimaryJedisConnectionFactory(JedisShardInfo shardInfo) {
        super(shardInfo);
    }

    public PrimaryJedisConnectionFactory(JedisPoolConfig poolConfig) {
        super(poolConfig);
    }

    public PrimaryJedisConnectionFactory(RedisSentinelConfiguration sentinelConfig) {
        super(sentinelConfig);
    }

    public PrimaryJedisConnectionFactory(RedisSentinelConfiguration sentinelConfig, JedisPoolConfig poolConfig) {
        super(sentinelConfig, poolConfig);
    }

    public PrimaryJedisConnectionFactory(RedisClusterConfiguration clusterConfig) {
        super(clusterConfig);
    }

    public PrimaryJedisConnectionFactory(RedisClusterConfiguration clusterConfig, JedisPoolConfig poolConfig) {
        super(clusterConfig, poolConfig);
    }
}
