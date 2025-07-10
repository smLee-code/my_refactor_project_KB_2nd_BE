<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login Page</title>
</head>
<body>
<%--
    http.formLogin().loginPage("/security/login")
    - GET 방식으로 /security/login 요청 시
    /WEB-INF/views/security/login.jsp로 포워드

    - 포워드 시 자동으로 request scope 객체에
     "_csrf" 객체가 추가됨
--%>


<h1>login</h1>

<!-- 로그인 실패 메시지 표시 -->
<c:if test="${param.error != null}">
    <div style="color: red">사용자 ID 또는 비밀번호가 틀립니다.</div>
</c:if>

<form name='f' action='/security/login' method='POST'>
    <!-- CSRF 토큰 (필수!) -->
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

    <table>
        <tr>
            <td>User:</td>
            <td><input type='text' name='username' value=''></td>  <!-- 사용자 ID 필드명 고정 -->
        </tr>
        <tr>
            <td>Password:</td>
            <td><input type='password' name='password' /></td>     <!-- 비밀번호 필드명 고정 -->
        </tr>
        <tr>
            <td colspan='2'>
                <input name="submit" type="submit" value="Login" />
            </td>
        </tr>
    </table>
</form>
</body>
</html>
