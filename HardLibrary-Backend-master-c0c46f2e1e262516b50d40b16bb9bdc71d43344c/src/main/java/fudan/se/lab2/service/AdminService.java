package fudan.se.lab2.service;

import com.alibaba.fastjson.JSONObject;
import fudan.se.lab2.domain.*;
import fudan.se.lab2.repository.*;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AdminService {
    private JwtTokenUtil jwtTokenUtil;
    private Utility utility;
    @Resource
    private JdbcTemplate jdbcTemplate;//自动分析使用数据库

    public AdminService(Utility utility,JwtTokenUtil jwtTokenUtil){
        this.utility = utility;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    //管理员登录方法
    public String adminLogin(String username, String password){
        List<Admin> userList = jdbcTemplate.query("SELECT * FROM admin ",new RowMapper<Admin>() {
            Admin admin = null;
            @Override
            public Admin mapRow(ResultSet rs, int rowNum) throws SQLException {
                admin = new Admin();
                admin.setId(rs.getLong("id"));
                admin.setPassword(rs.getString("password"));
                admin.setUsername(rs.getString("username"));
                return admin;
            }
        });
        boolean login=false;
        String token = "";
        for (Admin admin : userList) {
            if (admin.getUsername().equals(username)&&admin.getPassword().equals(password)) {
                login=true;
                token = jwtTokenUtil.generateAdminToken(admin);
                break;
            }
        }
        if (login){
            return token;
        }else {
            return "false";
        }
    }






}
