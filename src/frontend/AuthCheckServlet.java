package frontend;

import base.AccountService;
import main.NoUserException;
import main.UserProfile;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthCheckServlet extends HttpServlet {

    protected final AccountService accountService;

    public AuthCheckServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        JSONObject json = new JSONObject();
        Map<Object, Object> jsonBody = new HashMap<>();
        json.put("body", jsonBody);

        int status = HttpServletResponse.SC_OK;

        UserProfile user;
        try {
            user = this.accountService.getUser(request.getSession().getId());
            user.hydrate(jsonBody);
        } catch (NoUserException e) {
            status = HttpServletResponse.SC_UNAUTHORIZED;
        }
        json.put("status", status);
        response.setStatus(status);
        response.getWriter().print(json.toJSONString());
    }
}
