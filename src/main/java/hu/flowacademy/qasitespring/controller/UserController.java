package hu.flowacademy.qasitespring.controller;

import hu.flowacademy.qasitespring.dto.ResponseMessageDTO;
import hu.flowacademy.qasitespring.model.User;
import hu.flowacademy.qasitespring.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
/**
 * @RequestMapping in a general request handler, so it doesn't specify the exact method to handle,
 * so it handles all the methods.
 * We use it to add a /api prefix before all other request handlers in the class
 */
@RequestMapping("/api")
/**
 * using @RequiredArgsConstructor to implement constructor based dependency injection
 * So we don't have to use @Autowired or write constructor by own
 *
 * RequiredArgsConstructor will contains all the final fields of the class
 */
@RequiredArgsConstructor
public class UserController {

    private static final String MSG_SUCCESSFULLY_REGISTRATION = "Successfully registration!";

    /**
     * Injecting an instance of the UserService here
     */
    private final UserService userService;

    /**
     * @PostMapping handles POST request on the specified path
     * ex. /users, if class has another @RequestMapping("api"),
     * the real path would be /api/user
     */
    @PostMapping("/users")
    public ResponseMessageDTO createUser(@RequestBody User user) {
        userService.save(user);
        return ResponseMessageDTO.builder().message(MSG_SUCCESSFULLY_REGISTRATION).build();
    }

    @GetMapping("/users/current")
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
