package fudan.se.lab2.controller;

import fudan.se.lab2.domain.Employee;
import fudan.se.lab2.domain.TestHistory;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import fudan.se.lab2.service.AuthService;
import fudan.se.lab2.service.EmployeeService;
import fudan.se.lab2.service.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBW
 */
@Controller
public class EmployeeController {

    private AuthService authService;
    private EmployeeService employeeService;
    private Utility utility;
    private JwtTokenUtil jwtTokenUtil;//可能有bug
    Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Resource
    private JdbcTemplate jdbcTemplate;//自动分析使用数据库

    public EmployeeController(AuthService authService, Utility utility,EmployeeService employeeService,JwtTokenUtil jwtTokenUtil) {
        this.authService = authService;
        this.utility = utility;
        this.jwtTokenUtil = jwtTokenUtil;
        this.employeeService = employeeService;
    }



    //登录方法
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Map<String,String> request) throws JSONException {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.login(request.get("username"),request.get("password")));
    }

    //员工查看自己的信息
    @PostMapping("/checkselfinfo")
    @ResponseBody
    public ResponseEntity<?> checkSelfInfo(@RequestBody Map<String,String> request,@RequestHeader Map<String, String> headers) throws JSONException {
        String token = headers.get("authorization");
        System.out.println("打印"+token);
        Employee employee = jwtTokenUtil.getEmployeeFromToken(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    //员工修改个人信息
    @PostMapping("/updateselfinfo")
    @ResponseBody
    public ResponseEntity<?> updateSelfInfo(@RequestBody Map<String,String> request,@RequestHeader Map<String, String> headers) throws JSONException {
        String token = headers.get("authorization");
        Employee employee = jwtTokenUtil.getEmployeeFromToken(token);
        //暂时不允许修改username
        //String username = request.get("username");
        String password = request.get("password");
        //不允许修改name
        String email = request.get("email");
        //不允许修改type
        int age = Integer.parseInt(request.get("age"));
        //不允许修改id
        //不允许自己修改自己的部门
        String location = request.get("location");
        String sex = request.get("sex");
        //不允许修改入职日期
        String telephoneNumber = request.get("telephoneNumber");
        String result = employeeService.updateInfo(employee.getId(),password,email,age,location,sex,telephoneNumber);
        String updateLog = utility.updateLog(employee.getUsername(),"update personal info", utility.getCurrentDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    //员工查看个人课程
    @PostMapping("/checkselftest")
    @ResponseBody
    public ResponseEntity<?> checkSelfTest(@RequestBody Map<String,String> request,@RequestHeader Map<String, String> headers) throws JSONException {
        String token = headers.get("authorization");
        Employee employee = jwtTokenUtil.getEmployeeFromToken(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(utility.getTestHistoryListFromTestHistorybyEmployeeId(employee.getId()));
    }

    //员工查看个人课程对应的讲师的信息,需要id为入参
    @PostMapping("/checkselflessontutor")
    @ResponseBody
    public ResponseEntity<?> checkSelfLessonTutor(@RequestBody Map<String,String> request,@RequestHeader Map<String, String> headers) throws JSONException {
        String token = headers.get("authorization");
        Long id = Long.valueOf(request.get("id"));
        Employee employee = jwtTokenUtil.getEmployeeFromToken(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(utility.getTutorFromTestHistory(id));
    }

    //员工查看个人课程
    @PostMapping("/checkselflesson")
    @ResponseBody
    public ResponseEntity<?> checkSelfLesson(@RequestBody Map<String,String> request,@RequestHeader Map<String, String> headers) throws JSONException {
        String token = headers.get("authorization");
        Employee employee = jwtTokenUtil.getEmployeeFromToken(token);
        List<TestHistory> historyList = utility.getTestHistoryListFromTestHistorybyEmployeeId(employee.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(utility.getLessonListFromTestHistoryList(historyList));
    }


    /**
     * This is a function to test your connectivity. (健康测试时，可能会用到它）.
     */
    @GetMapping("/welcome")
    public ResponseEntity<?> welcome() {
        Map<String, String> response = new HashMap<>();
        String message = "Welcome to 2021 Software Engineering Lab2. ";
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

}
