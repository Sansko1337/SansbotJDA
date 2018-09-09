package ooo.sansk.sansbot.module.image;

public class ImageResult {

    private final ImageResultReason imageResultReason;
    private final String imageType;
    private final byte[] imageData;

    public ImageResult(ImageResultReason imageResultReason, String imageType, byte[] imageData) {
        this.imageResultReason = imageResultReason;
        this.imageType = imageType;
        this.imageData = imageData;
    }

    public ImageResultReason getImageResultReason() {
        return imageResultReason;
    }

    public String getImageType() {
        return imageType;
    }

    public byte[] getImageData() {
        return imageData;
    }
}
