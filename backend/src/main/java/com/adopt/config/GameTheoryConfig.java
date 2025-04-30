package com.adopt.config;

import com.adopt.models.gametheory.GameTheoryModel;
import com.adopt.models.gametheory.NashEquilibriumModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for game theory models
 */
@Configuration
public class GameTheoryConfig {

    /**
     * Creates a map of available game theory models indexed by type
     */
    @Bean
    public Map<GameTheoryModel.GameTheoryType, GameTheoryModel> gameTheoryModels(
            NashEquilibriumModel nashEquilibriumModel) {
        
        Map<GameTheoryModel.GameTheoryType, GameTheoryModel> models = new HashMap<>();
        
        // Register the Nash Equilibrium model
        models.put(GameTheoryModel.GameTheoryType.NASH_EQUILIBRIUM, nashEquilibriumModel);
        
        // Additional game theory models can be registered here
        
        return models;
    }
} 