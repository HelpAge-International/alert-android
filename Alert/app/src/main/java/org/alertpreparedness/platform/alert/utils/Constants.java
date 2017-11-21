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

    //INDICATOR-FREQUENCY

    //user type
    public static final int CountryDirector = 1;
    public static final int CountryAdmin = 2;
    public static final int ErtLeader = 3;
    public static final int Ert = 4;
    public static final int PartnerUser = 5;

    //copy over from portal
//    public static final int HazardScenario0 = 0;
//    public static final int HazardScenario1 = 1;
//    public static final int HazardScenario2 = 2;
//    public static final int HazardScenario3 = 3;
//    public static final int HazardScenario4 = 4;
//    public static final int HazardScenario5 = 5;
//    public static final int HazardScenario6 = 6;
//    public static final int HazardScenario7 = 7;
//    public static final int HazardScenario8 = 8;
//    public static final int HazardScenario9 = 9;
//    public static final int HazardScenario10 = 10;
//    public static final int HazardScenario11 = 11;
//    public static final int HazardScenario12 = 12;
//    public static final int HazardScenario13 = 13;
//    public static final int HazardScenario14 = 14;
//    public static final int HazardScenario15 = 15;
//    public static final int HazardScenario16 = 16;
//    public static final int HazardScenario17 = 17;
//    public static final int HazardScenario18 = 18;
//    public static final int HazardScenario19 = 19;
//    public static final int HazardScenario20 = 20;
//    public static final int HazardScenario21 = 21;
//    public static final int HazardScenario22 = 22;
//    public static final int HazardScenario23 = 23;
//    public static final int HazardScenario24 = 24;
//    public static final int HazardScenario25 = 25;
//    public static final int HazardScenario26 = 26;
//    public static final int[] HAZARD_SCENARIO = {HazardScenario0, HazardScenario1, HazardScenario2, HazardScenario3, HazardScenario4, HazardScenario5, HazardScenario6, HazardScenario7, HazardScenario8, HazardScenario9, HazardScenario10, HazardScenario11,
//            HazardScenario12, HazardScenario13, HazardScenario14, HazardScenario15, HazardScenario16, HazardScenario17, HazardScenario18, HazardScenario19, HazardScenario20, HazardScenario21, HazardScenario22, HazardScenario23, HazardScenario24, HazardScenario25, HazardScenario26};

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
}
