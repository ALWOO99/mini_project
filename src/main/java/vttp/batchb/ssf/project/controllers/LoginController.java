package vttp.batchb.ssf.project.controllers;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import vttp.batchb.ssf.project.service.CatBreedService;
import vttp.batchb.ssf.project.service.DogBreedService;
import vttp.batchb.ssf.project.service.UserService;

@Controller
public class LoginController {

    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    private final UserService userService;

    @Autowired
    private DogBreedService dogBreedService; // Service to handle fetching dog breeds
    @Autowired
    private CatBreedService catBreedService; // Service to handle fetching dog breeds

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    // Display login page
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping()
    public String loginRedirect() {
        return "login";
    }

    // Handle login form submission
    @PostMapping("/login")
public String loginSubmit(
        @RequestParam @NotEmpty(message = "Username cannot be empty") String username,
        @RequestParam @NotEmpty(message = "Password cannot be empty") String password,
        Model model,
        HttpSession session) {

    logger.info("Login attempt for user: " + username);

    if (username.isBlank() || password.isBlank()) {
        model.addAttribute("error", "Username and password cannot be empty");
        return "login";  // Return to login page on validation failure
    }

    // Authenticate the user using the service
    if (userService.authenticate(username, password)) {
        logger.info("User " + username + " successfully authenticated");

        session.setAttribute("username", username);

        // Fetch dog and cat breeds after login
        List<String> dogBreeds = dogBreedService.getDogBreeds();
        List<String> catBreeds = catBreedService.getCatBreeds();
        model.addAttribute("dogBreeds", dogBreeds);
        model.addAttribute("catBreeds", catBreeds);
        model.addAttribute("username", username);

        return "home";
    } else {
        logger.warning("Failed login attempt for user: " + username);
        model.addAttribute("error", "Invalid username or password");
        return "login";  // Return to login page on failure
    }
}


    // Display registration page
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    // Handle registration form submission
    @PostMapping("/register")
    public String registerSubmit(
            @RequestParam @NotEmpty(message = "Username cannot be empty") String username,
            @RequestParam @NotEmpty(message = "Password cannot be empty")@Size(min = 6, max = 20, message = "Password must be 6-20 characters long") String password,
            Model model) {

        // Check for empty fields
        if (username.isBlank() || password.isBlank()) {
            model.addAttribute("error", "Username and password cannot be empty");
            return "register";
        }

        // Check for minimum password length
        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters long");
            return "register";
        }

        // Attempt to register
        if (userService.register(username, password)) {
            model.addAttribute("message", "User registered successfully. Please login.");
            return "login"; // Return to login page after successful registration
        } else {
            model.addAttribute("error", "Username already exists. Try a different one.");
            model.addAttribute("alert", true); // Add alert flag
            return "register";
        }
    }

    // Handle logout and redirect to login page
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Clear session
        return "redirect:/login"; // Redirect to login page
    }

    // Display home page with dog breeds after login
    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        // Check if user is authenticated by checking the session
        String username = (String) session.getAttribute("username");

        // Redirect to login if user is not authenticated
        if (username == null) {
            return "redirect:/login";
        }

        // Fetch dog breeds after checking if the user is logged in
        List<String> dogBreeds = dogBreedService.getDogBreeds();
        List<String> catBreeds = catBreedService.getCatBreeds();
        model.addAttribute("username", username); // Add username to model
        model.addAttribute("dogBreeds", dogBreeds); // Add dog breeds to model
        model.addAttribute("catBreeds", catBreeds); // Add cat breeds to model

        return "home"; // Return to home page with dog breeds
    }

    @GetMapping("/breedDetails")
    public String showBreedDetails(Model model, HttpSession session) {

        List<String> dogBreeds = dogBreedService.getDogBreeds();
        model.addAttribute("dogBreeds", dogBreeds);
        return "breedDetails"; // renders templates/breedDetails.html
    }

    @GetMapping("/catBreedDetails")
    public String showCatBreedDetails(Model model, HttpSession session) {

        List<String> catBreeds = catBreedService.getCatBreeds();
        model.addAttribute("catBreeds", catBreeds);
        return "catBreedDetails"; // renders templates/breedDetails.html
    }
}
