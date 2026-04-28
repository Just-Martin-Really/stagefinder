package de.dhbwravensburg.webeng.stagefinder.domain.repository;

import de.dhbwravensburg.webeng.stagefinder.domain.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserId(Long userId);

    boolean existsByUserIdAndArtistId(Long userId, Long artistId);
}
