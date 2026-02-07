package com.example.farmmanagement.controllers;

import com.example.farmmanagement.model.Animal;
import com.example.farmmanagement.model.Crop;
import com.example.farmmanagement.model.CropActivity;
import com.example.farmmanagement.model.CropTransaction;
import com.example.farmmanagement.model.Field;
import com.example.farmmanagement.service.AnimalService;
import com.example.farmmanagement.service.CropActivityService;
import com.example.farmmanagement.service.CropService;
import com.example.farmmanagement.service.CropTransactionService;
import com.example.farmmanagement.service.FieldService;
import com.example.farmmanagement.service.AiService;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class DashboardController {

    private final CropService cropService;
    private final AnimalService animalService;
    private final FieldService fieldService;
    private final CropTransactionService transactionService;
    private final CropActivityService activityService;
    private final AiService aiService;

    public DashboardController(CropService cropService, AnimalService animalService, FieldService fieldService,
            CropTransactionService transactionService, CropActivityService activityService, AiService aiService) {
        this.cropService = cropService;
        this.animalService = animalService;
        this.fieldService = fieldService;
        this.transactionService = transactionService;
        this.activityService = activityService;
        this.aiService = aiService;
    }

    // Handles requests to "/home"
    @GetMapping("/home")
    public String showHomePage(Model model) {
        model.addAttribute("pageTitle", "Home Dashboard");
        model.addAttribute("activePage", "home");

        // Global Analytics
        long totalCrops = cropService.countCrops();
        long totalAnimals = animalService.countAnimals();
        Double globalExpense = transactionService.calculateGlobalTotalExpenses();
        Double globalIncome = transactionService.calculateGlobalTotalIncome();
        Double globalProfit = globalIncome - globalExpense;

        model.addAttribute("totalCrops", totalCrops);
        model.addAttribute("totalAnimals", totalAnimals);
        model.addAttribute("globalExpense", globalExpense);
        model.addAttribute("globalIncome", globalIncome);
        model.addAttribute("globalProfit", globalProfit);

        // Chart Data
        model.addAttribute("cropDistribution", cropService.getCropTypeDistribution());

        // AI Insights
        model.addAttribute("farmInsights", aiService.getFarmSummary());

        // Mock Data for UI (To be replaced with real services later)
        model.addAttribute("weatherLocation", "Chicago, IL");
        model.addAttribute("weatherTemp", "24");
        model.addAttribute("weatherCondition", "Sunny");

        return "index";
    }

    // --- CROP MANAGEMENT ---

    // GET Request: Show the form and the list of existing crops
    @GetMapping("/crop")
    public String showCropManagementPage(Model model) {
        model.addAttribute("pageTitle", "Crop Management");
        model.addAttribute("activePage", "crop");
        model.addAttribute("crops", cropService.getAllCrops()); // Fetch from DB
        model.addAttribute("fields", fieldService.getAllFields()); // Fetch fields for selection
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

    @GetMapping("/crop/{id}")
    public String viewCropDetails(@PathVariable Long id, Model model) {
        Crop crop = cropService.getCropById(id).orElse(null);
        if (crop == null) {
            return "redirect:/crop";
        }
        model.addAttribute("crop", crop);
        model.addAttribute("transactions", transactionService.getTransactionsByCropId(id));
        model.addAttribute("totalExpense", transactionService.calculateTotalExpenses(id));
        model.addAttribute("totalIncome", transactionService.calculateTotalIncome(id));
        model.addAttribute("activities", activityService.getActivitiesByCropId(id));
        model.addAttribute("newTransaction", new CropTransaction());
        model.addAttribute("newActivity", new CropActivity());
        model.addAttribute("pageTitle", "Crop Details: " + crop.getType());
        model.addAttribute("activePage", "crop");

        // AI Insights for Crop
        model.addAttribute("cropInsights", aiService.getCropInsights(crop));

        return "crop-details";
    }

    @PostMapping("/crop/{id}/transaction/add")
    public String addTransaction(@PathVariable Long id, @ModelAttribute("newTransaction") CropTransaction transaction) {
        Crop crop = cropService.getCropById(id).orElse(null);
        if (crop != null && transaction != null) {
            transaction.setCrop(crop);
            transactionService.saveTransaction(transaction);
        }
        return "redirect:/crop/" + id;
    }

    @PostMapping("/crop/{id}/activity/add")
    public String addActivity(@PathVariable Long id, @ModelAttribute("newActivity") CropActivity activity) {
        Crop crop = cropService.getCropById(id).orElse(null);
        if (crop != null && activity != null) {
            activity.setCrop(crop);
            activityService.saveActivity(activity);
        }
        return "redirect:/crop/" + id;
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

    // --- FIELD MANAGEMENT ---
    @GetMapping("/field")
    public String showFieldManagementPage(Model model) {
        model.addAttribute("pageTitle", "Field Management");
        model.addAttribute("activePage", "field");
        model.addAttribute("fields", fieldService.getAllFields());
        model.addAttribute("newField", new Field());
        return "field-management";
    }

    @PostMapping("/field/add")
    public String addField(@ModelAttribute("newField") Field field, Model model) {
        if (field != null && field.getName() != null && !field.getName().trim().isEmpty()) {
            fieldService.saveField(field);
        }
        return "redirect:/field";
    }
}