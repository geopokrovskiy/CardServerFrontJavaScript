package com.geopokrovskiy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "cards")
@NoArgsConstructor
@RequiredArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NonNull
    @Column(nullable = false)
    private String question;
    @NonNull
    @Column(nullable = false)
    private String answer;

    @NonNull
    @Column(nullable = false)
    private LocalDate creationDate;
    @NonNull
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
