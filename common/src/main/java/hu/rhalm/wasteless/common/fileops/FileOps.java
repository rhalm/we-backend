package hu.rhalm.wasteless.common.fileops;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public class FileOps {
    public static Optional<String> fileName(MultipartFile file) {
        return Optional
                .of(file.getName())
                .flatMap(str -> str.equals("") ? Optional.empty() : Optional.of(str))
                .or(() -> Optional.ofNullable(file.getOriginalFilename()).map(FilenameUtils::getName));
    }

    public static String extension(MultipartFile file) {
        return FilenameUtils.getExtension(file.getOriginalFilename());
    }
}
