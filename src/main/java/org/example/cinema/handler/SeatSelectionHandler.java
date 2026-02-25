package org.example.cinema.handler;

import org.example.cinema.dto.SeatSelectionDTO;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SeatSelectionHandler {

    // validateSeatSelection - валидация выбора мест
    public boolean validateSeatSelection(List<SeatSelectionDTO> selectedSeats) {
        if (selectedSeats == null || selectedSeats.isEmpty()) {
            return false;
        }

        // Проверка, что все выбранные места доступны
        return selectedSeats.stream()
                .allMatch(SeatSelectionDTO::getIsAvailable);
    }

    // calculateTotalPrice - расчет общей стоимости выбранных мест
    public Double calculateTotalPrice(List<SeatSelectionDTO> selectedSeats) {
        return selectedSeats.stream()
                .mapToDouble(SeatSelectionDTO::getPrice)
                .sum();
    }

    // checkAdjacentSeats - проверка, что места находятся рядом
    public boolean checkAdjacentSeats(List<SeatSelectionDTO> selectedSeats) {
        if (selectedSeats.size() <= 1) {
            return true;
        }

        // Проверка, что все места в одном ряду
        Integer firstRow = selectedSeats.get(0).getRowNumber();
        boolean sameRow = selectedSeats.stream()
                .allMatch(seat -> seat.getRowNumber().equals(firstRow));

        if (!sameRow) {
            return false;
        }

        // Проверка, что номера мест идут подряд
        List<Integer> seatNumbers = selectedSeats.stream()
                .map(SeatSelectionDTO::getSeatNumber)
                .sorted()
                .collect(Collectors.toList());

        for (int i = 1; i < seatNumbers.size(); i++) {
            if (seatNumbers.get(i) - seatNumbers.get(i - 1) != 1) {
                return false;
            }
        }

        return true;
    }

    // getSelectedSeatIds - получение ID выбранных мест
    public List<Long> getSelectedSeatIds(List<SeatSelectionDTO> selectedSeats) {
        return selectedSeats.stream()
                .map(SeatSelectionDTO::getSeatId)
                .collect(Collectors.toList());
    }
}