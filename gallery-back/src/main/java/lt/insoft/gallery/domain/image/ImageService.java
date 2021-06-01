package lt.insoft.gallery.domain.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;

import org.imgscalr.Scalr;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lt.insoft.gallery.application.ParameterFormatException;
import lt.insoft.gallery.application.ResourceNotFoundException;
import lt.insoft.gallery.domain.tag.TagService;

@Service
@RequiredArgsConstructor()
public class ImageService {

    private final ImageDbRepository imageDbRepository;
    private final TagService tagService;

    public Long uploadImage(MultipartFile image, ImageAddDTO imageAddDto) {
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(imageAddDto.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException ex) {
            throw new ParameterFormatException("Date", imageAddDto.getDate(), "yyyy-MM-dd");
        }

        String fileType = image.getContentType();
        if (!fileType.startsWith("image")) {
            throw new ParameterFormatException("multipartImage", fileType, "image/...");
        }
        ImageEntity imageToSave;
        try {
            // @formatter:off
            imageToSave = ImageEntity
                .builder()
                .content(image.getBytes())
                .name(image.getOriginalFilename())
                .date(parsedDate)
                .description(imageAddDto.getDescription())
                .tags(new HashSet<>())
                .build();
            // @formatter:on
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Could not get bytes from image file");
        }

        Long id = imageDbRepository.save(imageToSave).getId();
        tagService.addTags(id, imageAddDto.getTags());
        return id;
    }

    public byte[] downloadImage(Long imageId) {
        // @formatter:off
        return imageDbRepository
                .findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(ImageEntity.class, "id", imageId.toString()))
                .getContent();
        // @formatter:on
    }

    public ImageResposeDTO getFullImageInfo(Long imageId) {
        // @formatter:off
        ImageEntity image = imageDbRepository
                .findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(ImageEntity.class, "imageId", imageId.toString()));
        return ImageResposeDTO.builder()
                .id(imageId)
                .content(image.getContent())
                .date(image.getDate())
                .description(image.getDescription())
                .tags(tagService.getImageTags(imageId))
                .build();
        // @formatter:on
    }

    public void deleteImage(Long imageId) {
        if (!imageDbRepository.existsById(imageId)) {
            throw new ResourceNotFoundException(ImageEntity.class, "id", imageId.toString());
        }
        imageDbRepository.deleteById(imageId);
    }

    @Transactional
    public void modifyImage(Long imageId, ImageModifyDTO imageModifyDTO, MultipartFile imageFromClient) {
        ImageEntity image = imageDbRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(ImageEntity.class, "imageId", imageId.toString()));
        if (imageModifyDTO.getDate() != null) {
            LocalDate parsedDate;
            try {
                parsedDate = LocalDate.parse(imageModifyDTO.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                image.setDate(parsedDate);
            } catch (DateTimeParseException ex) {
                throw new ParameterFormatException("Date", imageModifyDTO.getDate(), "yyyy-MM-dd");
            }
        }
        if (imageModifyDTO.getDescription() != null && !Objects.equals(imageModifyDTO.getDescription(), image.getDescription())) {
            image.setDescription(imageModifyDTO.getDescription());
        }
        if (imageModifyDTO.getName() != null && !Objects.equals(imageModifyDTO.getName(), image.getName())) {
            image.setName(imageModifyDTO.getName());
        }
        if (imageFromClient != null) {
            String fileType = imageFromClient.getContentType();
            if (!fileType.startsWith("image")) {
                throw new ParameterFormatException("multipartImage", fileType, "image/...");
            }
            try {
                image.setContent(imageFromClient.getBytes());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Could not get bytes from image file");
            }
        }

    }

    public List<ImageResposeDTO> getImages(String searchParams) {
        List<ImageEntity> images = imageDbRepository.findAll();
        if (searchParams != null && !searchParams.isEmpty()) {
            // @formatter:off
            images = imageDbRepository
                    .findAll(Specification.where(ImageSpecification.search(searchParams)));
                    // .stream()
                    // .distinct()
                    // .collect(Collectors.toList());
         //   @formatter:on
        }
        // @formatter:off
        return images.stream()
                .peek(image -> image.setContent(scaleImage(image.getContent())))
                .map(image -> ImageResposeDTO
                        .builder()
                        .id(image.getId())
                        .content(image.getContent())
                        .date(image.getDate())
                        .name(image.getName())
                        .description(image.getDescription())
                        .tags(tagService.getImageTags(image.getId()))
                        .build())
                .collect(Collectors.toList());
        // @formatter:on
    }

    private byte[] scaleImage(byte[] image) {
        BufferedImage imagetoResize;
        try (InputStream is = new ByteArrayInputStream(image); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            imagetoResize = ImageIO.read(is);
            imagetoResize = Scalr.resize(imagetoResize, Scalr.Method.SPEED, 150);
            ImageIO.write(imagetoResize, "jpeg", baos);
            return baos.toByteArray();
        } catch (IOException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Scaler thinks given bytes do not form an image!");
        }

    }


}
