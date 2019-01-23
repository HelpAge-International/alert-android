package org.alertpreparedness.platform.v1.utils;


import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import org.alertpreparedness.platform.v1.R;

public final class Constants {

    //Database tags
    public static final String APP_STATUS = "app_status";
    public static final String APP_STATUS_SAND = "sand";
    public static final String APP_STATUS_TEST = "test";
    public static final String APP_STATUS_UAT = "uat";
    public static final String APP_STATUS_LIVE = "live";

    //animation delay
    public static final long MENU_CLOSING_DURATION = 250;

    //important ids
    public static final String UID = "uid";
    public static final String COUNTRY_ID = "country_id";
    public static final String AGENCY_ID = "agency_id";
    public static final String SYSTEM_ID = "system_id";
    public static final String LOCAL_NETWORK_ID = "local_network_id";
    public static final String NETWORK_ID = "network_id";
    public static final String NETWORK_COUNTRY_ID = "network_country_id";
    public static final String USER_TYPE = "user_type";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";

    //hazard
    public static final String HAZARD_ID = "hazard_id";
    public static final String[] HAZARD_SCENARIO_NAME = {"Cold Wave", "Conflict", "Cyclone", "Drought", "Earthquake", "Epidemic", "Fire", "Flash Flood", "Flood", "Heat Wave", "Heavy Rain", "Humanitarian Access",
            "Insect Infestation", "Landslide", "Locust Infestation", "Mudslide", "Population Displacement", "Population Return", "Snow Avalanche", "Snowfall", "Storm", "Storm Surge", "Technological Disaster", "Tornado", "Tsunami", "Violent Wind", "Volcano"};

    public enum Hazard{
        COLD_WAVE(0, R.string.hazard_cold_wave, R.drawable.cold_wave),
        CONFLICT(1, R.string.hazard_conflict, R.drawable.conflict),
        CYCLONE(2, R.string.hazard_cyclone, R.drawable.cyclone),
        DROUGHT(3, R.string.hazard_drought, R.drawable.drought),
        EARTHQUAKE(4, R.string.hazard_earthquake, R.drawable.earthquake),
        EPIDEMIC(5, R.string.hazard_epidemic, R.drawable.epidemic),
        FIRE(6, R.string.hazard_fire, R.drawable.fire),
        FLASH_FLOOD(7, R.string.hazard_flash_flood, R.drawable.flash_flood),
        FLOOD(8, R.string.hazard_flood, R.drawable.flood),
        HEAT_WAVE(9, R.string.hazard_heat_wave, R.drawable.heat_wave),
        HEAVY_RAIN(10, R.string.hazard_heavy_rain, R.drawable.heavy_rain),
        HUMANITARIAN_ACCESS(11, R.string.hazard_humanitarian_access, R.drawable.humanitarian_access),
        INSECT_INFESTATION(12, R.string.hazard_insect_infestation, R.drawable.insect_infestation),
        LANDSLIDE(13, R.string.hazard_landslide, R.drawable.landslide_mudslide),
        LOCUST_INFESTATION(14, R.string.hazard_locust_infestation, R.drawable.locust_infestation),
        MUDSLIDE(15, R.string.hazard_mudslide, R.drawable.landslide_mudslide),
        POPULATION_DISPLACEMENT(16, R.string.hazard_population_displacement, R.drawable.population_displacement),
        POPULATION_RETURN(17, R.string.hazard_population_return, R.drawable.population_return),
        SNOW_AVALANCHE(18, R.string.hazard_snow_avalanche, R.drawable.snow_avalanche),
        SNOWFALL(19, R.string.hazard_snowfall, R.drawable.snowfall),
        STORM(20, R.string.hazard_storm, R.drawable.storm),
        STORM_SURGE(21, R.string.hazard_storm_surge, R.drawable.storm_surge),
        TECHNOLOGICAL_DISASTER(22, R.string.hazard_technological_disaster, R.drawable.technological_disaster),
        TORNADO(23, R.string.hazard_tornado, R.drawable.tornado),
        TSUNAMI(24, R.string.hazard_tsunami, R.drawable.tsunami),
        VIOLENT_WIND(25, R.string.hazard_violent_wind, R.drawable.violent_wind),
        VOLCANO(26, R.string.hazard_volcano, R.drawable.volcano);

