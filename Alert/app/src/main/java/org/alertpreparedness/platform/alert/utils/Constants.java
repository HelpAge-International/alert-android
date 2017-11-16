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
    public static final String[] HAZARD_SCENARIO_NAME = {"Cold Wave", "Conflict", "Cyclone", "Drought", "Earthquake", "Epidemic", "Fire", "Flash Flood", "Flood", "Heat Wave", "Heavy Rain", "Humanitarian Access",
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

    //user type
    public static final int CountryDirector = 1;
    public static final int CountryAdmin = 2;
    public static final int ErtLeader = 3;
    public static final int Ert = 4;
    public static final int PartnerUser = 5;

    //copy over from portal
    public static final int HazardScenario0 = 0;
    public static final int HazardScenario1 = 1;
    public static final int HazardScenario2 = 2;
    public static final int HazardScenario3 = 3;
    public static final int HazardScenario4 = 4;
    public static final int HazardScenario5 = 5;
    public static final int HazardScenario6 = 6;
    public static final int HazardScenario7 = 7;
    public static final int HazardScenario8 = 8;
    public static final int HazardScenario9 = 9;
    public static final int HazardScenario10 = 10;
    public static final int HazardScenario11 = 11;
    public static final int HazardScenario12 = 12;
    public static final int HazardScenario13 = 13;
    public static final int HazardScenario14 = 14;
    public static final int HazardScenario15 = 15;
    public static final int HazardScenario16 = 16;
    public static final int HazardScenario17 = 17;
    public static final int HazardScenario18 = 18;
    public static final int HazardScenario19 = 19;
    public static final int HazardScenario20 = 20;
    public static final int HazardScenario21 = 21;
    public static final int HazardScenario22 = 22;
    public static final int HazardScenario23 = 23;
    public static final int HazardScenario24 = 24;
    public static final int HazardScenario25 = 25;
    public static final int HazardScenario26 = 26;
    public static final int[] HAZARD_SCENARIO = {HazardScenario0, HazardScenario1, HazardScenario2, HazardScenario3, HazardScenario4, HazardScenario5, HazardScenario6, HazardScenario7, HazardScenario8, HazardScenario9, HazardScenario10, HazardScenario11,
            HazardScenario12, HazardScenario13, HazardScenario14, HazardScenario15, HazardScenario16, HazardScenario17, HazardScenario18, HazardScenario19, HazardScenario20, HazardScenario21, HazardScenario22, HazardScenario23, HazardScenario24, HazardScenario25, HazardScenario26};
}
