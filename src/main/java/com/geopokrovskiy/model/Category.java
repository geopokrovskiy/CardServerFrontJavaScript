package com.geopokrovskiy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "user_id"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    @Column(nullable = false)
    private String name;
    @NonNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ToString.Exclude
    @JsonIgnore
    @NonNull
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
    private List<Card> cardList = new ArrayList<>();

}
