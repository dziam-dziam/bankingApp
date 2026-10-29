package innowise.bankingApp.exceptions;

import lombok.Getter;

public enum ErrorCode {

    USER_NOT_FOUND("ERR_USER_404", "User with id: %d was not found"),

    PAYMENT_CARD_NOT_FOUND("ERR_CARD_404", "Payment card with id: %d was not found"),

    TOO_MANY_PAYMENT_CARDS("ERR_CARD_LIMIT", "User cannot have more than 5 cards assigned to them."),

    PAYMENT_CARD_ALREADY_ACTIVE("ERR_CARD_ACTIVE", "Payment card is already active"),

    PAYMENT_CARD_ALREADY_DEACTIVATED("ERR_CARD_INACTIVE", "Payment card is already deactivated"),

    USER_ALREADY_ACTIVE("ERR_USER_ACTIVE", "User is already active"),

    USER_ALREADY_DEACTIVATED("ERR_USER_INACTIVE", "User is already deactivated"),

    ID_REQUIRED("ERR_ID_REQUIRED", "Id cannot be null"),

    USER_ID_REQUIRED("ERR_USER_ID_REQUIRED", "User id cannot be null"),

    CARD_ID_REQUIRED("ERR_CARD_ID_REQUIRED", "PaymentCardId cannot be null"),

    DTO_REQUIRED("ERR_DTO_REQUIRED", "Dto cannot be null"),

    UPDATE_DTO_REQUIRED("ERR_UPDATE_DTO_REQUIRED", "Update dto cannot be null");

    @Getter
    private final String code;
    private final String messageTemplate;

    ErrorCode(String code, String messageTemplate) {
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    public String formatMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}