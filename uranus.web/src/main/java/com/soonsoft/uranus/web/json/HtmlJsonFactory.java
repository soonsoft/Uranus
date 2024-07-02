package com.soonsoft.uranus.web.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
import com.fasterxml.jackson.core.json.WriterBasedJsonGenerator;
import com.fasterxml.jackson.core.util.Separators;

import org.springframework.web.util.HtmlUtils;

/**
 * HtmlJsonFactory
 */
public final class HtmlJsonFactory extends JsonFactory {

    private static final long serialVersionUID = 1L;

    public HtmlJsonFactory() {
        super();
    }

    @Override
    protected JsonGenerator _createGenerator(Writer out, IOContext ctxt) throws IOException {
        HtmlEncodeJsonGenerator gen = new HtmlEncodeJsonGenerator(ctxt, _generatorFeatures, _objectCodec, out);
        if (_characterEscapes != null) {
            gen.setCharacterEscapes(_characterEscapes);
        }
        SerializableString rootSep = _rootValueSeparator;
        if (Separators.DEFAULT_ROOT_VALUE_SEPARATOR.equals(rootSep.getValue())) {
            gen.setRootValueSeparator(rootSep);
        }
        return gen;
    }

    @Override
    protected JsonGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) throws IOException {
        UTF8JsonGenerator gen = new HtmlEncodeUTF8JsonGenerator(ctxt, _generatorFeatures, _objectCodec, out);
        if (_characterEscapes != null) {
            gen.setCharacterEscapes(_characterEscapes);
        }
        SerializableString rootSep = _rootValueSeparator;
        if (Separators.DEFAULT_ROOT_VALUE_SEPARATOR.equals(rootSep.getValue())) {
            gen.setRootValueSeparator(rootSep);
        }
        return gen;
    }

    /**
     * Json序列化的时候自动对字符串编码,防止XSS攻击
     */
    private static class HtmlEncodeUTF8JsonGenerator extends UTF8JsonGenerator {

        public HtmlEncodeUTF8JsonGenerator(IOContext ctxt, int features, ObjectCodec codec, OutputStream out) {
            super(ctxt, features, codec, out, JsonFactory.DEFAULT_QUOTE_CHAR);
        }

        @Override
        public void writeString(String text) throws IOException {
            super.writeString(HtmlUtils.htmlEscape(text));
        }

    }

    /**
     * Json序列化的时候自动对字符串编码,防止XSS攻击
     */
    private static class HtmlEncodeJsonGenerator extends WriterBasedJsonGenerator {

        public HtmlEncodeJsonGenerator(IOContext ctxt, int features, ObjectCodec codec, Writer writer) {
            super(ctxt, features, codec, writer, JsonFactory.DEFAULT_QUOTE_CHAR);
        }

        @Override
        public void writeString(String text) throws IOException {
            super.writeString(HtmlUtils.htmlEscape(text));
        }
    }
}