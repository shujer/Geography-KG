package demo.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BaseController {
    /*
    加载home页面
     */
    @RequestMapping("")
    public String loadHomePage(Model m) {
        m.addAttribute("name", "SpringMVC");
        return "index";
    }
    /*
    加载echartsview页面
    */
    @RequestMapping("/echartsview")
    public String loadEchartsPage() {
        return "echartsview";
    }
}