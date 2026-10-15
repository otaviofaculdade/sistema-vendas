package br.com.aweb.sistema_vendas.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ConfiguracoesDeSeguranca {

    @Bean
    public SecurityFilterChain filtroDeSeguranca(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        // Recursos públicos
                        .requestMatchers("/css/**", "/js/**", "/login", "/error").permitAll()

                        // Produtos e clientes: cadastro (novo, edição e exclusão) só para ADMIN
                        .requestMatchers("/produtos/novo", "/produtos/edit/**", "/produtos/delete/**").hasRole("ADMIN")
                        .requestMatchers("/clientes/novo", "/clientes/edit/**", "/clientes/delete/**").hasRole("ADMIN")

                        // Produtos e clientes: consulta da listagem para ADMIN e PADRAO
                        .requestMatchers(HttpMethod.GET, "/produtos", "/clientes").hasAnyRole("ADMIN", "PADRAO")

                        // Pedidos: operações liberadas para ADMIN e PADRAO
                        .requestMatchers("/pedidos/**").hasAnyRole("ADMIN", "PADRAO")

                        // Qualquer outra rota de produtos/clientes não prevista acima fica com o ADMIN
                        .requestMatchers("/produtos/**", "/clientes/**").hasRole("ADMIN")

                        // Regra mais genérica: o restante exige apenas estar logado
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/acesso-negado"))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
