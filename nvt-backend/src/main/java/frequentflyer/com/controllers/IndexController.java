package frequentflyer.com.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by sasaradovanovic on 5/3/17.
 */
@Controller
public class IndexController {

    @CrossOrigin(origins = "*")
    @RequestMapping(value = {
            "/",
            "/combinations",
            "/combinations/**"
    }, method = RequestMethod.GET)
    public String index() {
        return "index.html";
    }
}
