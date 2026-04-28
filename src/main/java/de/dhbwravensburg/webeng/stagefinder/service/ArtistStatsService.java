package de.dhbwravensburg.webeng.stagefinder.service;

import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.SetlistFmService;
import de.dhbwravensburg.webeng.stagefinder.api.dto.ArtistStatsDto;
import de.dhbwravensburg.webeng.stagefinder.api.dto.SetlistDto;
import de.dhbwravensburg.webeng.stagefinder.api.dto.SongStatDto;
import de.dhbwravensburg.webeng.stagefinder.api.dto.VenueStatDto;
import de.dhbwravensburg.webeng.stagefinder.domain.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtistStatsService {

    private static final int PAGES_TO_FETCH = 3;
    private static final int TOP_SONGS_LIMIT = 10;
    private static final int TOP_VENUES_LIMIT = 5;

    private final ArtistRepository artistRepository;
    private final SetlistFmService setlistFmService;

    public ArtistStatsDto getStats(String mbid) {
        String name = artistRepository.findByMbid(mbid)
                .map(a -> a.getName())
                .orElseGet(() -> setlistFmService.getArtist(mbid).getName());

        List<SetlistDto> setlists = Stream.iterate(1, p -> p + 1)
                .limit(PAGES_TO_FETCH)
                .flatMap(page -> {
                    try {
                        return setlistFmService.getSetlists(mbid, page).stream();
                    } catch (Exception e) {
                        log.warn("Failed to fetch setlists for {} page {}: {}", mbid, page, e.getMessage());
                        return Stream.empty();
                    }
                })
                .toList();

        List<String> allSongs = setlists.stream()
                .filter(s -> s.getSongs() != null)
                .flatMap(s -> s.getSongs().stream())
                .filter(s -> !s.isBlank())
                .toList();

        List<SongStatDto> topSongs = countAndSort(allSongs).entrySet().stream()
                .limit(TOP_SONGS_LIMIT)
                .map(e -> SongStatDto.builder().name(e.getKey()).count(e.getValue()).build())
                .toList();

        List<String> venueKeys = setlists.stream()
                .filter(s -> s.getVenueName() != null)
                .map(s -> s.getVenueName() + "||" + s.getCityName() + "||" + s.getCountryName())
                .toList();

        List<VenueStatDto> topVenues = countAndSort(venueKeys).entrySet().stream()
                .limit(TOP_VENUES_LIMIT)
                .map(e -> {
                    String[] parts = e.getKey().split("\\|\\|", 3);
                    return VenueStatDto.builder()
                            .name(parts[0])
                            .city(parts.length > 1 ? parts[1] : null)
                            .country(parts.length > 2 ? parts[2] : null)
                            .count(e.getValue())
                            .build();
                })
                .toList();

        List<LocalDate> dates = setlists.stream()
                .map(SetlistDto::getEventDate)
                .filter(d -> d != null)
                .map(d -> { try { return LocalDate.parse(d, SetlistDto.EVENT_DATE_FORMAT); } catch (Exception e) { return null; } })
                .filter(d -> d != null)
                .toList();

        String oldestShowDate = dates.stream().min(Comparator.naturalOrder())
                .map(d -> d.format(SetlistDto.EVENT_DATE_FORMAT)).orElse(null);
        String newestShowDate = dates.stream().max(Comparator.naturalOrder())
                .map(d -> d.format(SetlistDto.EVENT_DATE_FORMAT)).orElse(null);

        return ArtistStatsDto.builder()
                .mbid(mbid)
                .name(name)
                .totalShows(setlists.size())
                .totalSongPlays(allSongs.size())
                .oldestShowDate(oldestShowDate)
                .newestShowDate(newestShowDate)
                .topSongs(topSongs)
                .topVenues(topVenues)
                .build();
    }

    private Map<String, Integer> countAndSort(List<String> items) {
        return items.stream()
                .collect(Collectors.groupingBy(s -> s, Collectors.summingInt(s -> 1)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
}
