package image.compressor.controllers;

import image.compressor.utils.CompressorBuilder;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.http.server.types.files.SystemFile;

@Controller("/compress")
public class CompressController {

    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<SystemFile> compress(CompletedFileUpload file) throws Exception {
        SystemFile compressedFile = new CompressorBuilder(file).build();

        return HttpResponse.ok(compressedFile);
    }
}
