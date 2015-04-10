package frontend;

import base.AccountService;
import base.ValidatedServlet;
import main.NoUserException;
import main.SignInException;
import main.UserProfile;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SignInServlet extends ValidatedServlet {

    private static final String[] LOGIN_REQUIRED_FIELDS = {"name", "password",};
    protected final AccountService accountService;

    public SignInServlet(AccountService accountService) {
        super(SignInServlet.LOGIN_REQUIRED_FIELDS);
        this.accountService = accountService;
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        JSONObject json = new JSONObject();
        Map<Object, Object> jsonBody = new HashMap<>();
        json.put("body", jsonBody);

        int status = HttpServletResponse.SC_OK;

        UserProfile user = null;
        try {
            user = this.accountService.getUser(request.getSession().getId());
            status = HttpServletResponse.SC_FORBIDDEN;
            jsonBody.put("message", "You're already authorized.");
        } catch (NoUserException e) {
            if (!this.areRequiredFieldsValid(request, jsonBody)) {
                status = HttpServletResponse.SC_BAD_REQUEST;
            } else {
                String name = request.getParameter("name");
                String sid = request.getSession().getId();
                try {
                    user = this.accountService.signIn(sid, name, request.getParameter("password"));
                    user.hydrate(jsonBody);
                } catch (SignInException e1) {
                    status = HttpServletResponse.SC_UNAUTHORIZED;
                    jsonBody.put("message", "Incorrect username or password.");
                }
            }
        }
        json.put("status", status);
        response.setStatus(status);
        response.getWriter().print(json.toJSONString());
    }
}
