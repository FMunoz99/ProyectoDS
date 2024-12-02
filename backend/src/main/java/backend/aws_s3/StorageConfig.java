package backend.aws_s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    private static final Logger logger = LoggerFactory.getLogger(StorageConfig.class);

    @Value("${AWS_ACCESS_KEY}")
    private String accessKey;

    @Value("${AWS_SECRET_KEY}")
    private String secretKey;

    @Value("${AWS_SESSION_TOKEN}")
    private String sessionToken;

    @Value("${AWS_REGION}")
    private String region;

    @Bean
    public AmazonS3 getAmazonS3Client() {
        validateAWSConfig();

        logger.info("Configurando cliente Amazon S3 con región: {}", region);

        final var credentials = new BasicSessionCredentials(accessKey, secretKey, sessionToken);

        return AmazonS3ClientBuilder
                .standard()
                .withRegion(region) // Usamos directamente la región configurada
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    private void validateAWSConfig() {
        if (accessKey == null || accessKey.isEmpty()) {
            throw new IllegalArgumentException("AWS_ACCESS_KEY no está configurada");
        }
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("AWS_SECRET_KEY no está configurada");
        }
        if (sessionToken == null || sessionToken.isEmpty()) {
            throw new IllegalArgumentException("AWS_SESSION_TOKEN no está configurada");
        }
        if (region == null || region.isEmpty()) {
            throw new IllegalArgumentException("AWS_REGION no está configurada");
        }
    }
}
