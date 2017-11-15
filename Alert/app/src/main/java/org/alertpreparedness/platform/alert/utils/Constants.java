package org.alertpreparedness.platform.alert.utils;


public final class Constants {

    //Database tags
    public static final String APP_STATUS = "app_status";
    public static final String APP_STATUS_SAND = "sand";
    public static final String APP_STATUS_TEST = "test";
    public static final String APP_STATUS_UAT = "uat";
    public static final String APP_STATUS_LIVE = "live";

    //firebase related
    public static final String UID = "live";

    //animation delay
    public static final long MENU_CLOSING_DURATION = 250;

    //hazard
    public static final String HAZARD_ID = "hazard_id";
    public static final String[] HAZARD_SCENARIO = {"Cold Wave", "Conflict", "Cyclone", "Drought", "Earthquake", "Epidemic", "Fire", "Flash Flood", "Flood", "Heat Wave", "Heavy Rain", "Humanitarian Access",
            "Insect Infestation", "Landslide", "Locust Infestation", "Mudslide", "Population Displacement", "Population Return", "Snow Avalanche", "Snowfall", "Storm", "Storm Surge", "Technological Disaster", "Tornado", "Tsunami", "Violent Wind", "Volcano"};
    public static final String[] INDICATOR_GEO_LOCATION = {"National", "Subnational", "Use my location"};
    public static final String[] TRIGGER_LEVEL = {"Average or falling", "Potential challenges & risks", "Red alert"};

    //indicator-geo-location
    public static final int NATIONAL = 0;
    public static final int SUBNATIONAL = 1;
    public static final int MY_LOCATION = 2;

    //indicator-geo-location
    public static final int TRIGGER_GREEN = 0;
    public static final int TRIGGER_AMBER = 1;
    public static final int TRIGGER_RED = 2;
}
