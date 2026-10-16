package br.com.aweb.sistema_vendas.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

// O Render fornece a URL do banco no formato postgresql://usuario:senha@host/banco,
// que o driver JDBC não entende. Aqui ela é convertida para jdbc:postgresql://host/banco,
// com usuário e senha separados. URLs que já estão no formato jdbc: não são alteradas.
public class UrlBancoDeDadosRender implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        var url = environment.getProperty("DB_URL", environment.getProperty("DATABASE_URL"));
        if (url == null || !(url.startsWith("postgres://") || url.startsWith("postgresql://")))
            return;

        var uri = URI.create(url);
        var jdbcUrl = "jdbc:postgresql://" + uri.getHost()
                + (uri.getPort() > 0 ? ":" + uri.getPort() : "")
                + uri.getPath()
                + (uri.getQuery() != null ? "?" + uri.getQuery() : "");

        var propriedades = new HashMap<String, Object>();
        propriedades.put("spring.datasource.url", jdbcUrl);

        if (uri.getRawUserInfo() != null) {
            var credenciais = uri.getRawUserInfo().split(":", 2);
            propriedades.put("spring.datasource.username", URLDecoder.decode(credenciais[0], StandardCharsets.UTF_8));
            if (credenciais.length > 1)
                propriedades.put("spring.datasource.password", URLDecoder.decode(credenciais[1], StandardCharsets.UTF_8));
        }

        environment.getPropertySources().addFirst(new MapPropertySource("urlBancoDeDadosRender", propriedades));
    }

}
