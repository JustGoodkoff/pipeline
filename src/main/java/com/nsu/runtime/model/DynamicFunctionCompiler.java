package com.nsu.runtime.model;
import javax.tools.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Function;

public final class DynamicFunctionCompiler {

    public static <I, O> Function<I, O> compile(
            String funBody,
            Class<I> inType,
            Class<O> outType
    ) {

        try {
            String className = "UserMapFn_" + UUID.randomUUID().toString().replace("-", "");

            String source = """
                import java.util.function.Function;
                public final class %s implements Function<%s, %s> {
                    @Override
                    public %s apply(%s in) {
                        %s
                    }
                }
                """.formatted(
                    className,
                    inType.getCanonicalName(),
                    outType.getCanonicalName(),
                    outType.getCanonicalName(),
                    inType.getCanonicalName(),
                    funBody
            );

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            InMemoryFileManager fileManager =
                    new InMemoryFileManager(compiler.getStandardFileManager(diagnostics, null, null));

            JavaFileObject file = new InMemoryJavaFileObject(className, source);

            boolean ok = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    null,
                    null,
                    List.of(file)
            ).call();

            if (!ok) {
                StringBuilder sb = new StringBuilder();
                for (Diagnostic<?> d : diagnostics.getDiagnostics()) {
                    sb.append(d.getMessage(null)).append("\n");
                }
                throw new IllegalStateException(sb.toString());
            }

            Class<?> cls = fileManager.getClassLoader(StandardLocation.CLASS_OUTPUT)
                    .loadClass(className);

            @SuppressWarnings("unchecked")
            Function<I, O> fn = (Function<I, O>) cls.getDeclaredConstructor().newInstance();
            return fn;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static final class InMemoryJavaFileObject extends SimpleJavaFileObject {
        private final String code;

        InMemoryJavaFileObject(String name, String code) {
            super(URI.create("string:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    static final class InMemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {

        private final Map<String, ByteArrayOutputStream> classes = new HashMap<>();

        InMemoryFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(
                Location location,
                String className,
                JavaFileObject.Kind kind,
                FileObject sibling
        ) {
            return new SimpleJavaFileObject(
                    URI.create("mem:///" + className + kind.extension), kind
            ) {
                @Override
                public OutputStream openOutputStream() {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    classes.put(className, baos);
                    return baos;
                }
            };
        }

        @Override
        public ClassLoader getClassLoader(Location location) {
            return new ClassLoader() {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    ByteArrayOutputStream baos = classes.get(name);
                    if (baos == null) {
                        throw new ClassNotFoundException(name);
                    }
                    byte[] bytes = baos.toByteArray();
                    return defineClass(name, bytes, 0, bytes.length);
                }
            };
        }
    }
}
