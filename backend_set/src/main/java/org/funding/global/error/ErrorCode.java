package org.funding.global.error;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "COMMON001", "입력값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(405, "COMMON002", "지원하지 않는 HTTP 메소드입니다."),
    INTERNAL_SERVER_ERROR(500, "COMMON003", "서버 내부 오류가 발생했습니다."),

    // 멤버 에러
    MEMBER_NOT_FOUND(404, "MEMBER001", "해당 사용자를 찾을 수 없습니다."),
    EMAIL_DUPLICATION(400, "MEMBER002", "이미 사용 중인 이메일입니다."),
    LOGIN_INPUT_INVALID(400, "MEMBER003", "이메일 또는 비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_ADMIN(401, "MEMBER004", "해당 멤버는 관리자가 아닙니다."),

    // 펀딩 에러
    FUNDING_NOT_FOUND(404, "FUNDING001", "해당 펀딩을 찾을 수 없습니다."),
    FAIL_FUNDING(400, "FUNDING002", "펀딩 생성에 실패하였습니다."),
    FAIL_FUND_UPDATE(402, "FUNDING003", "펀딩 수정에 실패하였습니다."),
    FAIL_DELETE_FUND(405, "FUNDING004", "펀딩 삭제중 에러가 발생하였습니다."),
    NOT_FUND_TYPE(406, "FUNDING005", "지원하지 않는 타입 정보입니다."),
    FAIL_ENTER_FUND(404, "FUNDING006", "펀딩 가입 처리중 오류 발생"),

    // 프로젝트 에러
    PROJECT_NOT_FOUND(404, "PROJECT001", "해당 프로젝트를 찾을 수 없습니다."),
    FAIL_PROJECT(400, "PROJECT002", "프로젝트 생성에 실패하였습니다."),
    NOT_FOUND_PROJECT_TYPE(402, "PROJECT003", "찾을 수 없는 프로젝트 타입니다."),
    NOT_PROJECT_TYPE(406, "PROJECT004", "지원하지 않는 타입 정보입니다."),
    NOT_PROJECT_OWNER(404, "PROJECT005", "프로젝트의 작성자가 아닙니다"),

    // 인증 에러
    AUTHENTICATION_FAILED(401, "A001", "인증에 실패하였습니다."),
    ACCESS_DENIED(403, "A002", "해당 권한이 없습니다."),

    // 카테고리 에러
    SAME_CATEGORY_NAME(404, "CATEGORY001", "이미 존재하는 이름의 카테고리입니다."),
    CATEGORY_NOT_FOUND(404, "CATEGORY002", "존재하지 않는 카테고리 이름입니다."),

    // 댓글 에러
    NOT_FOUND_COMMENT(404, "COMMENT001", "삭제할 댓글이 없습니다."),

    // 이메일 전송 에러
    ERROR_EMAIL(400, "EMAIL001", "이메일 전송 과정에서 에러가 발생했습니다."),

    // openai 에러
    FAIL_VISION_AI(400, "AI001", "이미지 검증에 실패하였습니다."),

    // 결제 애러
    FAIL_PAYMENT(400, "PAYMENT001", "결제 정보 생성에 실패하였습니다."),
    ENTER_DONATE_AMOUNT(401, "PAYMENT002", "기부 금액을 입력해주세요"),
    NOT_FOUND_PAYMENT(404, "PAYMENT003", "결제 정보를 찾을 수 없습니다."),
    FAIL_VERIFY_PAYMENT(405, "PAYMENT004", "결제 검증에 실패하였습니다."),
    FAIL_SUCCESS_PAY(406, "PAYMENT005", "결제 완료 처리 실패"),
    NO_HAVE_PAYMENT(400, "PAYMENT006", "결제가 필요한 펀딩이 아닙니다"),
    NOT_FOUND_PORT_ONE_TOKEN(404, "PAYMENT007", "포트원 토큰 획득 실패");


    private final int status;
    private final String code;
    private final String message;
}