        private final int id;
        @StringRes
        private final int stringRes;
        @DrawableRes
        private final int iconRes;

        Hazard(int id, int stringRes, int iconRes) {
            this.id = id;
            this.stringRes = stringRes;
            this.iconRes = iconRes;
        }

        public int getId() {
            return id;
        }

        public int getStringRes() {
            return stringRes;
        }

        public int getIconRes() {
            return iconRes;
        }

        public static Hazard getById(int id){
            for (Hazard hazard : values()) {
                if(hazard.getId() == id){
                    return hazard;
                }
            }

            return null;
        }
    }

    public static final String[] INDICATOR_GEO_LOCATION = {"National", "Subnational", "Use my location"};
    public static final String[] TRIGGER_LEVEL = {"Average or falling", "Potential challenges & risks", "High"};
    public static final String[] FREQUENCY_NAMES = {"hour(s)", "day(s)", "week(s)", "month(s)", "year(s)"};

    //duration type
    public static final int HOUR = 0;
    public static final int DAY = 1;
    public static final int WEEK = 2;
    public static final int MONTH = 3;
    public static final int YEAR = 4;

    //action duration
    public static final int DUE_WEEK = 0;
    public static final int DUE_MONTH = 1;
    public static final int DUE_YEAR = 2;

    //indicator-geo-location
    public static final int NATIONAL = 0;
    public static final int SUBNATIONAL = 1;
    public static final int MY_LOCATION = 2;

    //indicator-geo-location
    public static final int TRIGGER_GREEN = 0;
    public static final int TRIGGER_AMBER = 1;
    public static final int TRIGGER_RED = 2;

    //Action Type
    public static final int CHS = 0;
    public static final int MANDATED = 1;
    public static final int CUSTOM = 2;

    public static final int NOTIFICATION_SETTING_ALERT_LEVEL_CHANGED = 0;
    public static final int NOTIFICATION_SETTING_RED_ALERT_REQUEST = 1;
    public static final int NOTIFICATION_SETTING_UPDATE_HAZARD_INDICATOR = 2;
    public static final int NOTIFICATION_SETTING_MPA_APA_EXPIRED = 3;
    public static final int NOTIFICATION_SETTING_RESPONSE_PLAN_EXPIRED = 4;
    public static final int NOTIFICATION_SETTING_RESPONSE_PLAN_REJECTED = 5;



    //INDICATOR-FREQUENCY

    //user type
    public static final int CountryDirector = 1;
    public static final int CountryAdmin = 2;
    public static final int ErtLeader = 3;
    public static final int Ert = 4;
    public static final int PartnerUser = 5;

    //Red alert request
    public static final int REQ_PENDING = 0;
    public static final int REQ_APPROVED = 1;
    public static final int REQ_REJECTED = 2;

    //Preparedness
    public static final int ALL = 0;
    public static final int MPA = 1;
    public static final int APA = 2;

