package com.scu.smartlang.data.mapper;

import com.scu.smartlang.data.remote.firebase.models.UserDto;
import com.scu.smartlang.domain.model.User;

public class UserDataMapper {
    // NOT: UserStats dönüşüm mantığı henüz mevcut olmadığı için,
    // kurucu metotlar (constructors) tarafından beklenen statik alanlara
    // (UserStats ve UserStatsDto) şimdilik null değeri geçilmiştir.
    // İleride buraya bir UserStatsMapper bağımlılığı eklenmelidir.

    public UserDto mapToDto(User domainUser){
        if(domainUser == null){
            return null;
        }else {
            return new UserDto(
                    domainUser.getUid(),
                    domainUser.getUserName(),
                    domainUser.getEmail(),
                    domainUser.isEmailVerified(), // 4. parametre
                    domainUser.getXp(),
                    domainUser.getLevel(),
                    domainUser.getProfileImageUrl(),
                    domainUser.getCreatedAt(),
                    domainUser.getStats() != null ? null : null
            );
        }
    }

    public User mapToDomain(UserDto userDto){
        if(userDto == null){
            return null;
        }else{
            return new User(
                    userDto.getUid(),
                    userDto.getUserName(),
                    userDto.getEmail(),
                    userDto.isEmailVerified(),
                    userDto.getXp(),
                    userDto.getLevel(),
                    userDto.getProfileImageUrl(),
                    userDto.getCreatedAt(),
                    userDto.getStats() != null ? null : null
            );
        }
    }
}