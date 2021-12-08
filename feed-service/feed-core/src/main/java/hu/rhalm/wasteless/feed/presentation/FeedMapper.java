package hu.rhalm.wasteless.feed.presentation;

import hu.rhalm.wasteless.feed.AdCreationDTO;
import hu.rhalm.wasteless.feed.AdDTO;
import hu.rhalm.wasteless.feed.data.AdEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = FileMapper.class)
public interface FeedMapper {
    @Mapping(source = "images", target = "images", qualifiedByName = "imageToURL")
    @Mapping(source = "category.name", target = "category")
    AdDTO adToDTO(AdEntity adEntity);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "category", ignore = true)
    AdEntity DTOtoAd(AdCreationDTO adCreationDTO);
}
