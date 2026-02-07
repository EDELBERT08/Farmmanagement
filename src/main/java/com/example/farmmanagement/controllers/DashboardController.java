package com.example.farmmanagement.controllers;

import com.example.farmmanagement.model.Animal;
import com.example.farmmanagement.model.Crop;
import com.example.farmmanagement.service.AnimalService;
import com.example.farmmanagement.service.CropService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DashboardController {

    private final CropService cropService;
    private final AnimalService animalService;

    public DashboardController(CropService cropService, AnimalService animalService) {
        this.cropService = cropService;
        this.animalService = animalService;
    }

    // Handles requests to the root URL "/" and "/home"
    @GetMapping({ "/", "/home" })
    public String showHomePage(Model model) {
        model.addAttribute("pageTitle", "Home Dashboard");
        model.addAttribute("activePage", "home");
        return "index";
    }

    // --- CROP MANAGEMENT ---

    // GET Request: Show the form and the list of existing crops
    @GetMapping("/crop")
    public String showCropManagementPage(Model model) {
        model.addAttribute("pageTitle", "Crop Management");
        model.addAttribute("activePage", "crop");
        model.addAttribute("crops", cropService.getAllCrops()); // Fetch from DB
        model.addAttribute("newCrop", new Crop()); // Provide an empty Crop object
        return "crop-management";
    }

    // POST Request: Handle the submission of the new crop form
    @PostMapping("/crop/add")
    public String addCrop(@ModelAttribute("newCrop") Crop crop, Model model) {
        if (crop != null && crop.getType() != null && !crop.getType().trim().isEmpty()) {
            cropService.saveCrop(crop); // Save to DB
        }
        return "redirect:/crop";
    }

    // --- ANIMAL MANAGEMENT ---
    @GetMapping("/animal")
    public String showAnimalManagementPage(Model model) {
        model.addAttribute("pageTitle", "Animal Management");
        model.addAttribute("activePage", "animal");
        model.addAttribute("animals", animalService.getAllAnimals()); // Fetch from DB
        model.addAttribute("newAnimal", new Animal()); // For form binding (ensure your HTML uses this)
        return "animal-management";
    }

    // Add a POST mapping to handle the form submission
    @PostMapping("/animal/add")
    public String addAnimal(@ModelAttribute("newAnimal") Animal animal, Model model) {
        // Save the animal data to the database
        if (animal != null) {
            animalService.saveAnimal(animal);
        }
        return "redirect:/animal";
    }

    // --- PRODUCE MANAGEMENT (Kept as before, but using a generic template) ---
    @GetMapping("/produce")
    public String showProduceManagementPage(Model model) {
        model.addAttribute("pageTitle", "Produce Management");
        model.addAttribute("activePage", "produce");
        return "management-page";
    }
}