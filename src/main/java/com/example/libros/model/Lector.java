package com.example.libros.model;
import ch.qos.logback.core.model.NamedModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonTypeId;

import javax.lang.model.element.Name;

@Entity
@Table(name = "Lectores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private boolean activo;
    private boolean membresiaPremium;
}
