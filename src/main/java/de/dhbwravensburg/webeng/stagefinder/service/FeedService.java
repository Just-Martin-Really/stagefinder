package de.dhbwravensburg.webeng.stagefinder.service;

import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.SetlistFmService;
import de.dhbwravensburg.webeng.stagefinder.api.dto.FeedItemDto;
import de.dhbwravensburg.webeng.stagefinder.api.dto.SetlistDto;
import de.dhbwravensburg.webeng.stagefinder.api.exception.NotFoundException;
import de.dhbwravensburg.webeng.stagefinder.domain.entity.Favorite;
import de.dhbwravensburg.webeng.stagefinder.domain.entity.User;
import de.dhbwravensburg.webeng.stagefinder.domain.repository.FavoriteRepository;
import de.dhbwravensburg.webeng.stagefinder.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final SetlistFmService setlistFmService;

    public List<FeedItemDto> getFeed(Long userId, String currentUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        if (!user.getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("Access denied");
        }

        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        return favorites.stream()
                .flatMap(fav -> {
                    try {
                        return setlistFmService.getSetlists(fav.getArtist().getMbid(), 1).stream()
                                .map(s -> toFeedItem(fav.getArtist().getName(), fav.getArtist().getMbid(), s));
                    } catch (Exception e) {
                        log.warn("Failed to fetch setlists for {}: {}", fav.getArtist().getMbid(), e.getMessage());
                        return Stream.empty();
                    }
                })
                .filter(item -> item.getEventDate() != null)
                .sorted(Comparator.comparing(
                        item -> LocalDate.parse(item.getEventDate(), SetlistDto.EVENT_DATE_FORMAT),
                        Comparator.reverseOrder()
                ))
                .toList();
    }

    private FeedItemDto toFeedItem(String artistName, String artistMbid, SetlistDto s) {
        return FeedItemDto.builder()
                .artistName(artistName)
                .artistMbid(artistMbid)
                .eventDate(s.getEventDate())
                .venueName(s.getVenueName())
                .cityName(s.getCityName())
                .countryName(s.getCountryName())
                .songCount(s.getSongs() != null ? s.getSongs().size() : 0)
                .url(s.getUrl())
                .build();
    }
}
