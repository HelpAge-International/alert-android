package org.alertpreparedness.platform.alert

import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import org.alertpreparedness.platform.alert.risk_monitoring.model.CountryJsonData
import org.alertpreparedness.platform.alert.utils.Constants

/**
 * Created by fei on 14/11/2017.
 */

val HAZARD_EMPTY = 1
val HAZARD_NOT_EMPTY = 0

fun getLevel1Values(countryId: Int, mCountryDataList : ArrayList<CountryJsonData>) : List<String>? {
    val selectedCountry = mCountryDataList.first { countryJsonData -> countryJsonData.countryId == countryId }
    return selectedCountry.levelOneValues?.map { it.value }
}

fun getLevel2Values(countryId: Int, level1Id: Int, mCountryDataList : ArrayList<CountryJsonData>) : List<String>? {
    val selectedCountry = mCountryDataList.first { countryJsonData -> countryJsonData.countryId == countryId }
    return selectedCountry.levelOneValues?.first { it.id == level1Id }?.levelTwoValues?.map { it.value }
}

fun GetProgrammeSector(sector : Int) : String =
        when(sector) {
            0 -> {
                "Wash"
            }
            1 -> {
                "Health"
            }
            2 -> {
                "Shelter"
            }
            3 -> {
                "Nutrition"
            }
            4 -> {
                "Food Security and Livelihoods"
            }
            5 -> {
                "Protection"
            }
            6 -> {
                "Camp Management"
            }
            else -> {
                "Other"
            }
        }

fun GroupViewHolder.getHazardImg(title: String): Int =
        when (title) {
            "Cold Wave" -> {
                R.drawable.cold_wave
            }
            "Conflict" -> {
                R.drawable.conflict
            }
            "Cyclone" -> {
                R.drawable.cyclone
            }
            "Drought" -> {
                R.drawable.drought
            }
            "Earthquake" -> {
                R.drawable.earthquake
            }
            "Epidemic" -> {
                R.drawable.epidemic
            }
            "Fire" -> {
                R.drawable.fire
            }
            "Flash Flood" -> {
                R.drawable.flash_flood
            }
            "Flood" -> {
                R.drawable.flood
            }
            "Heat Wave" -> {
                R.drawable.heat_wave
            }
            "Heavy Rain" -> {
                R.drawable.heavy_rain
            }
            "Humanitarian Access" -> {
                R.drawable.humanitarian_access
            }
            "Insect Infestation" -> {
                R.drawable.insect_infestation
            }
            "Landslide" -> {
                R.drawable.landslide_mudslide
            }
            "Locust Infestation" -> {
                R.drawable.locust_infestation
            }
            "Mudslide" -> {
                R.drawable.landslide_mudslide
            }
            "Population Displacement" -> {
                R.drawable.population_displacement
            }
            "Population Return" -> {
                R.drawable.population_return
            }
            "Snow Avalanche" -> {
                R.drawable.snow_avalanche
            }
            "Snowfall" -> {
                R.drawable.snowfall
            }
            "Storm" -> {
                R.drawable.storm
            }
            "Storm Surge" -> {
                R.drawable.storm_surge
            }
            "Technological Disaster" -> {
                R.drawable.technological_disaster
            }
            "Tornado" -> {
                R.drawable.tornado
            }
            "Tsunami" -> {
                R.drawable.tsunami
            }
            "Violent Wind" -> {
                R.drawable.violent_wind
            }
            "Volcano" -> {
                R.drawable.volcano
            }
            else -> {
                R.drawable.other
            }
        }

fun getHazardTypes() : ArrayList<String> {

    var list : ArrayList<String> = arrayListOf(
            "Cold Wave",
            "Conflict",
            "Cyclone",
            "Drought",
            "Earthquake",
            "Epidemic",
            "Fire",
            "Flash Flood",
            "Flood",
            "Heat Wave",
            "Heavy Rain",
            "Humanitarian Access",
            "Insect Infestation",
            "Landslide",
            "Locust Infestation",
            "Mudslide",
            "Population Displacement",
            "Population Return",
            "Snow Avalanche",
            "Snowfall",
            "Storm",
            "Storm Surge",
            "Technological Disaster",
            "Tornado",
            "Tsunami",
            "Violent Wind",
            "Volcano",
            "Other"
    )

    return list
}

