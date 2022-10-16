package image.compressor.utils;

import io.micronaut.http.MediaType;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.server.types.files.SystemFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;

public class CompressorBuilder {

    InputStream input;
    String fileName;
    String extension;
    MediaType mediaType;

    public CompressorBuilder(CompletedFileUpload file) throws Exception {
        this.input = file.getInputStream();
        this.fileName = getFileName();
        this.extension = getExtension(file);
        this.mediaType = geMediaType(file);
    }

    public SystemFile build() throws Exception {
        BufferedImage image = ImageIO.read(this.input);

        File output = File.createTempFile(this.fileName, this.extension);
        output.deleteOnExit();
        OutputStream out = new FileOutputStream(output);

        ImageWriter writer = ImageIO.getImageWritersByFormatName(this.extension).next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(out);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();

        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.5f);
        }

        writer.write(null, new IIOImage(image, null, null), param);

        out.close();
        ios.close();
        writer.dispose();

        return new SystemFile(output, this.mediaType);
    }

    private String getFileName() {
        return "image_" + UUID.randomUUID();
    }

    private String getExtension(CompletedFileUpload file) throws Exception {
        if (file.getContentType().isEmpty()) {
            throw new Exception("MediaType empty");
        }

        return file.getContentType().get().getExtension();
    }

    private MediaType geMediaType(CompletedFileUpload file) throws Exception {
        MediaType mediaType;

        if (file.getContentType().isEmpty()) {
            throw new Exception("MediaType empty");
        }

        switch (file.getContentType().get().getName()) {
            case MediaType.IMAGE_PNG:
                mediaType = MediaType.IMAGE_PNG_TYPE;
                break;
            case MediaType.IMAGE_JPEG:
                mediaType = MediaType.IMAGE_JPEG_TYPE;
                break;
            default:
                throw new Exception("MediaType not supported");
        }

        return mediaType;
    }
}
