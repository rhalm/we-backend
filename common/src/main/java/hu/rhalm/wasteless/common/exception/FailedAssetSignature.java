package hu.rhalm.wasteless.common.exception;

import java.util.UUID;

public class FailedAssetSignature extends RuntimeException {

    private FailedAssetSignature(String message) {
        super(message);
    }

    public static FailedAssetSignature fromAssetId(UUID assetId) {
        return new FailedAssetSignature("Failed to sign URL for asset with assetId " + assetId.toString());
    }
}
