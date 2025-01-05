package com.benorim.carhov.entity;

import com.benorim.carhov.enums.DayOfWeek;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class RideSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private CarHovUser user;

    @Column(nullable = false)
    private double startLatitude;

    @Column(nullable = false)
    private double startLongitude;

    @Column(nullable = false)
    private double endLatitude;

    @Column(nullable = false)
    private double endLongitude;

    @Enumerated(EnumType.STRING)
    @Transient // Do not persist this field; it's derived from `daysOfWeek`.
    private List<DayOfWeek> dayList;

    private String daysOfWeek;

    private LocalTime departureTime;

    private int availableSeats;

    private boolean available;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Setter(AccessLevel.NONE)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public List<DayOfWeek> getDayList() {
        if (daysOfWeek == null || daysOfWeek.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(daysOfWeek.split(","))
                .map(DayOfWeek::valueOf)
                .collect(Collectors.toList());
    }

    public void setDayList(List<DayOfWeek> dayList) {
        this.dayList = dayList;
        this.daysOfWeek = dayList.stream()
                .map(DayOfWeek::name)
                .collect(Collectors.joining(","));
    }
}
