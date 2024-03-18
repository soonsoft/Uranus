package com.soonsoft.uranus.core.common.attribute.access;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import com.soonsoft.uranus.core.common.attribute.IAttributeJsonTemplate;
import com.soonsoft.uranus.core.common.attribute.access.IndexNode.ListNode;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.core.functional.func.Func0;

public class DefaultAttributeJsonTemplate implements IAttributeJsonTemplate {
    // private final static byte BYTE_u = (byte) 'u';
    // private final static byte BYTE_0 = (byte) '0';

    private final static byte BYTE_LBRACKET = (byte) '[';
    private final static byte BYTE_RBRACKET = (byte) ']';
    private final static byte BYTE_LCURLY = (byte) '{';
    private final static byte BYTE_RCURLY = (byte) '}';

    // private final static byte BYTE_BACKSLASH = (byte) '\\';
    private final static byte BYTE_QUOTE = '"';
    private final static byte BYTE_COMMA = (byte) ',';
    private final static byte BYTE_COLON = (byte) ':';

    private final static byte[] NULL_BYTES = { 'n', 'u', 'l', 'l' };
    private final static byte[] TRUE_BYTES = { 't', 'r', 'u', 'e' };
    private final static byte[] FALSE_BYTES = { 'f', 'a', 'l', 's', 'e' };

    private static record JsonToken(JsonTokenType type, Object value, boolean isProperty) {}
    private static enum JsonTokenType {
        JsonArray,
        JsonObject,
        JsonSymbol,
        JsonValue,
    }
    private static class DefaultJsonWriter implements Closeable {
        private BufferedOutputStream out;
        private ByteArrayOutputStream memoryOutput;
        
        public void open() {
            memoryOutput = new ByteArrayOutputStream();
            out = new BufferedOutputStream(memoryOutput);
        }

