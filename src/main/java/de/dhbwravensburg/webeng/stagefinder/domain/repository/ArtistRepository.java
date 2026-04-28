package de.dhbwravensburg.webeng.stagefinder.domain.repository;

import de.dhbwravensburg.webeng.stagefinder.domain.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Optional<Artist> findByMbid(String mbid);
}
