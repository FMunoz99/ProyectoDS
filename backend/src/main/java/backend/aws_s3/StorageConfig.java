package backend.aws_s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Value("${AWS_ACCESS_KEY}")
    private String accessKey;

    @Value("${AWS_SECRET_KEY}")
    private String secretKey;

    @Value("${AWS_SESSION_TOKEN}")
    private String accessSessionToken;

    @Value("${AWS_REGION}")
    private String region;

    @Bean
    public AmazonS3 getAmazonS3Client() {
        final var basicSessionCredentials = new BasicSessionCredentials(accessKey, secretKey, accessSessionToken);

        return AmazonS3ClientBuilder
                .standard()
                .withRegion(String.valueOf(RegionUtils.getRegion(region)))
                .withCredentials(new AWSStaticCredentialsProvider(basicSessionCredentials))
                .build();
    }
}