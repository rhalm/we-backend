package hu.rhalm.wasteless.common.models;

import lombok.Data;

import java.net.URL;
import java.time.Instant;

@Data
public class SignedUrl {
    private final URL url;
    private final Instant expiry;
}