fun getHazardImg(title: String): Int =
        when (title) {
            "Cold Wave" -> {
                R.drawable.cold_wave
            }
            "Conflict" -> {
                R.drawable.conflict
            }
            "Cyclone" -> {
                R.drawable.cyclone
            }
            "Drought" -> {
                R.drawable.drought
            }
            "Earthquake" -> {
                R.drawable.earthquake
            }
            "Epidemic" -> {
                R.drawable.epidemic
            }
            "Fire" -> {
                R.drawable.fire
            }
            "Flash Flood" -> {
                R.drawable.flash_flood
            }
            "Flood" -> {
                R.drawable.flood
            }
            "Heat Wave" -> {
                R.drawable.heat_wave
            }
            "Heavy Rain" -> {
                R.drawable.heavy_rain
            }
            "Humanitarian Access" -> {
                R.drawable.humanitarian_access
            }
            "Insect Infestation" -> {
                R.drawable.insect_infestation
            }
            "Landslide" -> {
                R.drawable.landslide_mudslide
            }
            "Locust Infestation" -> {
                R.drawable.locust_infestation
            }
            "Mudslide" -> {
                R.drawable.landslide_mudslide
            }
            "Population Displacement" -> {
                R.drawable.population_displacement
            }
            "Population Return" -> {
                R.drawable.population_return
            }
            "Snow Avalanche" -> {
                R.drawable.snow_avalanche
            }
            "Snowfall" -> {
                R.drawable.snowfall
            }
            "Storm" -> {
                R.drawable.storm
            }
            "Storm Surge" -> {
                R.drawable.storm_surge
            }
            "Technological Disaster" -> {
                R.drawable.technological_disaster
            }
            "Tornado" -> {
                R.drawable.tornado
            }
            "Tsunami" -> {
                R.drawable.tsunami
            }
            "Violent Wind" -> {
                R.drawable.violent_wind
            }
            "Volcano" -> {
                R.drawable.volcano
            }
            else -> {
                R.drawable.other
            }
        }

