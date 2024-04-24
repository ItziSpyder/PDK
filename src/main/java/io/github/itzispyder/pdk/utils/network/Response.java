package io.github.itzispyder.pdk.utils.network;

import java.util.Arrays;

public class Response {

    private final String id;
    private final Method method;
    private final Type type;
    private final Object[] args;

    public Response(Method method, Type type, Object... args) {
        this.method = method;
        this.type = type;
        this.args = args;
        this.id = "%s:%s(%s)".formatted(method.id, type.id, Arrays.toString(args).replaceAll("(^\\[)|(]$)", ""));
    }

    public Method getMethod() {
        return method;
    }

    public Type getType() {
        return type;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Response res))
            return false;
        return res.id.equals(this.id);
    }

    public static Response parse(String responseString) throws IllegalArgumentException {
        try {
            String[] split = responseString.split(":");
            String body = split[1];
            String methodStr = split[0].trim();
            String typeStr = body.replaceFirst("\\(.*\\)", "").trim();
            String argsStr = body.replaceFirst("^.*\\(", "").replaceAll("\\)$", "").trim();
            Object[] args = argsStr.split("\s*,\s*");

            Method method = null;
            Type type = null;

            for (var v : Method.values()) {
                if (v.id.equals(methodStr)) {
                    method = v;
                    break;
                }
            }
            for (var v : Type.values()) {
                if (v.id.equals(typeStr)) {
                    type = v;
                    break;
                }
            }

            if (method == null || type == null)
                throw new IllegalArgumentException("method or type cannot be null! (provided: %s, %s)".formatted(methodStr, typeStr));

            return new Response(method, type, args);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("response syntax is invalid: " + ex.getMessage());
        }
    }

    public static boolean isResponsePattern(String str) {
        return str.matches(".+:.+\\((.+,?)\\)");
    }

    public enum Method {
        TO_SERVER("improperC2S"),
        TO_CLIENT("improperS2C");

        private final String id;

        Method(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public enum Type {
        HANDSHAKE("handshake"),
        DEAD_FISH("dead_fish");

        private final String id;

        Type(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