    public static final String[] COUNTRIES = {
            "Afghanistan",
            "Åland Islands",
            "Albania",
            "Algeria",
            "American Samoa",
            "Andorra",
            "Angola",
            "Anguilla",
            "Antarctica",
            "Antigua and Barbuda",
            "Argentina",
            "Armenia",
            "Aruba",
            "Australia",
            "Austria",
            "Azerbaijan",
            "Bahamas",
            "Bahrain",
            "Bangladesh",
            "Barbados",
            "Belarus",
            "Belgium",
            "Belize",
            "Benin",
            "Bermuda",
            "Bhutan",
            "Bolivia",
            "Bonaire, Sint Eustatius and Saba",
            "Bosnia and Herzegovina",
            "Botswana",
            "Bouvet Island",
            "Brazil",
            "British Indian Ocean Territory",
            "Brunei",
            "Bulgaria",
            "Burkina Faso",
            "Burundi",
            "Cambodia",
            "Cameroon",
            "Canada",
            "Cape Verde",
            "Cayman Islands",
            "Central African Republic",
            "Chad",
            "Chile",
            "China",
            "Christmas Island",
            "Cocos (Keeling) Islands",
            "Colombia",
            "Comoros",
            "Congo",
            "Congo, the Democratic Republic of the",
            "Cook Islands",
            "Costa Rica",
            "Ivory Coast",
            "Croatia",
            "Cuba",
            "Curaçao",
            "Cyprus",
            "Czech Republic",
            "Denmark",
            "Djibouti",
            "Dominica",
            "Dominican Republic",
            "Ecuador",
            "Egypt",
            "El Salvador",
            "Equatorial Guinea",
            "Eritrea",
            "Estonia",
            "Ethiopia",
            "Falkland Islands",
            "Faroe Islands",
            "Fiji",
            "Finland",
            "France",
            "French Guiana",
            "French Polynesia",
            "French Southern Territories",
            "Gabon",
            "Gambia",
            "Georgia",
            "Germany",
            "Ghana",
            "Gibraltar",
            "Greece",
            "Greenland",
            "Grenada",
            "Guadeloupe",
            "Guam",
            "Guatemala",
            "Guernsey",
            "Guinea",
            "Guinea-Bissau",
            "Guyana",
            "Haiti",
            "Heard Island and McDonald Islands",
            "Holy See (Vatican City State)",
            "Honduras",
            "Hong Kong",
            "Hungary",
            "Iceland",
            "India",
            "Indonesia",
            "Iran, Islamic Republic of",
            "Iraq",
            "Ireland",
            "Isle of Man",
            "Israel",
            "Italy",
            "Jamaica",
            "Japan",
            "Jersey",
            "Jordan",
            "Kazakhstan",
            "Kenya",
            "Kiribati",
            "Korea, Democratic People's Republic of",
            "Korea, Republic of",
            "Kuwait",
            "Kyrgyzstan",
            "Lao People's Democratic Republic",
            "Latvia",
            "Lebanon",
            "Lesotho",
            "Liberia",
            "Libya",
            "Liechtenstein",
            "Lithuania",
            "Luxembourg",
            "Macao",
            "Macedonia, the former Yugoslav Republic of",
            "Madagascar",
            "Malawi",
            "Malaysia",
            "Maldives",
            "Mali",
            "Malta",
            "Marshall Islands",
            "Martinique",
            "Mauritania",
            "Mauritius",
            "Mayotte",
            "Mexico",
            "Micronesia, Federated States of",
            "Moldova, Republic of",
            "Monaco",
            "Mongolia",
            "Montenegro",
            "Montserrat",
            "Morocco",
            "Mozambique",
            "Myanmar",
            "Namibia",
            "Nauru",
            "Nepal",
            "Netherlands",
            "New Caledonia",
            "New Zealand",
            "Nicaragua",
            "Niger",
            "Nigeria",
            "Niue",
            "Norfolk Island",
            "Northern Mariana Islands",
            "Norway",
            "Oman",
            "Pakistan",
            "Palau",
            "Palestinian Territory, Occupied",
            "Panama",
            "Papua New Guinea",
            "Paraguay",
            "Peru",
            "Millippines",
            "Pitcairn",
            "Poland",
            "Portugal",
            "Puerto Rico",
            "Qatar",
            "Réunion",
            "Romania",
            "Russian Federation",
            "Rwanda",
            "Saint Barthélemy",
            "Saint Helena, Ascension and Tristan da Cunha",
            "Saint Kitts and Nevis",
            "Saint Lucia",
            "Saint Martin (French part)",
            "Saint Pierre and Miquelon",
            "Saint Vincent and the Grenadines",
            "Samoa",
            "San Marino",
            "Sao Tome and Principe",
            "Saudi Arabia",
            "Senegal",
            "Serbia",
            "Seychelles",
            "Sierra Leone",
            "Singapore",
            "Sint Maarten (Dutch part)",
            "Slovakia",
            "Slovenia",
            "Solomon Islands",
            "Somalia",
            "South Africa",
            "South Georgia and the South Sandwich Islands",
            "South Sudan",
            "Spain",
            "Sri Lanka",
            "Sudan",
            "Suriname",
            "Svalbard and Jan Mayen",
            "Swaziland",
            "Sweden",
            "Switzerland",
            "Syrian Arab Republic",
            "Taiwan, Province of China",
            "Tajikistan",
            "Tanzania, United Republic of",
            "Thailand",
            "Timor-Leste",
            "Togo",
            "Tokelau",
            "Tonga",
            "Trinidad and Tobago",
            "Tunisia",
            "Turkey",
            "Turkmenistan",
            "Turks and Caicos Islands",
            "Tuvalu",
            "Uganda",
            "Ukraine",
            "United Arab Emirates",
            "United Kingdom",
            "United States",
            "United States Minor Outlying Islands",
            "Uruguay",
            "Uzbekistan",
            "Vanuatu",
            "Venezuela",
            "Vietnam",
            "Virgin Islands, British",
            "Virgin Islands, U.S.",
            "Wallis and Futuna",
            "Western Sahara",
            "Yemen",
            "Zambia",
            "Zimbabwe"
    };

