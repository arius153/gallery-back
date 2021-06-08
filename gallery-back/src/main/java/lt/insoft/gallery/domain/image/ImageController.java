package lt.insoft.gallery.domain.image;

import java.net.http.HttpHeaders;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

@RestController
@RequestMapping(path = "/images")
@RequiredArgsConstructor
@CommonsLog
public class ImageController {

    private final ImageService imageService;


    @PostMapping
    Long uploadImage(@RequestPart MultipartFile image, @RequestPart ImageAddDTO imageAddDTO) {
        return imageService.uploadImage(image, imageAddDTO);
    }
    @GetMapping(value = "/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    Resource downloadImage(@PathVariable Long imageId) {
        byte[] image = imageService.downloadImage(imageId);
        return new ByteArrayResource(image);
    }


    @GetMapping
    public List<ImageResposeDTO> getImages(@RequestParam(required = false) String searchParams, @RequestParam(value = "page", defaultValue = "0") int page) {
        return imageService.getImages(searchParams, page);
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
    public void modifyImage(@PathVariable("imageId") Long imageId, @RequestPart(required = false) MultipartFile image, @RequestPart ImageAddDTO imageModifyDTO) {
        imageService.modifyImage(imageId, imageModifyDTO, image);
    }

    @GetMapping(value = "/count")
    public long getCount(@RequestParam(value = "searchParams", required = false) String searchParams)
    {
        return imageService.getCount(searchParams);
    }
}
