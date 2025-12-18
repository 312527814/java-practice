package org.example.entity;

import lombok.Data;

import java.util.List;

@Data
public class ActorFilms {
    String actor;
    List<String> movies;
}
