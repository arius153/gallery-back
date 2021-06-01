package lt.insoft.gallery.domain.image;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;


    @PostMapping
    Long unloadImage(@RequestPart MultipartFile image, @RequestPart ImageAddDTO imageAddDTO) {
        System.out.println("Do we have a file --> " + image.toString());
        System.out.println("Do we have a DTO? --> " + imageAddDTO.toString());
        return imageService.uploadImage(image, imageAddDTO);
    }
    @GetMapping(value = "/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    Resource downloadImage(@PathVariable Long imageId) {
            byte[] image = imageService.downloadImage(imageId);
            return new ByteArrayResource(image);
    }

    @GetMapping(value = "")
    public List<ImageResposeDTO> getImages(@RequestParam(required = false) String searchParams) {
        return imageService.getImages(searchParams);
    }

    @GetMapping(value = "/data/{imageId}")
    public ImageResposeDTO getFullImageInfo(@PathVariable("imageId") Long imageId) {
        return imageService.getFullImageInfo(imageId);
    }

    @DeleteMapping(value = "/{imageId}")
    public void deleteImage(@PathVariable("imageId") Long imageId) {
        imageService.deleteImage(imageId);
    }

    @PutMapping(value = "/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modifyImage(@PathVariable("imageId") Long imageId, @RequestPart MultipartFile image, @RequestPart ImageModifyDTO imageModifyDTO) {
        imageService.modifyImage(imageId, imageModifyDTO, image);
    }
}
