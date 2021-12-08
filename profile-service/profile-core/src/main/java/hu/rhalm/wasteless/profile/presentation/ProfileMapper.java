package hu.rhalm.wasteless.profile.presentation;

import hu.rhalm.wasteless.common.exception.FailedAssetSignature;
import hu.rhalm.wasteless.profile.ProfileCreateDTO;
import hu.rhalm.wasteless.profile.ProfileDTO;
import hu.rhalm.wasteless.profile.data.ProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ImageMapper.class)
public interface ProfileMapper {
    @Mapping(source = "mine", target = "mine")
    @Mapping(source = "entity.image", target = "image", qualifiedByName = "signImageUrl")
    ProfileDTO entityToDTO(ProfileEntity entity, Boolean mine) throws FailedAssetSignature;

    ProfileEntity entityFromUserIdAndCreateDTO(String userId, ProfileCreateDTO entity);
}
