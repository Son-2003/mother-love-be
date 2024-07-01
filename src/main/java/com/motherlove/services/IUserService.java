package com.motherlove.services;

import com.motherlove.models.payload.dto.UserDto;
import org.springframework.data.domain.Page;

public interface IUserService {
    Page<UserDto> getAllAccount(int pageNo, int pageSize, String sortBy, String sortDir);
}
