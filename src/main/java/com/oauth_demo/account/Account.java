package com.oauth_demo.account;

import com.oauth_demo.account.constant.Gender;
import com.oauth_demo.account.constant.LoginType;
import com.oauth_demo.account.constant.MemberRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole memberRole;

    private String snsId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;


    @Column(nullable = false)
    private String address;

    @Builder
    private Account(String email, String password, String name, String phoneNumber, Gender gender, String address, LoginType loginType, String snsId){
        this.email = email;
        this.password = password;
        this.name = name;
        this.memberRole = MemberRole.ROLE_USER;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.address = address;
        this.loginType = loginType;
        this.snsId = snsId;
    }

    public void encodePassoword(BCryptPasswordEncoder passwordEncoder){
        password = passwordEncoder.encode(password);
    }

    public boolean isOriginalMember(){
        return this.loginType == LoginType.ORIGINAL;
    }

    public void updateName(String name){
        this.name = name;
    }

}
