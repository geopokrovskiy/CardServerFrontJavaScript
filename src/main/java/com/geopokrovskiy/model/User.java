package com.geopokrovskiy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    @NonNull
    private String login;
    @Column(nullable = false)
    @NonNull
    private String password;
    @Column(nullable = false)
    private LocalDate regDate;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @Cascade(value = org.hibernate.annotations.CascadeType.DELETE)
    private List<Category> categories = new ArrayList<>();

    private String hash;
}
