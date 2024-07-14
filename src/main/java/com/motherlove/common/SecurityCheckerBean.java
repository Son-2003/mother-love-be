package com.motherlove.common;

import com.motherlove.models.payload.dto.AddressDto;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@NoArgsConstructor
public class SecurityCheckerBean {

    public boolean checkAddressPermission(ResponseEntity<AddressDto> addressResponse) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getAuthorities().stream().findFirst().orElseThrow().toString().equals("ROLE_MEMBER")) {
                return Objects.equals(Objects.requireNonNull(addressResponse.getBody()).getUser().getUserName(), authentication.getName());
            }
            return true;
        }
        return false;
    }

    public boolean checkAddressesPermission(ResponseEntity<List<AddressDto>> addressResponse) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication.getAuthorities().stream().findFirst().orElseThrow().toString().equals("ROLE_MEMBER")) {
                List<AddressDto> list = Objects.requireNonNull(addressResponse.getBody())
                        .stream()
                        .filter(a -> !Objects.equals(a.getUser().getUserName(),
                                authentication.getName()))
                        .toList();

                return list.isEmpty();
            }
            return true;
        }
        return false;
    }

}
