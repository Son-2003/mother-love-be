package com.motherlove.security;

import com.motherlove.models.entities.Customer;
import com.motherlove.models.entities.Staff;
import com.motherlove.models.entities.UserType;
import com.motherlove.repositories.CustomerRepository;
import com.motherlove.repositories.StaffRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Getter
@Setter
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private UserType userType;

    @Override
    public UserDetails loadUserByUsername(String staffAccountOrEmailOrPhone) throws UsernameNotFoundException {
        System.out.println(userType);
        //allow user to log in by username or email
        if(userType==UserType.CUSTOMER){
            Customer customer = customerRepository.findByCustomerAccountOrEmailOrPhone(staffAccountOrEmailOrPhone, staffAccountOrEmailOrPhone, staffAccountOrEmailOrPhone).orElseThrow(
                    ()-> new UsernameNotFoundException("User not found with username or email: "+ staffAccountOrEmailOrPhone)
            );
            SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority(UserType.CUSTOMER.toString());
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(adminAuthority);
            return new User(staffAccountOrEmailOrPhone, customer.getPassword(), authorities);
        }else if(userType==UserType.STAFF){
            Staff staff = staffRepository.findByStaffAccountOrEmailOrPhone(staffAccountOrEmailOrPhone, staffAccountOrEmailOrPhone, staffAccountOrEmailOrPhone).orElseThrow(
                    ()-> new UsernameNotFoundException("User not found with username or email: "+ staffAccountOrEmailOrPhone)
            );
            SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority(UserType.STAFF.toString());
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(adminAuthority);
            return new User(staffAccountOrEmailOrPhone, staff.getPassword(), authorities);
        }
        return null;
    }
}
