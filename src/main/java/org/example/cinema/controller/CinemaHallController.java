package org.example.cinema.controller;

import org.example.cinema.model.CinemaHall;
import org.example.cinema.model.Seat;
import org.example.cinema.model.User;
import org.example.cinema.service.CinemaHallService;
import org.example.cinema.service.SeatService;
import org.example.cinema.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/halls")
public class CinemaHallController {
    private final CinemaHallService cinemaHallService;
    private final SeatService seatService;
    private final UserService userService;

    @Autowired
    public CinemaHallController(CinemaHallService cinemaHallService,
                                SeatService seatService,
                                UserService userService) {
        this.cinemaHallService = cinemaHallService;
        this.seatService = seatService;
        this.userService = userService;
    }

    // listHalls - отображение списка кинозалов
    @GetMapping
    public String listHalls(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        Optional<User> userOptional = userService.findUserByEmail(email);

        if (userOptional.isPresent() && "ADMIN".equals(userOptional.get().getRole())) {
            List<CinemaHall> halls = cinemaHallService.getAllCinemaHalls();
            model.addAttribute("halls", halls);
            return "admin/halls";
        }
        return "redirect:/login";
    }

    // showCreateHallForm - отображение формы создания зала
    @GetMapping("/new")
    public String showCreateHallForm(Model model) {
        model.addAttribute("hall", new CinemaHall());
        return "admin/hall-form";
    }

    // createHall - создание нового кинозала
    @PostMapping
    public String createHall(@ModelAttribute CinemaHall hall,
                             @RequestParam (required = false) String name,
                             @RequestParam  String description,
                             @RequestParam Integer rowsCount,
                             @RequestParam Integer seatsPerRow,
                             @RequestParam Integer capacity,
                             @RequestParam String hallType,
                             RedirectAttributes redirectAttributes) {

        if (!cinemaHallService.isHallNameUnique(hall.getName())) {
            redirectAttributes.addFlashAttribute("error", "Зал с таким названием уже существует");
            return "redirect:/admin/halls/new";
        }

        // Устанавливаем тип зала
        if (hallType != null && !hallType.isEmpty()) {
            hall.setHallType(hallType);
        } else {
            hall.setHallType("STANDARD");
        }

        // Создаем зал с расположением
        CinemaHall savedHall = cinemaHallService.saveCinemaHallWithLayout(hall, name, description, rowsCount, seatsPerRow, capacity, hallType);

        // Создание мест в зале
        seatService.createSeatsForHall(savedHall.getId(), rowsCount, seatsPerRow);

        redirectAttributes.addFlashAttribute("success", "Кинозал успешно создан");
        return "redirect:/admin/halls";
    }

    // viewHall - просмотр деталей зала
    @GetMapping("/{id}")
    public String viewHall(@PathVariable Long id, Model model) {
        Optional<CinemaHall> hallOptional = cinemaHallService.getCinemaHallById(id);
        if (hallOptional.isPresent()) {
            CinemaHall hall = hallOptional.get();
            List<Seat> seatsList = seatService.getSeatsByHall(id);

            // Группируем места по номерам рядов
            Map<Integer, List<Seat>> seatsByRow = seatsList.stream()
                    .collect(Collectors.groupingBy(Seat::getRowNumber));

            model.addAttribute("hall", hall);
            model.addAttribute("seatsByRows", seatsByRow); // Теперь это Map<Integer, List<Seat>>

            return "admin/hall-details";
        }
        return "redirect:/admin/halls";
    }

    // deleteHall - удаление кинозала
    @PostMapping("/{id}/delete")
    public String deleteHall(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        cinemaHallService.deleteCinemaHall(id);
        redirectAttributes.addFlashAttribute("success", "Кинозал успешно удален");
        return "redirect:/admin/halls";
    }
}