package com.soonsoft.uranus.services.dictionary.exceptions;

/**
 * Exception raised when a dictionary item (by group + item code) does not exist.
 */
public class DictionaryNotExistsException extends DictionaryException {

    private final String groupCode;
    private final String itemCode;

    public DictionaryNotExistsException(String groupCode, String itemCode) {
        super(String.format("Dictionary item not found: groupCode=%s, itemCode=%s", groupCode, itemCode));
        this.groupCode = groupCode;
        this.itemCode = itemCode;
    }

    public DictionaryNotExistsException(String groupCode, String itemCode, Throwable cause) {
        super(String.format("Dictionary item not found: groupCode=%s, itemCode=%s", groupCode, itemCode), cause);
        this.groupCode = groupCode;
        this.itemCode = itemCode;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public String getItemCode() {
        return itemCode;
    }
}
