package org.delcom.app.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ActivityView {

    @GetMapping("/")
    public String home() {
        // Arahkan ke folder pages/activities/home.html
        return "pages/activities/home"; 
    }

    @GetMapping("/activity")
    public String activityPage() {
        return "pages/activities/home";
    }
}