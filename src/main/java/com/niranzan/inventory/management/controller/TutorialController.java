package com.niranzan.inventory.management.controller;

import com.niranzan.inventory.management.entity.Tutorial;
import com.niranzan.inventory.management.repository.TutorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

import static com.niranzan.inventory.management.enums.AppPages.REDIRECT_URL;

@Controller
@RequiredArgsConstructor
public class TutorialController extends BaseController {
    private final TutorialRepository tutorialRepository;

    @GetMapping("/tutorials")
    public String getAll(Model model, @Param("keyword") String keyword) {
        try {
            List<Tutorial> tutorials = new ArrayList<>();
            if (keyword == null) {
                tutorials.addAll(tutorialRepository.findAll());
            } else {
                tutorials.addAll(tutorialRepository.findByTitleContainingIgnoreCase(keyword));
                model.addAttribute("keyword", keyword);
            }

            model.addAttribute("tutorials", tutorials);
        } catch (Exception e) {
            model.addAttribute("message.html", e.getMessage());
        }

        return "tutorials";
    }

    @GetMapping("/tutorials/new")
    public String addTutorial(Model model) {
        Tutorial tutorial = new Tutorial();
        tutorial.setPublished(true);

        model.addAttribute("tutorial", tutorial);
        model.addAttribute("pageTitle", "Create new Tutorial");

        return "tutorial_form";
    }

    @PostMapping("/tutorials/save")
    public String saveTutorial(Tutorial tutorial, RedirectAttributes redirectAttributes) {
        try {
            tutorialRepository.save(tutorial);

            redirectAttributes.addFlashAttribute("message.html", "The Tutorial has been saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addAttribute("message.html", e.getMessage());
        }

        return REDIRECT_URL.getPath() + "tutorials";
    }

    @GetMapping("/tutorials/{id}")
    public String editTutorial(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Tutorial tutorial = tutorialRepository.findById(id).get();

            model.addAttribute("tutorial", tutorial);
            model.addAttribute("pageTitle", "Edit Tutorial (ID: " + id + ")");

            return "tutorial_form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message.html", e.getMessage());

            return REDIRECT_URL.getPath() + "tutorials";
        }
    }

    @GetMapping("/tutorials/delete/{id}")
    public String deleteTutorial(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            tutorialRepository.deleteById(id);

            redirectAttributes.addFlashAttribute("message.html", "The Tutorial with id=" + id + " has been deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message.html", e.getMessage());
        }

        return REDIRECT_URL.getPath() + "tutorials";
    }

    @GetMapping("/tutorials/{id}/published/{status}")
    public String updateTutorialPublishedStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean published,
                                                Model model, RedirectAttributes redirectAttributes) {
        try {
            tutorialRepository.updatePublishedStatus(id, published);

            String status = published ? "published" : "disabled";
            String message = "The Tutorial id=" + id + " has been " + status;

            redirectAttributes.addFlashAttribute("message.html", message);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message.html", e.getMessage());
        }

        return REDIRECT_URL.getPath() + "tutorials";
    }
}
