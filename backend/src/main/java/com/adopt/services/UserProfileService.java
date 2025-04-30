package com.adopt.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for retrieving and managing user profile data
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final Random random = new Random();
    
    // In-memory cache of user profiles (in a real system, this would be a database)
    private final Map<String, Map<String, Object>> userProfileCache = new ConcurrentHashMap<>();
    
    /**
     * Get user profile data based on cookie ID or device ID
     * 
     * @param cookieId the browser cookie ID
     * @param deviceId the device ID (for mobile)
     * @return map of user profile attributes
     */
    public Map<String, Object> getUserProfile(String cookieId, String deviceId) {
        // Try to get profile by cookie ID first
        if (cookieId != null && !cookieId.isEmpty()) {
            Map<String, Object> profile = userProfileCache.get(cookieId);
            if (profile != null) {
                return profile;
            }
        }
        
        // Try to get profile by device ID next
        if (deviceId != null && !deviceId.isEmpty()) {
            Map<String, Object> profile = userProfileCache.get(deviceId);
            if (profile != null) {
                return profile;
            }
        }
        
        // If no profile exists, create a synthetic one for demonstration
        Map<String, Object> syntheticProfile = createSyntheticProfile(cookieId != null ? cookieId : deviceId);
        
        // Cache the synthetic profile
        String profileKey = cookieId != null && !cookieId.isEmpty() ? cookieId : deviceId;
        if (profileKey != null && !profileKey.isEmpty()) {
            userProfileCache.put(profileKey, syntheticProfile);
        }
        
        return syntheticProfile;
    }
    
    /**
     * Update user profile with new data
     * 
     * @param cookieId the browser cookie ID
     * @param deviceId the device ID (for mobile)
     * @param newData the new data to merge into the profile
     */
    public void updateUserProfile(String cookieId, String deviceId, Map<String, Object> newData) {
        Map<String, Object> existingProfile = getUserProfile(cookieId, deviceId);
        existingProfile.putAll(newData);
        
        // Update cache
        String profileKey = cookieId != null && !cookieId.isEmpty() ? cookieId : deviceId;
        if (profileKey != null && !profileKey.isEmpty()) {
            userProfileCache.put(profileKey, existingProfile);
        }
    }
    
    /**
     * Track a user event (click, conversion, etc.)
     * 
     * @param cookieId the browser cookie ID
     * @param deviceId the device ID (for mobile)
     * @param eventType the type of event
     * @param eventData additional event data
     */
    public void trackUserEvent(String cookieId, String deviceId, String eventType, Map<String, Object> eventData) {
        Map<String, Object> profile = getUserProfile(cookieId, deviceId);
        
        // Update profile based on event type
        switch (eventType) {
            case "click":
                profile.put("lastClickTimestamp", System.currentTimeMillis());
                profile.put("totalClicks", ((Integer) profile.getOrDefault("totalClicks", 0)) + 1);
                break;
                
            case "conversion":
                profile.put("lastConversionTimestamp", System.currentTimeMillis());
                profile.put("totalConversions", ((Integer) profile.getOrDefault("totalConversions", 0)) + 1);
                profile.put("hasPreviousConversion", true);
                break;
                
            case "pageview":
                profile.put("lastPageviewTimestamp", System.currentTimeMillis());
                profile.put("totalPageviews", ((Integer) profile.getOrDefault("totalPageviews", 0)) + 1);
                profile.put("daysSinceLastVisit", 0);
                break;
        }
        
        // Merge any additional event data
        if (eventData != null) {
            profile.putAll(eventData);
        }
        
        // Update cache
        String profileKey = cookieId != null && !cookieId.isEmpty() ? cookieId : deviceId;
        if (profileKey != null && !profileKey.isEmpty()) {
            userProfileCache.put(profileKey, profile);
        }
    }
    
    // Helper methods
    
    private Map<String, Object> createSyntheticProfile(String userId) {
        Map<String, Object> profile = new HashMap<>();
        
        // Basic demographics (synthetic)
        profile.put("ageGroup", getRandomAgeGroup());
        profile.put("gender", getRandomGender());
        profile.put("income", getRandomIncome());
        
        // Behavioral attributes
        profile.put("isTargetAudience", random.nextBoolean());
        profile.put("purchasingPower", 0.1 + (random.nextDouble() * 0.9)); // 0.1 to 1.0
        profile.put("engagementLevel", 0.1 + (random.nextDouble() * 0.9)); // 0.1 to 1.0
        
        // Recency metrics
        profile.put("daysSinceLastVisit", random.nextInt(30));
        profile.put("daysSinceLastInterest", random.nextInt(60));
        
        // Conversion history
        boolean hasPreviousConversion = random.nextDouble() < 0.3; // 30% chance
        profile.put("hasPreviousConversion", hasPreviousConversion);
        
        // Interest categories (synthetic)
        String[] interests = getRandomInterests();
        profile.put("interests", interests);
        
        return profile;
    }
    
    private String getRandomAgeGroup() {
        String[] ageGroups = {"18-24", "25-34", "35-44", "45-54", "55-64", "65+"};
        return ageGroups[random.nextInt(ageGroups.length)];
    }
    
    private String getRandomGender() {
        String[] genders = {"male", "female", "other", "unknown"};
        return genders[random.nextInt(genders.length)];
    }
    
    private int getRandomIncome() {
        // Random income between $20,000 and $200,000
        return 20000 + (random.nextInt(18) * 10000);
    }
    
    private String[] getRandomInterests() {
        String[] allInterests = {
            "technology", "travel", "fashion", "sports", "food", "gaming",
            "automotive", "finance", "health", "education", "entertainment",
            "home", "beauty", "business", "family"
        };
        
        // Select 1-5 random interests
        int numInterests = 1 + random.nextInt(5);
        String[] interests = new String[numInterests];
        
        for (int i = 0; i < numInterests; i++) {
            interests[i] = allInterests[random.nextInt(allInterests.length)];
        }
        
        return interests;
    }
} 