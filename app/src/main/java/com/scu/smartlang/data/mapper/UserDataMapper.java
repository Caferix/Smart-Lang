package com.scu.smartlang.data.mapper;

import com.scu.smartlang.data.remote.firebase.models.UserDto;
import com.scu.smartlang.domain.model.User;

public class UserDataMapper {
    public UserDto mapToDto(User domainUser){
        if(domainUser == null){
            return null;
        }else {
            return new UserDto(
                    domainUser.getUid(),
                    domainUser.getUserName(),
                    domainUser.getEmail(),
                    domainUser.getXp(),
                    domainUser.getLevel(),
                    domainUser.getProfileImageUrl(),
                    domainUser.getCreatedAt()
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
                    userDto.getXp(),
                    userDto.getLevel(),
                    userDto.getProfileImageUrl(),
                    userDto.getCreatedAt()
            );
        }
    }
}
