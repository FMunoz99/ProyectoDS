package backend.aws_s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class StorageService {

    private final AmazonS3 s3Client;

    @Value("${AWS_BUCKET_NAME}")
    private String bucketName;

    public StorageService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file, String keyPrefix) throws IOException {
        String key = keyPrefix + "/" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        s3Client.putObject(bucketName, key, file.getInputStream(), metadata);

        return s3Client.getUrl(bucketName, key).toString();
    }
}
