package backend.aws_s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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

    public String generatePresignedUrl(String objectUrl) {
        // Extraer la clave del objeto de la URL proporcionada
        String objectKey = objectUrl.replace("https://ds-proy-bucket.s3.amazonaws.com/", "");
        System.out.println("Generated Object Key (original): " + objectKey);

        // Reemplazar %40 por @
        String processedKey = objectKey.replace("%40", "@")
                .replace("%20", " ");
        System.out.println("Processed Object Key: " + processedKey);

        System.out.println("Processed Key: " + processedKey);

        // Verificar si el objeto existe en S3
        if (!s3Client.doesObjectExist(bucketName, processedKey)) {
            System.out.println("Imagen no encontrada en S3");
            return null;
        }

        // Establecer la fecha de expiración para la URL pre-firmada
        Date expiration = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 2);

        // Crear la solicitud para generar la URL pre-firmada
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, processedKey)
                        .withExpiration(expiration);

        // Generar la URL pre-firmada
        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }



}
