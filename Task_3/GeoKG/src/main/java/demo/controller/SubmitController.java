package demo.controller;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import demo.pojo.Link;
import demo.pojo.Node;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SubmitController {
    @RequestMapping(value = "/echartsview/sparql.do")
    @ResponseBody
    public void sparqlQuery(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getParameter("sparql");
        System.out.println(query);
        Node node = new Node(0,"乔布斯",10);
        Link link = new Link("丽萨-乔布斯","女儿","乔布斯");
        Node[] ns = new Node[5];
        Link[] ls = new Link[5];
        for(int i = 0; i < ns.length; i++) {
            ns[i] = node;
            ls[i] = link;
        }
        Map<String,Object> option= new HashMap<String,Object>();
        option.put("nodes", ns);
        option.put("links", ls);
        Gson gson = new Gson();
        String options = gson.toJson(option);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.append(options);
    }
}