        public void wirte(byte[] data) {
            if(data != null) {
                try {
                    out.write(data);
                } catch(IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        public byte[] getResult() {
            try {
                out.flush();
                return memoryOutput.toByteArray();
            } catch(IOException e) {
                throw new IllegalStateException(e);
            } finally {
                try {
                    close();
                } catch(IOException e) {
                    // ignore exception
                }
            }
        }

        @Override
        public void close() throws IOException {
            if(out != null) {
                try {
                    out.flush();
                } finally {
                    out.close();
                }
            }
        }
    }
    
    private final AttributeBagOperator attributeBagOperator;
    private final Map<String, IndexNode> indexes;

    private Action1<byte[]> appendBytesFn;
    private Func0<byte[]> returnBytesFn;

    public DefaultAttributeJsonTemplate(Map<String, IndexNode> indexes, AttributeBagOperator operator) {
        this.indexes = indexes;
        this.attributeBagOperator = operator;
    }

    public DefaultAttributeJsonTemplate(
        Map<String, IndexNode> indexes, AttributeBagOperator operator,
        Action1<byte[]> appendBytesFn, Func0<byte[]> returnBytesFn) {
        
        this.indexes = indexes;
        this.attributeBagOperator = operator;
        this.appendBytesFn = appendBytesFn;
        this.returnBytesFn = returnBytesFn;
    }

    public void setAppendBytes(Action1<byte[]> appendBytes) {
        this.appendBytesFn = appendBytes;
    }

    public void setReturnBytes(Func0<byte[]> returnBytes) {
        this.returnBytesFn = returnBytes;
    }

    public void appendBytes(byte[]... bytes) {
        if(this.appendBytesFn != null) {
            for(byte[] b : bytes) {
                this.appendBytesFn.apply(b);
            }
        }
    }

    public String getResult() {
        if(this.returnBytesFn != null) {
            byte[] result = this.returnBytesFn.call();
            if(result != null) {
                return new String(result, StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    public String getJSON(String entityName) {
        if(indexes == null) {
            return null;
        }

        Stack<JsonToken> stack = new Stack<>();
        if(entityName == null) {
            pushAll(stack, indexes.entrySet(), JsonTokenType.JsonObject);
        } else {
            IndexNode root = indexes.get(entityName);
            if(root != null) {
                stack.push(getJsonToken(root, true));
            }
        }

        if(!stack.isEmpty()) {
            if(appendBytesFn == null || returnBytesFn == null) {
                try(DefaultJsonWriter writer = new DefaultJsonWriter()) {
                    writer.open();
                    appendBytesFn = writer::wirte;
                    returnBytesFn = writer::getResult;
                    return toJsonString(stack);
                } catch(IllegalStateException e) {
                    throw e;
                } catch(IOException e) {
                    throw new IllegalStateException(e);
                }
            }
            return toJsonString(stack);
        }

        return new String(NULL_BYTES, StandardCharsets.UTF_8);
    }

    private void each(Stack<JsonToken> stack) {
        while(!stack.isEmpty()) {
            JsonToken token = stack.pop();
            if(token.type == JsonTokenType.JsonSymbol) {
                appendBytes((byte[]) token.value);
                continue;
            }

            IndexNode node = (IndexNode) token.value;

            if(token.isProperty) {
                // 写属性名
                appendBytes(
                    new byte[] { BYTE_QUOTE  }, 
                    node.getPropertyName().getBytes(StandardCharsets.UTF_8), 
                    new byte[] { BYTE_QUOTE, BYTE_COLON });
            }

            switch (token.type) {
                case JsonArray:
                    if(node.getChildren() == null) {
                        appendBytes(NULL_BYTES);
                    } else {
                        appendBytes(new byte[] { BYTE_LBRACKET });
                        stack.push(createJsonSymbol(new byte[] { BYTE_RBRACKET }));
                        if(!node.getChildren().isEmpty()) {
                            pushAll(stack, node.getChildren().entrySet(), token.type);
                        }
                    }
                    break;
                case JsonObject:
                    if(node.getChildren() == null) {
                        appendBytes(NULL_BYTES);
                    } else {
                        appendBytes(new byte[] { BYTE_LCURLY });
                        stack.push(createJsonSymbol(new byte[] { BYTE_RCURLY }));
                        if(!node.getChildren().isEmpty()) {
                            pushAll(stack, node.getChildren().entrySet(), token.type);
                        }
                    }
                    break;
                default:
                    AttributeData attrData = attributeBagOperator.getAttributeData(node.getIndex());
                    String attrValue = attrData.getPropertyValue();
                    if(attrValue == null) {
                        appendBytes(NULL_BYTES);
                    } else if("true".equals(attrValue)) {
                        appendBytes(TRUE_BYTES);
                    } else if("false".equals(attrValue)) {
                        appendBytes(FALSE_BYTES);
                    } else {
                        appendBytes(
                            new byte[] { BYTE_QUOTE  }, 
                            attrData.getPropertyValue().getBytes(StandardCharsets.UTF_8), 
                            new byte[] { BYTE_QUOTE });
                    }
                    break;
            }
        }
    }

    private void pushAll(Stack<JsonToken> stack, Set<Entry<String, IndexNode>> entrySet, JsonTokenType type) {
        // 反向入栈以保持顺序
        JsonToken[] children = new JsonToken[entrySet.size()];
        int index = children.length - 1;
        for(Map.Entry<String, IndexNode> entry : entrySet) {
            children[index] = getJsonToken(entry.getValue(), (type == JsonTokenType.JsonArray ? false : true));
            index--;
        }
        for(int i = 0; i < children.length; i++) {
            stack.push(children[i]);
            if(i < children.length - 1) {
                stack.push(createJsonSymbol(new byte[] { BYTE_COMMA }));
            }
        }
    }

    private JsonToken getJsonToken(IndexNode node, boolean isProperty) {
        JsonTokenType type = 
            node instanceof ListNode ? JsonTokenType.JsonArray : JsonTokenType.JsonObject;
        if(type == JsonTokenType.JsonObject && node.getChildren() == null) {
            type = JsonTokenType.JsonValue;
        }
        return new JsonToken(type, node, isProperty);
    }

    private JsonToken createJsonSymbol(byte[] bytes) {
        return new JsonToken(JsonTokenType.JsonSymbol, bytes, false);
    }

    private String toJsonString(Stack<JsonToken> stack) {
        appendBytes(new byte[] { BYTE_LCURLY });
        each(stack);
        appendBytes(new byte[] { BYTE_RCURLY });
        return getResult();
    }
}