    public static final int AF = 0;
    public static final int AX = 1;
    public static final int AL = 2;
    public static final int DZ = 3;
    public static final int AS = 4;
    public static final int AD = 5;
    public static final int AO = 6;
    public static final int AI = 7;
    public static final int AQ = 8;
    public static final int AG = 9;
    public static final int AR = 10;
    public static final int AM = 11;
    public static final int AW = 12;
    public static final int AU = 13;
    public static final int AT = 14;
    public static final int AZ = 15;
    public static final int BS = 16;
    public static final int BH = 17;
    public static final int BD = 18;
    public static final int BB = 19;
    public static final int BY = 20;
    public static final int BE = 21;
    public static final int BZ = 22;
    public static final int BJ = 23;
    public static final int BM = 24;
    public static final int BT = 25;
    public static final int BO = 26;
    public static final int BQ = 27;
    public static final int BA = 28;
    public static final int BW = 29;
    public static final int BV = 30;
    public static final int BR = 31;
    public static final int IO = 32;
    public static final int BN = 33;
    public static final int BG = 34;
    public static final int BF = 35;
    public static final int BI = 36;
    public static final int KH = 37;
    public static final int CM = 38;
    public static final int CA = 39;
    public static final int CV = 40;
    public static final int KY = 41;
    public static final int CF = 42;
    public static final int TD = 43;
    public static final int CL = 44;
    public static final int CN = 45;
    public static final int CX = 46;
    public static final int CC = 47;
    public static final int CO = 48;
    public static final int KM = 49;
    public static final int CG = 50;
    public static final int CD = 51;
    public static final int CK = 52;
    public static final int CR = 53;
    public static final int CI = 54;
    public static final int HR = 55;
    public static final int CU = 56;
    public static final int CW = 57;
    public static final int CY = 58;
    public static final int CZ = 59;
    public static final int DK = 60;
    public static final int DJ = 61;
    public static final int DM = 62;
    public static final int DOO = 63;
    public static final int EC = 64;
    public static final int EG = 65;
    public static final int SV = 66;
    public static final int GQ = 67;
    public static final int ER = 68;
    public static final int EE = 69;
    public static final int ET = 70;
    public static final int FK = 71;
    public static final int FO = 72;
    public static final int FJ = 73;
    public static final int FI = 74;
    public static final int FR = 75;
    public static final int GF = 76;
    public static final int PF = 77;
    public static final int TF = 78;
    public static final int GA = 79;
    public static final int GM = 80;
    public static final int GE = 81;
    public static final int DE = 82;
    public static final int GH = 83;
    public static final int GI = 84;
    public static final int GR = 85;
    public static final int GL = 86;
    public static final int GD = 87;
    public static final int GP = 88;
    public static final int GU = 89;
    public static final int GT = 90;
    public static final int GG = 91;
    public static final int GN = 92;
    public static final int GW = 93;
    public static final int GY = 94;
    public static final int HT = 95;
    public static final int HM = 96;
    public static final int VA = 97;
    public static final int HN = 98;
    public static final int HK = 99;
    public static final int HU = 100;
    public static final int IS = 101;
    public static final int IN = 102;
    public static final int ID = 103;
    public static final int IR = 104;
    public static final int IQ = 105;
    public static final int IE = 106;
    public static final int IM = 107;
    public static final int IL = 108;
    public static final int IT = 109;
    public static final int JM = 110;
    public static final int JP = 111;
    public static final int JE = 112;
    public static final int JO = 113;
    public static final int KZ = 114;
    public static final int KE = 115;
    public static final int KI = 116;
    public static final int KP = 117;
    public static final int KR = 118;
    public static final int KW = 119;
    public static final int KG = 120;
    public static final int LA = 121;
    public static final int LV = 122;
    public static final int LB = 123;
    public static final int LS = 124;
    public static final int LR = 125;
    public static final int LY = 126;
    public static final int LI = 127;
    public static final int LT = 128;
    public static final int LU = 129;
    public static final int MO = 130;
    public static final int MK = 131;
    public static final int MG = 132;
    public static final int MW = 133;
    public static final int MY = 134;
    public static final int MV = 135;
    public static final int ML = 136;
    public static final int MT = 137;
    public static final int MH = 138;
    public static final int MQ = 139;
    public static final int MR = 140;
    public static final int MU = 141;
    public static final int YT = 142;
    public static final int MX = 143;
    public static final int FM = 144;
    public static final int MD = 145;
    public static final int MC = 146;
    public static final int MN = 147;
    public static final int ME = 148;
    public static final int MS = 149;
    public static final int MA = 150;
    public static final int MZ = 151;
    public static final int MM = 152;
    public static final int NA = 153;
    public static final int NR = 154;
    public static final int NP = 155;
    public static final int NL = 156;
    public static final int NC = 157;
    public static final int NZ = 158;
    public static final int NI = 159;
    public static final int NE = 160;
    public static final int NG = 161;
    public static final int NU = 162;
    public static final int NF = 163;
    public static final int MP = 164;
    public static final int NO = 165;
    public static final int OM = 166;
    public static final int PK = 167;
    public static final int PW = 168;
    public static final int PS = 169;
    public static final int PA = 170;
    public static final int PG = 171;
    public static final int PY = 172;
    public static final int PE = 173;
    public static final int PH = 174;
    public static final int PN = 175;
    public static final int PL = 176;
    public static final int PT = 177;
    public static final int PR = 178;
    public static final int QA = 179;
    public static final int RE = 180;
    public static final int RO = 181;
    public static final int RU = 182;
    public static final int RW = 183;
    public static final int BL = 184;
    public static final int SH = 185;
    public static final int KN = 186;
    public static final int LC = 187;
    public static final int MF = 188;
    public static final int PM = 189;
    public static final int VC = 190;
    public static final int WS = 191;
    public static final int SM = 192;
    public static final int ST = 193;
    public static final int SA = 194;
    public static final int SN = 195;
    public static final int RS = 196;
    public static final int SC = 197;
    public static final int SL = 198;
    public static final int SG = 199;
    public static final int SX = 200;
    public static final int SK = 201;
    public static final int SI = 202;
    public static final int SB = 203;
    public static final int SO = 204;
    public static final int ZA = 205;
    public static final int GS = 206;
    public static final int SS = 207;
    public static final int ES = 208;
    public static final int LK = 209;
    public static final int SD = 210;
    public static final int SR = 211;
    public static final int SJ = 212;
    public static final int SZ = 213;
    public static final int SE = 214;
    public static final int CH = 215;
    public static final int SY = 216;
    public static final int TW = 217;
    public static final int TJ = 218;
    public static final int TZ = 219;
    public static final int TH = 220;
    public static final int TL = 221;
    public static final int TG = 222;
    public static final int TK = 223;
    public static final int TO = 224;
    public static final int TT = 225;
    public static final int TN = 226;
    public static final int TR = 227;
    public static final int TM = 228;
    public static final int TC = 229;
    public static final int TV = 230;
    public static final int UG = 231;
    public static final int UA = 232;
    public static final int AE = 233;
    public static final int GB = 234;
    public static final int US = 235;
    public static final int UM = 236;
    public static final int UY = 237;
    public static final int UZ = 238;
    public static final int VU = 239;
    public static final int VE = 240;
    public static final int VN = 241;
    public static final int VG = 242;
    public static final int VI = 243;
    public static final int WF = 244;
    public static final int EH = 245;
    public static final int YE = 246;
    public static final int ZM = 247;
    public static final int ZW = 248;
    public static final String HAS_RUN_BEFORE = "has_run_before";
}
