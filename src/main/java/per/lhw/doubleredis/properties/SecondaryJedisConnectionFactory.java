package per.lhw.doubleredis.properties;

import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;

/**
 * Created by lhwarthas on 17/8/15.
 */

public class SecondaryJedisConnectionFactory extends JedisConnectionFactory {

    public SecondaryJedisConnectionFactory() {
    }

    public SecondaryJedisConnectionFactory(JedisShardInfo shardInfo) {
        super(shardInfo);
    }

    public SecondaryJedisConnectionFactory(JedisPoolConfig poolConfig) {
        super(poolConfig);
    }

    public SecondaryJedisConnectionFactory(RedisSentinelConfiguration sentinelConfig) {
        super(sentinelConfig);
    }

    public SecondaryJedisConnectionFactory(RedisSentinelConfiguration sentinelConfig, JedisPoolConfig poolConfig) {
        super(sentinelConfig, poolConfig);
    }

    public SecondaryJedisConnectionFactory(RedisClusterConfiguration clusterConfig) {
        super(clusterConfig);
    }

    public SecondaryJedisConnectionFactory(RedisClusterConfiguration clusterConfig, JedisPoolConfig poolConfig) {
        super(clusterConfig, poolConfig);
    }
}
