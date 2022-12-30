package br.com.noahfood.pagamentos.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class Configurations {
    @Bean
    public ModelMapper obtainModel(){
        return new ModelMapper();
    }
}
