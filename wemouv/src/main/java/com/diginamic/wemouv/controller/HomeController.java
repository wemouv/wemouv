package com.diginamic.wemouv.controller;

import com.diginamic.wemouv.service.CovoiturageService;
import com.diginamic.wemouv.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    CovoiturageService covoiturageService;

    @Autowired
    UtilisateurService utilisateurService;

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("user", utilisateurService.findById(6L));

        model.addAttribute(
                "covoiturages",
                covoiturageService.findAll()
        );


        return "index";
    }
}
