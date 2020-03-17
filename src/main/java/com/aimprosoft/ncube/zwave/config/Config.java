package com.aimprosoft.ncube.zwave.config;

import io.imont.ferret.client.FerretBuilder;
import io.imont.ferret.client.config.NetworkConfig;
import io.imont.ferret.client.config.YamlFileStateStore;
import io.imont.lion.Lion;
import io.imont.lion.LionBuilder;
import io.imont.mole.MoleBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class Config {
    @Bean
    public Lion lionCoordinator() throws IOException {
        NetworkConfig config = new NetworkConfig()
                .setCoordinator(true)
                .setFriendlyName("Z-wave-network");

        FerretBuilder ferretBuilder = new FerretBuilder()
                .withConfiguration(config)
                .withStateStore(new YamlFileStateStore("coordinatorYml.yml"));
        MoleBuilder moleBuilder = new MoleBuilder()
                .withDbFile("coordinatorDB.db");
        Lion lion = new LionBuilder()
                .ferretBuilder(ferretBuilder)
                .moleBuilder(moleBuilder)
                .workDir(".")
                .build();

        lion.registerNetwork(new ZWaveNetworkLayer());
        lion.getDriverManager().registerBundle(new DriverBundle());
        return lion;
    }

}
