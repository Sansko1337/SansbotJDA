package ooo.sansk.sansbot.module.fontimage;

import java.awt.image.BufferedImage;

public class TextToImageConversionResult {

    private final boolean successful;
    private final BufferedImage output;

    public TextToImageConversionResult(boolean successful, BufferedImage output) {
        this.successful = successful;
        this.output = output;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public BufferedImage getOutput() {
        return output;
    }
}
