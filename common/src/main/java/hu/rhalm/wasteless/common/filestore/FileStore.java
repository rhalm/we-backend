package hu.rhalm.wasteless.common.filestore;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import hu.rhalm.wasteless.common.models.SignedUrl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
public class FileStore {

    public FileStore(String bucket) throws IOException {
        InputStream serviceAccount = new ClassPathResource("/config/credentials.json").getInputStream();
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .setStorageBucket(bucket)
                .build();
        FirebaseApp.initializeApp(options);

        bucketName = StorageClient.getInstance()
                .bucket()
                .getName();

        storage = StorageOptions.newBuilder()
                .setCredentials(googleCredentials)
                .build()
                .getService();
    }

    private final String bucketName;
    private final Storage storage;

    public UUID upload(MultipartFile file, Folder folder) throws IOException {
        return upload(file.getInputStream(), folder);
    }

    public UUID upload(InputStream stream, Folder folder) throws IOException {
        UUID fileId = UUID.randomUUID();
        BlobId blobId = BlobId.of(bucketName, folder.withPath(fileId));
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("media")
                .setCacheControl("public, max-age=31536000")
                .build();
        storage.createFrom(blobInfo, stream);
        return fileId;
    }

    public Optional<SignedUrl> signedUrl(UUID fileId, Folder folder) {
        BlobId blobId = BlobId.of(bucketName, folder.withPath(fileId));
        return Optional.ofNullable(storage.get(blobId))
                .map(blob ->
                        new SignedUrl(
                                blob.signUrl(24, TimeUnit.HOURS),
                                Instant.now().plus(23, ChronoUnit.HOURS)
                        )
                );
    }
}
