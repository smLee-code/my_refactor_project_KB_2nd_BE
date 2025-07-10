package org.scoula.security.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;

import javax.servlet.http.HttpServletRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
  private String username;
  private String password;

  public static LoginDTO of(HttpServletRequest request) {
    ObjectMapper om = new ObjectMapper();
    try{

      // RequestBody(username,password)에 담긴 JSON 문자열을 읽어와 LoginDTO로 역직렬화
      return om.readValue(request.getInputStream(), LoginDTO.class);
    } catch (Exception e) {
      e.printStackTrace();
      throw new BadCredentialsException("username 또는 password가 없습니다.");
    }
  }
}
