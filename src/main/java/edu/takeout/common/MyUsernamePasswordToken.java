package edu.takeout.common;

import lombok.Data;
import org.apache.shiro.authc.UsernamePasswordToken;

@Data
public class MyUsernamePasswordToken extends UsernamePasswordToken {
    private boolean iu;
}
