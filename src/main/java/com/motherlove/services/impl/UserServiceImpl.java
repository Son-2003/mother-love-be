package com.motherlove.services.impl;

import com.motherlove.models.entities.User;
import com.motherlove.models.exception.ResourceNotFoundException;
import com.motherlove.models.payload.dto.UserDto;
import com.motherlove.repositories.UserRepository;
import com.motherlove.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public Page<UserDto> getAllAccount(int pageNo, int pageSize, String sortBy, String sortDir) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        Optional<User> user = Optional.ofNullable(userRepository.findByUserNameOrEmailOrPhone(userName, userName, userName)
                .orElseThrow(() -> new ResourceNotFoundException("User")));

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        List<User> users = null;

        if(user.isPresent()){
            if(user.get().getRole().getRoleName().equalsIgnoreCase("ROLE_ADMIN")){
                users = userRepository.findAllAccountByAdmin(pageable);
            }else {
                users = userRepository.findAllAccountMember(pageable);
            }
        }

        List<UserDto> userDTOs = users.stream().map(this::mapToCustomerDto).toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), userDTOs.size());
        return new PageImpl<>(userDTOs.subList(start, end), pageable, userDTOs.size());
    }

    private UserDto mapToCustomerDto(User customer){
        return mapper.map(customer, UserDto.class);
    }
}
