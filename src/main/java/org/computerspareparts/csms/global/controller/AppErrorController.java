package org.computerspareparts.csms.global.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppErrorController implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(AppErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = 0;
        if (statusObj != null) {
            try {
                statusCode = Integer.parseInt(statusObj.toString());
            } catch (NumberFormatException ex) {
                log.warn("Unable to parse error status code: {}", statusObj);
            }
        }
        log.info("Handling error, status={}", statusCode);

        model.addAttribute("status", statusCode);

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            return "error/404";
        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
            return "error/403";
        } else {
            return "error/error";
        }
    }

    @GetMapping("/error/403")
    public String accessDenied() {
        // Direct mapping so AccessDeniedHandlerImpl can forward to /error/403 and render the 403 page.
        return "error/403";
    }
}
