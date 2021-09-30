# MarketPriceSubscriber example

## Table of Content

* [Overview](#overview)

* [Disclaimer](#disclaimer)

* [Prerequisites](#prerequisites)

* [Applications design](#application-design)

* [Building the _MarkePriceSubscriber_ and the _ValueAddObjectsForEMA_](#building-the-marketpricesubscriber)

* [Running the _MarkePriceSubscriber_](#running-the-marketpricesubscriber)

* [Running the _MarketPriceStepByStepExample_](#running-the-marketpricestepbystepexample)

* [Troubleshooting](#troubleshooting)

* [Solution Code](#solution-code)

## <a id="overview"></a>Overview
This project is one of the many learning materials published by Refinitiv to help developers learning Refintiv APIs. It contains two Java example applications and the _ValueAddObjectsForEMA_ example library that demonstrate the different concepts explained in the [A simple MarketPrice object for EMA](https://developers.refinitiv.com/en/article-catalog/article/simple-marketprice-object-ema-part-1) article published on the [Refinitiv Developer Community portal](https://developers.refinitiv.com). These applications are based on the Java edition of the Enterprise Message API that is one of the APIs of the Refinitiv Real-Time SDK. Please consult this [Refinitiv Real-Time Java SDK page](https://developers.refinitiv.com/en/api-catalog/refinitiv-real-time-opnsrc/rt-sdk-java) for learning materials and documentation about this API.

For any question or comment related to this article please use the _ADD YOUR COMMENT_ section at the bottom of this page or post a question on the [EMA Q&A Forum](https://community.developers.refinitiv.com/spaces/72/index.html) of the Developer Community.

_**Note:** To be able to ask questions and to benefit from the full content available on the [Refinitiv Developer Community portal](https://developers.refinitiv.com) we recommend you to [register here](https://developers.refinitiv.com/en/register) or [login here]( https://community.developers.refinitiv.com/users/login.html)._

## <a id="disclaimer"></a>Disclaimer
The example applications presented here and the _ValueAddObjectsForEMA_ example library have been written by Refinitiv for the only purpose of illustrating articles published on the Refinitiv Developer Community. These example applications and the _ValueAddObjectsForEMA_ example library have not been tested for a usage in production environments. Refinitiv cannot be held responsible for any issues that may happen if these example applications, the _ValueAddObjectsForEMA_ library or the related source code is used in production or any other client environment.

## <a id="prerequisites"></a>Prerequisites

Required software components:

* [Refinitiv Real-Time SDK](https://developers.refinitiv.com/en/api-catalog/refinitiv-real-time-opnsrc/rt-sdk-java) (2.0.2 or greater) - Refinitiv interface to the Real-Time Market Data environment
* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - Java Development Kit - version 8

## <a id="application-design"></a> Applications design
The source code of these example applications has been designed for easy reuse in other example applications. It is made of three distinct parts:

### The *_ValueAddObjectsForEMA_* library
This module implements the complete logic and algorithms explained in the [A simple MarketPrice object for EMA - Part1](https://developers.refinitiv.com/en/article-catalog/article/simple-marketprice-object-ema-part-1) article. These features are implemented by the _MarketPrice_ objects defined by the _com.refinitiv.platformservices.rt.objects.marketprice_ package. The source code of this package is reused by other Refinitiv example applications. The *_ValueAddObjectsForEMA_* example library provides other reusable objects presented by other articles published on the Refinitiv Developer Community.   

For more details about the _MarketPrice_ objects usage, please refer to the [A simple MarketPrice object for EMA - Part2](https://developers.refinitiv.com/en/article-catalog/article/simple-marketprice-object-ema-part-2) article and the *_ValueAddObjectsForEMA_* javadoc. 

### The _MarketPriceStepByStepExample_ application
This example application that is part of the *_ValueAddObjectsForEMA_* library demonstrates the MarketPrice objects capabilities and how to use them. The application starts by creating an EMA OmmConsumer and uses it in with MarketPrice objects in several individual steps that demonstrate the implemented features. Before each step, explanatory text is displayed and you are prompted to press to start the step.

### The _MarketPriceSubscriber_ tool

The _MarketPriceSubscriber_ is a command line tool you can use to retrieve market prices in text or JSON format. The application accepts options and arguments that allow you to set the instrument name, the service name and the DACS user name. You can switch the application to a non verbose mode and redirect the output (fields and values) to a file so that it can be processed by another application or script. This is a good example of a simple but real application that relies on the _ValueAddObjectsForEMA_ library and the _MarketPrice_ class.

## <a id="building-the-marketpricesubscriber" name="building-the-marketpricesubscriber"></a>Building the _MarkePriceSubscriber_ and the _ValueAddObjectsForEMA_

### Make sure the _ValueAddObjectsForEMA_ submodule is downloaded

Before you start building the project make sure the _ValueAddObjectsForEMA_ submodule is properly downloaded. To this aim list the files of the _./ValueAddObjectsForEMA_ directory. This directory should contain several script and source files. If it's empty, please run the following Git commands in the root directory of your _MarkePriceSubscriber_ project: 

    git submodule init
    git submodule update

### Change the service name and DACS user name if need be

The *MarketPriceStepByStepExample.java* file of the *_ValueAddObjectsForEMA_* contains two hardcoded values that you may want to change depending on your the RTDS or Real-Time platform you use. These values indicate:

* The **service name** used to subscribe to chains records: The hardcoded value is "ELEKTRON_DD". This value can be changed thanks to the *MarketPriceStepByStepExample.serviceName* class member in the *MarketPriceStepByStepExample.java* file.  
* The **DACS user name** used to connect the application to the infrastructure. If the Data Access Control System (DACS) is activated on your RTDS and if your DACS username is different than your operating system user name, you will need to set it thanks to the *MarketPriceStepByStepExample.dacsUserName* class member of the *MarketPriceStepByStepExample.java* file.

The _MarketPriceSubscriber_ application doesn't need these changes as the **service name** and the  **DACS user name** can be passed as command line arguments of the application.

**You can use the NetBeans IDE to build the applications. NetBeans 8.2 project files are provided with the applications source code.**

## <a id="running-the-marketpricesubscriber"></a>Running the _MarketPriceSubscriber_

**Before you start the application** you must configure the *EmaConfig.xml file* to specify the host name of the server (the RTDS or Real-Time platform) to which the EMA connects. This is set thanks to the value of the *\<ChannelGroup>\<ChannelList>\<Channel>\<Host>* node. This value can be a remote host name or IP address.

To run the _MarketPriceSubscriber_ you will need to provide the following command line parameters in your Netbeans environment (or if running via the console / script file)

### Usage

    usage: marketprice-subscriber [-nv] [-wu] [-s service-name] [-u user-name]
                                marketprice-name
    options:
    -j,--json-output-mode     Outputs images (a.k.a. Refresh) and updates in
                            JSON format.
    -nv,--non-verbose         Enables the non verbose mode. Only images
                            (a.k.a. Refresh) and updates are displayed.
    -s,--service-name <arg>   Real-Time or RTDS service name
                            Default value: ELEKTRON_DD
    -u,--user-name <arg>      DACS user name
                            Default value: System user name
    -wu,--with-updates        Enables updates. When this option activated,
                            the market price is opened in streaming mode.
                            The applications displays received images and
                            updates until it's terminated.

### Examples

    > marketprice-subscriber -s ELEKTRON_DD IBM.N

    > marketprice-subscriber -nv -s ELEKTRON_DD EUR=

    > marketprice-subscriber -nv -j -s ELEKTRON_DD JPY=

### Expected output

This is an example of the _MarketPriceSubscriber_ output with non-verbose and text modes activated:

    > marketprice-subscriber -nv -s ELEKTRON_DD EUR=

    Name        : JPY=
    Service     : ELEKTRON_DD
    Type        : Refresh
    Id          : 5
    Fields      : 
        PROD_PERM (1) = 213
        RDNDISPLAY (2) = 153
        DSPLY_NAME (3) = MDM BANK     MOW
        TIMACT (5) = 08:44:00:000:000:000
        NETCHNG_1 (11) = 0.29
        HIGH_1 (12) = 113.19
        LOW_1 (13) = 112.67
        CURRENCY (15) = JPY
        ACTIV_DATE (17) =  3 OCT 2017 
        OPEN_PRC (19) = 112.73
        HST_CLOSE (21) = 112.75
        BID (22) = 113.04
        BID_1 (23) = 113.05
        BID_2 (24) = 113.05
        ASK (25) = 113.06
        ASK_1 (26) = 113.06
        ASK_2 (27) = 113.06
        ACVOL_1 (32) = 50777.0
        TRD_UNITS (53) = 2DP 
        PCTCHNG (56) = 0.26
        OPEN_BID (57) = 112.73
        OPEN_ASK (59) = 112.76
        CLOSE_BID (60) = 112.75
        CLOSE_ASK (61) = 112.78
        NUM_MOVES (77) = 88534.0
        OFFCL_CODE (78) = MDMB        
        HSTCLSDATE (79) =  2 OCT 2017 
            .
            .
            .
        MIDLO1_MS (14202) = 22:58:41:068:000:000
        BID_HR_MS (14208) = 08:00:00:948:000:000

This is the same example with the json mode activated:

    > marketprice-subscriber -nv -j -s ELEKTRON_DD EUR=

    [{"Type":"Refresh","Fields":{"GV1_TIME":"08:00:00:000:000:000","ASIA_CL_DT":" 3 OCT 2017 ","AMERHI_BID":112.88,"HST_OPNBID":112.54,"MTH_BHI_DT":" 3 OCT 2017 ","EURO_AO_MS":"05:00:01:354:000:000","RDNDISPLAY":153,"YRASKHIGH":118.64,"OPEN_TYPE":"B ","SPS_SP_RIC":".[SPSEVAI-VAH10-P4","HSTCLBDDAT":" 2 OCT 2017 ","US_LW_TM":"13:38:00:000:000:000","PCTCHG_3M":-0.3,"PCTCHNG":0.25,"AMEROP_ASK":112.79,"CLOSE_ASK":112.78,"YRHIGHDAT":" 3 JAN 2017 ","HST_NUMBID":88534,"WK_ALO_DT":" 1 OCT 2017 ","QUOTIM_3":"08:43:11:000:000:000","QUOTIM_2":"08:43:11:000:000:000","GEN_TEXT16":"<JPY/BKGDINFO>","BIDHI3_MS":"03:49:18:031:000:000","YRASKLOW":107.33,"MTH_BID_HI":113.19,"GV5_TEXT":"OP_BID","US_OPEN":112.76,"ASKLO3_MS":"22:29:38:157:000:000","OPEN_ASK":112.76,"US_AO_MS":"11:00:01:854:000:000","EUROP_ASK":113.16,"NETCHNG_1":0.28,"ASIALO_ASK":112.67,"GV9_TEXT":"ASK_LO","ASIA_CLOSE":113.08,"USBAC_DT":" 2 OCT 2017 ","AMERLO_ASK":112.54,"ASK":113.05,"GN_TXT24_1":"<0#JPYF=>","YRBIDLOW":107.31,"MID_SPREAD":112.5,"PREF_DISP":6205,"ASIACL_BID":113.08,"ASIA_BL_MS":"22:58:41:068:000:000","ASKHI4_MS":"01:57:57:761:000:000","SEC_LOW":112.67,"MONTH_LOW":112.48,"ASIAHI_BID":113.19,"WKHI_DT":" 3 OCT 2017 ","EURLO_BID":112.9,"ASK_1":113.06,"ASIA_HI_TM":"05:19:00:000:000:000","BID_LOW_3":112.66,"BID_LOW_4":112.67,"ASK_2":113.06,"BID_LOW_1":112.63,"BIDLO3_MS":"22:46:29:135:000:000","YRLOW":107.33,"EURO_LOW":112.93,"BID_LOW_2":112.65,"BID_LOW_5":112.68,"EURO_BL_MS":"06:31:24:212:000:000","LOWTP_1":"A","AMERCL_BID":112.75,"CTB_PAGE2":"RBSL","RDN_EXCHD2":"NY$","CTB_PAGE1":"ASAN","HST_CLOSE":112.75,"CTB_PAGE3":"BCFX","EURO_OPEN":113.13,"PRIMACT_3":113.03,"ASKHI3_MS":"05:09:51:441:000:000","GV10_TEXT":"OP_ASK","QUOTE_DT3":" 3 OCT 2017 ","HST_OPNASK":112.57,"BID_ASK_DT":" 2 OCT 2017 ","BIDHI2_MS":"05:10:18:927:000:000","BID_1":113.03,"AMERHI_ASK":112.92,"CLOSE_TYPE":"B ","EURCL_BID":112.6,"START_DT":" 5 OCT 2017 ","BIDYRLODAT":" 8 SEP 2017 ","TIMCOR":"22:58:41:000:000:000","MTHHI_DT":" 3 OCT 2017 ","US_BL_MS":"14:33:43:051:000:000","EURO_BO_MS":"05:00:01:354:000:000","DSPLY_NAME":"ASANPACIFIBK MOW","BID_2":113.03,"AMEROP_BID":112.76,"BIDLO4_MS":"21:06:26:490:000:000","PCTCHG_YTD":-3.29,"ASKLO2_MS":"22:57:31:232:000:000","MTHLO_DT":" 1 OCT 2017 ","MID_HIGH":113.21,"CLOSE_BID":112.75,"BID_TICK_1":"�","ASIA_HIGH":113.19,"QUOTE_DATE":" 3 OCT 2017 ","US_CLOSE":112.75,"DLG_CODE3":"      ","SEC_HI_TP":"A","IRGPRC":0.25,"DLG_CODE1":"ASAN  ","DLG_CODE2":"RBSL  ","YRHIGH":118.6,"ASIABAC_DT":" 3 OCT 2017 ","BID_HIGH_2":113.18,"BID_HIGH_1":113.19,"PRV_ASK_H":113.08,"BID_NET_CH":0.28,"PRV_ASK_L":112.48,"BID_HIGH_4":113.16,"BID_HIGH_3":113.17,"QUOTIM_MS":31392522,"BID_HIGH_5":113.15,"ASIA_AL_MS":"22:58:13:879:000:000","NUM_MOVES":88534,"GEN_VAL10":112.57,"DSO_ID":16416,"BID":113.03,"CURRENCY":"JPY","QUOTIM":"08:43:12:000:000:000","ASKLO1_MS":"22:58:13:879:000:000","OPN_BID_MS":"21:00:01:668:000:000","EURO_NETCH":0.43,"BIDHI1_MS":"05:19:30:895:000:000","ASIAHI_ASK":113.22,"BIDPCTCHNG":0.25,"ASIA_BH_MS":"05:19:30:895:000:000","GV4_TEXT":"SPOT","BR_LINK3":"","MIDHI1_MS":"05:20:11:050:000:000","BR_LINK1":"","BR_LINK2":"","WKLO_DT":" 1 OCT 2017 ","BID_FWDOR":0,"EUROP_BID":113.13,"BID_SPREAD":112.9,"CONTEXT_ID":3312,"GEN_VAL8":113.08,"GEN_VAL9":112.48,"ASIA_OP_TM":"21:00:00:000:000:000","QUOTE_DT2":" 3 OCT 2017 ","GEN_VAL6":113.05,"GEN_VAL7":112.43,"PRIMACT_1":113.03,"GEN_VAL4":113.08,"PCTCHG_6M":1.95,"ACTIV_DATE":" 3 OCT 2017 ","PRIMACT_2":113.03,"GEN_VAL5":112.54,"GEN_VAL3":112.76,"WK_BID_HI":113.19,"EURCL_ASK":112.63,"BID_HOURLY":113.08,"US_HI_TM":"11:48:00:000:000:000","BKGD_REF":"Japanese Yen","ASIA_LW_TM":"22:58:00:000:000:000","EUBAC_DT":" 2 OCT 2017 ","HIGH_TIME4":"01:57:00:000:000:000","HIGH_TIME5":"01:57:00:000:000:000","GV6_TEXT":"BID_HI","HIGH_TIME2":"05:10:00:000:000:000","HIGH_TIME3":"03:49:00:000:000:000","US_BH_MS":"11:48:39:170:000:000","ASIA_LOW":112.67,"US_CL_DT":" 2 OCT 2017 ","ASK_HI_TME":"05:11:00:000:000:000","MID_HTIM":"05:20:11:000:000:000","PROD_PERM":213,"VALUE_TS3":"08:43:11:000:000:000","MID_LOW":112.66,"VALUE_TS2":"08:43:11:000:000:000","BID_HR_MS":"08:00:00:948:000:000","VALUE_TS1":"08:43:12:000:000:000","ASKHI2_MS":"05:10:01:450:000:000","SC_ACT_TP2":" A","SC_ACT_TP3":" A","YRLOWDAT":" 8 SEP 2017 ","MTH_ALO_DT":" 1 OCT 2017 ","SC_ACT_TP1":" A","ASIAOP_BID":112.73,"SEC_HIGH":113.22,"US_NETCH":0.28,"VALUE_DT3":" 3 OCT 2017 ","VALUE_DT2":" 3 OCT 2017 ","VALUE_DT1":" 3 OCT 2017 ","WK_BHI_DT":" 3 OCT 2017 ","BIDLO5_MS":"21:06:04:665:000:000","ASK_HIGH_1":113.22,"ASKYRHIDAT":" 3 JAN 2017 ","ASK_HIGH_2":113.21,"ASKYRLODAT":" 8 SEP 2017 ","ASK_HIGH_3":113.2,"HST_CLSBID":112.75,"ASK_HIGH_4":113.19,"ASK_HIGH_5":113.18,"GV8_TEXT":"ASK_HI","EURHI_ASK":113.22,"CTBTR_2":"RBS         ","PRV_BID_H":113.05,"ASKHI1_MS":"05:11:01:464:000:000","CTBTR_3":"BARCLAYS    ","FXMM_TYPE":"        ","CTBTR_1":"ASANPACIFIBK","EURO_HI_TM":"05:19:00:000:000:000","PRV_BID_L":112.43,"LOW_TIME":"22:58:00:000:000:000","ASIA_BO_MS":"21:00:01:668:000:000","WEEK_HIGH":113.19,"MTH_ASK_LO":112.48,"ASIA_AH_MS":"05:11:01:463:000:000","US_AL_MS":"13:38:11:825:000:000","ACVOL_1":50660,"EURO_AH_MS":"05:11:01:463:000:000","HST_HIMID":113.06,"RCS_AS_CL2":"                        ","BIDHI5_MS":"01:57:31:760:000:000","ASK_SPREAD":112.63,"ASKLO5_MS":"21:06:04:665:000:000","YRBIDHIGH":118.6,"PREV_DISP":60,"NUM_BIDS":50660,"TIMACT":"08:43:00:000:000:000","BCKGRNDPAG":"ASAN","EURO_OP_TM":"05:00:00:000:000:000","MIDLO1_MS":"22:58:41:068:000:000","HIGHTP_1":"B","HST_LOMID":112.46,"EURO_HIGH":113.19,"EURLO_ASK":112.93,"BASE_CCY":"USD","US_BNC":0.28,"ASIA_BNC":0.11,"OPEN_TIME":"21:00:00:000:000:000","GN_TXT16_2":"<JPYVOL>","ASK_FWDOR":0,"ASK_LO_TME":"22:58:00:000:000:000","EURO_CLOSE":112.6,"ASIAOP_ASK":112.76,"OPEN_PRC":112.73,"MID_LTIM":"22:58:41:000:000:000","PCTCHG_MTD":0.5,"EURO_BNC":0.43,"LOW_1":112.67,"HSTCLSDATE":" 2 OCT 2017 ","US_OP_TM":"11:00:00:000:000:000","LOW_2":112.68,"BIDYRHIDAT":" 3 JAN 2017 ","MONTH_HIGH":113.19,"MID_NET_CH":0.27,"GV2_TEXT":"USDJPY","BIDLO1_MS":"22:58:41:068:000:000","SEC_LO_TP":"B","US_BO_MS":"11:00:01:854:000:000","LOW_3":112.69,"LOW_4":112.7,"LOW_5":112.71,"WK_ASK_LO":112.48,"ASK_LOW_5":112.71,"BCAST_REF":"JP-(FX|1)","EURHI_BID":113.19,"HIGH_TIME":"05:19:00:000:000:000","ASK_LOW_2":112.68,"ASK_LOW_1":112.67,"ASK_LOW_4":112.7,"ASK_LOW_3":112.69,"AMERCL_ASK":112.78,"MID_OPEN":112.75,"ASIA_OPEN":112.73,"OPEN_BID":112.73,"RECORDTYPE":209,"MID_PRICE":113.04,"LOW_TIME2":"22:57:00:000:000:000","BIDLO2_MS":"22:51:54:324:000:000","LOW_TIME3":"22:29:00:000:000:000","US_HIGH":112.88,"BIDHI4_MS":"01:57:55:266:000:000","HIGH_4":113.16,"HIGH_3":113.17,"HIGH_2":113.18,"LOW_TIME4":"21:06:00:000:000:000","SCALING":"1","HIGH_1":113.19,"LOW_TIME5":"21:06:00:000:000:000","CTB_LOC2":"LON","CTB_LOC3":"LON","US_LOW":112.54,"AMERLO_BID":112.5,"ASKHI5_MS":"01:57:33:952:000:000","ASKLO4_MS":"21:06:26:490:000:000","HIGH_5":113.15,"ASIALO_BID":112.63,"GV1_TEXT":"SPOT  ","MID_CLOSE":112.77,"GV7_TEXT":"BID_LO","SEC_ACT_1":113.05,"SEC_ACT_2":113.06,"ASIA_NETCH":0.11,"US_AH_MS":"14:05:04:564:000:000","CTB_LOC1":"MOW","DDS_DSO_ID":12348,"OFFCL_CODE":"ASAN        ","SEC_ACT_3":113.06,"EURO_AL_MS":"06:31:25:859:000:000","ASIA_AO_MS":"21:00:03:420:000:000","EURO_CL_DT":" 2 OCT 2017 ","WEEK_LOW":112.48,"ACT_TP_3":"B�","CROSS_SC":"1E+00","ACT_TP_2":"B�","ACT_TP_1":"B�","EURO_BH_MS":"05:19:30:895:000:000","TRD_UNITS":"2DP ","ASIACL_ASK":113.11,"EURO_LW_TM":"06:31:00:000:000:000"},"Service":"ELEKTRON_DD","Id":5,"Name":"JPY="}
    ]

## <a id="running-the-marketpricestepbystepexample"></a>Running the _MarketPriceStepByStepExample_

Please refer to the related [README](https://github.com/Refinitiv-API-Samples/Example.EMA.Java.ValueAddObjectsForEMA/blob/master/README.md) file of the _ValueAddObjectsForEMA_.

## <a id="troubleshooting"></a>Troubleshooting

**Q: When I build the application, I get a "ValueAddObjectsForEMA submodule is missing or incomplete" error**

**A:** The _ValueAddObjectsForEMA_ submodule that is required by the _Article.EMA.Java.MarketPriceSubscriber_ project is not downloaded or is incomplete. To download the _ValueAddObjectsForEMA_ submodule, exetute the following Git command from the directory where you cloned the _Article.EMA.Java.MarketPriceSubscriber_ repository. Like this:

    git submodule init
    git submodule update

<br>

**Q: The application is stuck after the *">>> Connecting to the infrastructure..."* message is displayed.**

After a while the application displays an error like: 

      ERROR - Can't create the OmmConsumer because of the following error: login failed (timed out after waiting 45000 milliseconds) for 10.2.43.49:14002)

**A:** Verify that you properly set the *<host>* parameter in the EmaConfig.xml file (see [Running the MarketPriceSubscriber](#running-the-marketpricesubscriber) for more). 
Ultimately, ask your RTDS administrator to help you to investigate with RTDS monitoring tools like adsmon.

 
## <a id="solution-code"></a>Solution Code

The _MarketPriceSubscriber_ and the _ValueAddObjectsForEMA_ have been developed using the [Real-Time SDK Java API](https://developers.refinitiv.com/en/api-catalog/refinitiv-real-time-opnsrc/rt-sdk-java) that is available for download [here](https://developers.refinitiv.com/en/api-catalog/refinitiv-real-time-opnsrc/rt-sdk-java/download).

### Built With

* [Refinitiv RT-SDK Java](https://developers.refinitiv.com/en/api-catalog/refinitiv-real-time-opnsrc/rt-sdk-java)
* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [NetBeans](https://netbeans.org/) - IDE for Java development

### <a id="contributing"></a>Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

### <a id="authors"></a>Authors

* **Olivier Davant** - Release 1.0.  *Initial version*

### <a id="license"></a>License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
