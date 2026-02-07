package com.example.farmmanagement.service.impl;

import com.example.farmmanagement.model.Crop;
import com.example.farmmanagement.service.AiService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class AiServiceImpl implements AiService {

    private final Random random = new SecureRandom();

    @Override
    public String getFarmSummary() {
        String[] summaries = {
                "Farm productivity is up by 15% this season. Consider rotating crops in the North Field.",
                "Weather patterns suggest a dry spell next week. Ensure irrigation systems are prepped.",
                "Market prices for Maize are trending upwards. Good time to plan harvest logistics.",
                "Soil health in the South Sector looks excellent. Nitrogen levels are optimal.",
                "Pest activity reported in neighboring farms. Monitor crop health closely."
        };
        return summaries[random.nextInt(summaries.length)];
    }

    @Override
    public String getCropInsights(Crop crop) {
        if (crop == null)
            return "No crop data available for analysis.";

        String[] insights = {
                "Growth rate is on track. Projected harvest yield: High.",
                "Slight water stress detected. Recommend increasing irrigation frequency.",
                "Nutrient uptake is optimal. Continue current fertilizer regimen.",
                "Early signs of pest vulnerability. scout for aphids.",
                "Harvest window is approaching. Schedule labor and equipment."
        };
        return insights[random.nextInt(insights.length)];
    }
}
