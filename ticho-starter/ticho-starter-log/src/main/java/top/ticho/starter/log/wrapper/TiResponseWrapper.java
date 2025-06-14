package top.ticho.starter.log.wrapper;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author zhajianjun
 * @date 2023-01-11 14:00
 */
@Slf4j
public class TiResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream outputStream;
    private final ServletOutputStream servletOutputStream;
    private final PrintWriter printWriter;

    public TiResponseWrapper(HttpServletResponse response) {
        super(response);
        outputStream = new ByteArrayOutputStream(2048);
        servletOutputStream = new WrapperOutputStream(outputStream, response);
        printWriter = new WrapperWriter(outputStream, response);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() {
        return printWriter;
    }

    public String getBody() {
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    static class WrapperWriter extends PrintWriter {

        private final HttpServletResponse response;

        public WrapperWriter(ByteArrayOutputStream out, HttpServletResponse response) {
            super(out);
            this.response = response;
        }

        @Override
        public void write(int b) {
            super.write(b);
            try {
                response.getWriter().write(b);
            } catch (IOException e) {
                log.error("{}", e.getMessage(), e);
                this.setError();
            }
        }

        @Override
        public void write(@NonNull String s, int off, int len) {
            super.write(s, off, len);
            try {
                response.getWriter().write(s, off, len);
            } catch (IOException e) {
                log.error("{}", e.getMessage(), e);
                this.setError();
            }
        }
    }

    static class WrapperOutputStream extends ServletOutputStream {

        private final OutputStream outputStream;
        private final HttpServletResponse response;

        public WrapperOutputStream(OutputStream outputStream, HttpServletResponse response) {
            super();
            this.response = response;
            this.outputStream = outputStream;
        }

        @Override
        public boolean isReady() {
            if (response == null) {
                return false;
            }
            try {
                return response.getOutputStream().isReady();
            } catch (IOException e) {
                log.error("{}", e.getMessage(), e);
            }
            return false;
        }

        @Override
        public void setWriteListener(WriteListener listener) {
            if (response != null) {
                try {
                    response.getOutputStream().setWriteListener(listener);
                } catch (IOException e) {
                    log.error("{}", e.getMessage(), e);
                }
            }

        }

        @Override
        public void write(int b) throws IOException {
            if (response != null) {
                response.getOutputStream().write(b);
            }
            outputStream.write(b);
        }

    }
}
