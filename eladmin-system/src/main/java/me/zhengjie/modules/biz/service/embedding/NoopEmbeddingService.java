package me.zhengjie.modules.biz.service.embedding;

import me.zhengjie.modules.biz.config.ImageSearchEmbeddingProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class NoopEmbeddingService implements EmbeddingService {

    private final ImageSearchEmbeddingProperties properties;

    public NoopEmbeddingService(ImageSearchEmbeddingProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean isEnabled() {
        return properties.isEnabled();
    }

    @Override
    public float[] embed(MultipartFile file) throws IOException {
        return embed(file.getBytes());
    }

    @Override
    public float[] embed(byte[] imageBytes) throws IOException {
        if (!isEnabled()) {
            throw new UnsupportedOperationException("Embedding service is disabled.");
        }
        if ("djl".equalsIgnoreCase(properties.getMode())) {
            throw new UnsupportedOperationException("DJL mode is not configured. Please provide a model file first.");
        }
        return baselineEmbed(imageBytes);
    }

    @Override
    public int dim() {
        return properties.getDim();
    }

    /**
     * A zero-dependency baseline embedding:
     * resize image to 32x16 grayscale => 512 dims, then L2 normalize.
     */
    private float[] baselineEmbed(byte[] imageBytes) throws IOException {
        int dim = properties.getDim();
        int width = 32;
        int height = Math.max(1, dim / width);
        if (width * height != dim) {
            throw new IllegalArgumentException("baseline mode expects dim to be width*height, current dim=" + dim);
        }
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        if (image == null) {
            throw new IllegalArgumentException("Unsupported image format.");
        }
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = scaled.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(image, 0, 0, width, height, null);
        } finally {
            graphics.dispose();
        }

        float[] vector = new float[dim];
        int index = 0;
        double l2 = 0d;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gray = scaled.getRaster().getSample(x, y, 0);
                float value = gray / 255f;
                vector[index++] = value;
                l2 += (value * value);
            }
        }
        l2 = Math.sqrt(l2);
        if (l2 > 1e-9) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] = (float) (vector[i] / l2);
            }
        }
        return vector;
    }
}

