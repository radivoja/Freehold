package freehold.marketplace.config;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

import java.time.Duration;
import java.util.Map;

@Configuration
public class CloudWatchMetricsConfig {

    @Value("${cloudwatch.namespace}")
    private String namespace;

    @Value("${cloudwatch.step}")
    private Duration step;

    @Bean
    public CloudWatchMeterRegistry cloudWatchMeterRegistry(CloudWatchAsyncClient cloudWatchAsyncClient) {
        CloudWatchConfig config = new CloudWatchConfig() {
            private final Map<String, String> properties = Map.of(
                    "cloudwatch.namespace", namespace,
                    "cloudwatch.step", step.toString()
            );

            @Override
            public String get(String key) {
                return properties.get(key);
            }
        };

        return new CloudWatchMeterRegistry(config, Clock.SYSTEM, cloudWatchAsyncClient);
    }
}