fun GroupViewHolder.getCountryImage(location: Int): Int =
        when (location) {
            Constants.AF -> R.drawable.af
            Constants.AX -> R.drawable.other
            Constants.AL -> R.drawable.al
            Constants.DZ -> R.drawable.dz
            Constants.AS -> R.drawable.other
            Constants.AD -> R.drawable.other
            Constants.AO -> R.drawable.ao
            Constants.AI -> R.drawable.other
            Constants.AQ -> R.drawable.other
            Constants.AG -> R.drawable.other
            Constants.AR -> R.drawable.ar
            Constants.AM -> R.drawable.am
            Constants.AW -> R.drawable.other
            Constants.AU -> R.drawable.au
            Constants.AT -> R.drawable.at
            Constants.AZ -> R.drawable.az
            Constants.BS -> R.drawable.bs
            Constants.BH -> R.drawable.other
            Constants.BD -> R.drawable.bd
            Constants.BB -> R.drawable.other
            Constants.BY -> R.drawable.by
            Constants.BE -> R.drawable.be
            Constants.BZ -> R.drawable.bz
            Constants.BJ -> R.drawable.bj
            Constants.BM -> R.drawable.other
            Constants.BT -> R.drawable.bt
            Constants.BO -> R.drawable.bo
            Constants.BQ -> R.drawable.other
            Constants.BA -> R.drawable.other
            Constants.BW -> R.drawable.bw
            Constants.BV -> R.drawable.other
            Constants.BR -> R.drawable.br
            Constants.IO -> R.drawable.other
            Constants.BN -> R.drawable.bn
            Constants.BG -> R.drawable.bg
            Constants.BF -> R.drawable.bf
            Constants.BI -> R.drawable.bi
            Constants.KH -> R.drawable.kh
            Constants.CM -> R.drawable.cm
            Constants.CA -> R.drawable.ca
            Constants.CV -> R.drawable.other
            Constants.KY -> R.drawable.other
            Constants.CF -> R.drawable.cf
            Constants.TD -> R.drawable.td
            Constants.CL -> R.drawable.cl
            Constants.CN -> R.drawable.cn
            Constants.CX -> R.drawable.other
            Constants.CC -> R.drawable.other
            Constants.CO -> R.drawable.co
            Constants.KM -> R.drawable.other
            Constants.CG -> R.drawable.cg
            Constants.CD -> R.drawable.other
            Constants.CK -> R.drawable.other
            Constants.CR -> R.drawable.cr
            Constants.CI -> R.drawable.ci
            Constants.HR -> R.drawable.hr
            Constants.CU -> R.drawable.cu
            Constants.CW -> R.drawable.other
            Constants.CY -> R.drawable.cy
            Constants.CZ -> R.drawable.cz
            Constants.DK -> R.drawable.dk
            Constants.DJ -> R.drawable.dj
            Constants.DM -> R.drawable.other
            Constants.DOO-> R.drawable.doo
            Constants.EC -> R.drawable.ec
            Constants.EG -> R.drawable.eg
            Constants.SV -> R.drawable.sv
            Constants.GQ -> R.drawable.gq
            Constants.ER -> R.drawable.er
            Constants.EE -> R.drawable.ee
            Constants.ET -> R.drawable.et
            Constants.FK -> R.drawable.fk
            Constants.FO -> R.drawable.other
            Constants.FJ -> R.drawable.fj
            Constants.FI -> R.drawable.fi
            Constants.FR -> R.drawable.fr
            Constants.GF -> R.drawable.gf
            Constants.PF -> R.drawable.other
            Constants.TF -> R.drawable.tf
            Constants.GA -> R.drawable.ga
            Constants.GM -> R.drawable.gm
            Constants.GE -> R.drawable.ge
            Constants.DE -> R.drawable.de
            Constants.GH -> R.drawable.gh
            Constants.GI -> R.drawable.other
            Constants.GR -> R.drawable.gr
            Constants.GL -> R.drawable.gl
            Constants.GD -> R.drawable.other
            Constants.GP -> R.drawable.other
            Constants.GU -> R.drawable.other
            Constants.GT -> R.drawable.gt
            Constants.GG -> R.drawable.other
            Constants.GN -> R.drawable.gn
            Constants.GW -> R.drawable.gw
            Constants.GY -> R.drawable.gy
            Constants.HT -> R.drawable.ht
            Constants.HM -> R.drawable.other
            Constants.VA -> R.drawable.other
            Constants.HN -> R.drawable.hn
            Constants.HK -> R.drawable.other
            Constants.HU -> R.drawable.hu
            Constants.IS -> R.drawable.iss
            Constants.IN -> R.drawable.inn
            Constants.ID -> R.drawable.id
            Constants.IR -> R.drawable.ir
            Constants.IQ -> R.drawable.iq
            Constants.IE -> R.drawable.ie
            Constants.IM -> R.drawable.other
            Constants.IL -> R.drawable.il
            Constants.IT -> R.drawable.it
            Constants.JM -> R.drawable.jm
            Constants.JP -> R.drawable.jp
            Constants.JE -> R.drawable.other
            Constants.JO -> R.drawable.jo
            Constants.KZ -> R.drawable.kz
            Constants.KE -> R.drawable.ke
            Constants.KI -> R.drawable.other
            Constants.KP -> R.drawable.kp
            Constants.KR -> R.drawable.kr
            Constants.KW -> R.drawable.kw
            Constants.KG -> R.drawable.kg
            Constants.LA -> R.drawable.la
            Constants.LV -> R.drawable.lv
            Constants.LB -> R.drawable.lb
            Constants.LS -> R.drawable.ls
            Constants.LR -> R.drawable.lr
            Constants.LY -> R.drawable.ly
            Constants.LI -> R.drawable.other
            Constants.LT -> R.drawable.lt
            Constants.LU -> R.drawable.lu
            Constants.MO -> R.drawable.other
            Constants.MK -> R.drawable.mk
            Constants.MG -> R.drawable.mg
            Constants.MW -> R.drawable.mw
            Constants.MY -> R.drawable.my
            Constants.MV -> R.drawable.other
            Constants.ML -> R.drawable.ml
            Constants.MT -> R.drawable.other
            Constants.MH -> R.drawable.other
            Constants.MQ -> R.drawable.other
            Constants.MR -> R.drawable.mr
            Constants.MU -> R.drawable.other
            Constants.YT -> R.drawable.other
            Constants.MX -> R.drawable.mx
            Constants.FM -> R.drawable.other
            Constants.MD -> R.drawable.md
            Constants.MC -> R.drawable.other
            Constants.MN -> R.drawable.mn
            Constants.ME -> R.drawable.me
            Constants.MS -> R.drawable.other
            Constants.MA -> R.drawable.ma
            Constants.MZ -> R.drawable.mz
            Constants.MM -> R.drawable.mm
            Constants.NA -> R.drawable.na
            Constants.NR -> R.drawable.other
            Constants.NP -> R.drawable.np
            Constants.NL -> R.drawable.nl
            Constants.NC -> R.drawable.nc
            Constants.NZ -> R.drawable.nz
            Constants.NI -> R.drawable.ni
            Constants.NE -> R.drawable.ne
            Constants.NG -> R.drawable.ng
            Constants.NU -> R.drawable.other
            Constants.NF -> R.drawable.other
            Constants.MP -> R.drawable.other
            Constants.NO -> R.drawable.no
            Constants.OM -> R.drawable.om
            Constants.PK -> R.drawable.pk
            Constants.PW -> R.drawable.other
            Constants.PS -> R.drawable.ps
            Constants.PA -> R.drawable.pa
            Constants.PG -> R.drawable.pg
            Constants.PY -> R.drawable.py
            Constants.PE -> R.drawable.pe
            Constants.PH -> R.drawable.ph
            Constants.PN -> R.drawable.other
            Constants.PL -> R.drawable.pl
            Constants.PT -> R.drawable.pt
            Constants.PR -> R.drawable.pr
            Constants.QA -> R.drawable.qa
            Constants.RE -> R.drawable.other
            Constants.RO -> R.drawable.ro
            Constants.RU -> R.drawable.ru
            Constants.RW -> R.drawable.rw
            Constants.BL -> R.drawable.other
            Constants.SH -> R.drawable.other
            Constants.KN -> R.drawable.other
            Constants.LC -> R.drawable.other
            Constants.MF -> R.drawable.other
            Constants.PM -> R.drawable.other
            Constants.VC -> R.drawable.other
            Constants.WS -> R.drawable.other
            Constants.SM -> R.drawable.other
            Constants.ST -> R.drawable.other
            Constants.SA -> R.drawable.sa
            Constants.SN -> R.drawable.sn
            Constants.RS -> R.drawable.rs
            Constants.SC -> R.drawable.other
            Constants.SL -> R.drawable.sl
            Constants.SG -> R.drawable.other
            Constants.SX -> R.drawable.other
            Constants.SK -> R.drawable.sk
            Constants.SI -> R.drawable.si
            Constants.SB -> R.drawable.sb
            Constants.SO -> R.drawable.so
            Constants.ZA -> R.drawable.za
            Constants.GS -> R.drawable.other
            Constants.SS -> R.drawable.ss
            Constants.ES -> R.drawable.es
            Constants.LK -> R.drawable.lk
            Constants.SD -> R.drawable.sd
            Constants.SR -> R.drawable.sr
            Constants.SJ -> R.drawable.sj
            Constants.SZ -> R.drawable.sz
            Constants.SE -> R.drawable.se
            Constants.CH -> R.drawable.ch
            Constants.SY -> R.drawable.sy
            Constants.TW -> R.drawable.tw
            Constants.TJ -> R.drawable.tj
            Constants.TZ -> R.drawable.tz
            Constants.TH -> R.drawable.th
            Constants.TL -> R.drawable.tl
            Constants.TG -> R.drawable.tg
            Constants.TK -> R.drawable.other
            Constants.TO -> R.drawable.other
            Constants.TT -> R.drawable.tt
            Constants.TN -> R.drawable.tn
            Constants.TR -> R.drawable.tr
            Constants.TM -> R.drawable.tm
            Constants.TC -> R.drawable.other
            Constants.TV -> R.drawable.other
            Constants.UG -> R.drawable.ug
            Constants.UA -> R.drawable.ua
            Constants.AE -> R.drawable.ae
            Constants.GB -> R.drawable.gb
            Constants.US -> R.drawable.us
            Constants.UM -> R.drawable.other
            Constants.UY -> R.drawable.uy
            Constants.UZ -> R.drawable.uz
            Constants.VU -> R.drawable.vu
            Constants.VE -> R.drawable.ve
            Constants.VN -> R.drawable.vn
            Constants.VG -> R.drawable.other
            Constants.VI -> R.drawable.other
            Constants.WF -> R.drawable.other
            Constants.EH -> R.drawable.eh
            Constants.YE -> R.drawable.ye
            Constants.ZM -> R.drawable.zm
            Constants.ZW -> R.drawable.zw
            else -> R.drawable.other
        }