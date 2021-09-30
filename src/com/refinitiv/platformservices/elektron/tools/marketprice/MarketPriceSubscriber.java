/*
 * Copyright 2017 Thomson Reuters
 *
 * DISCLAIMER: This source code has been written by Thomson Reuters for the only 
 * purpose of illustrating articles published on the Thomson Reuters Developer 
 * Community. It has not been tested for usage in production environments. 
 * Thomson Reuters cannot be held responsible for any issues that may happen if 
 * these objects or the related source code is used in production or any other 
 * client environment.
 *
 * Thomson Reuters Developer Community: https://developers.thomsonreuters.com
 *
 * Related Articles:
 *   A Simple MarketPrice object for EMA - Part 1: https://developers.thomsonreuters.com/article/simple-marketprice-object-ema-part-1
 *   A Simple MarketPrice object for EMA - Part 2: https://developers.thomsonreuters.com/article/simple-marketprice-object-ema-part-2
 *
 */
package com.refinitiv.platformservices.elektron.tools.marketprice;

import com.refinitiv.ema.access.DataType.DataTypes;
import com.refinitiv.ema.access.EmaFactory;
import com.refinitiv.ema.access.OmmConsumer;
import com.refinitiv.ema.access.OmmConsumerConfig;
import static com.refinitiv.ema.access.OmmConsumerConfig.OperationModel.USER_DISPATCH;
import com.refinitiv.ema.access.OmmDouble;
import com.refinitiv.ema.access.OmmException;
import com.refinitiv.ema.access.OmmFloat;
import com.refinitiv.ema.access.OmmInt;
import com.refinitiv.ema.access.OmmReal;
import com.refinitiv.ema.access.OmmState;
import com.refinitiv.ema.access.OmmUInt;
import com.refinitiv.platformservices.elektron.objects.common.Dispatcher;
import com.refinitiv.platformservices.elektron.objects.data.Field;
import com.refinitiv.platformservices.elektron.objects.marketprice.MarketPrice;
import static java.lang.System.exit;
import static java.lang.System.out;
import java.util.Collection;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;

/**
 * Main application class that implements the main method and the whole 
 * application logic from the arguments interpretation to the data output 
 * in text or JSON format.
 */
class MarketPriceSubscriber
{
    // TREP or Elektron Service name used to request the market price
    private static String serviceName = "ELEKTRON_DD";
    
    // Name of the market price instrument
    private static String instrumentName = "";
    
    // Data Access Control System (DACS) username/RRTO MashineId 
    private static String userName = "";
    
     //RRTO password keyfile keypassword clientid
    private static String password = "";
    private static String keyfile = "";
    private static String keypassword = "";
    private static String clientid = "";
    
    // Indicates if the chain should be oppened in Snapshot ro Streaming mode. 
    private static boolean withUpdates = false;    
    
    // Indicates if the verbose mode is enabled. 
    private static boolean verboseMode = true;    
    
    // Indicates if chain elements must be displayed in JSON format. 
    private static boolean jsonOutputMode = false;    

    // The OmmConsumer used to request the chains
    private static OmmConsumer ommConsumer;
    
    // The OmmConsumer dispatcher
    private static Dispatcher dispatcher;
        
    // Type of event related to the received fields
    private static final String IMAGE = "Refresh";
    private static final String UPDATE = "Update";   
    
    // Indicate if the object to print is the first since the applicaiton started
    // This is used to add commas to the JSON output 
    private static boolean firstObjectToPrint = true;
    
    /**
     * Main application method
     * @param args application arguments
     */
    public static void main(String[] args)
    {
        analyzeArguments(args);
        
        if(verboseMode)
            displayArgumentsAndOptions();
        
        if(verboseMode) 
            System.out.println("  >>> Connecting to the infrastructure...");        
        
        createOmmConsumerAndDispatcher();
        
        if(verboseMode) 
            System.out.println("  >>> Subscribing to the MarketPrice <" + instrumentName + ">. Please wait...");        
    
        printJsonBracketsIfRequired();
        
        MarketPrice theMarketPrice = createMarkePrice();
        
        theMarketPrice.open();
        
        if(withUpdates)
        {
            dispatchEventsUntilCtrlC();
        }
        else
        {
            dispatchEventsUntilComplete(theMarketPrice);
        }
        
        theMarketPrice.close();                    
        
        uninitializeOmmConsumer();
    }

    /**
     * Creates the <code>OmmConsumer</code> and the <code>Dispatcher</code> used 
     * by the application
     */
    private static void createOmmConsumerAndDispatcher()
    {
        boolean sessionMgmt = false;
        
        if(ommConsumer != null)
            return;
        
        try
        {
            OmmConsumerConfig config = EmaFactory.createOmmConsumerConfig()
                    .operationModel(USER_DISPATCH);
            
            if(!userName.isEmpty())
            {
                config.username(userName);
            }
            if(!keyfile.isEmpty())
            {
                sessionMgmt = true;
                config.tunnelingKeyStoreFile(keyfile);
		config.tunnelingSecurityProtocol("TLS");                       
            }
            if(!keypassword.isEmpty())
            {
                config.tunnelingKeyStorePasswd(keypassword);
            }
            if(!clientid.isEmpty())
            {
                config.clientId(clientid);
            }
            if(sessionMgmt) {
                ommConsumer = EmaFactory.createOmmConsumer(config.consumerName("Consumer_4").username(userName).password(password));

            } else {
                ommConsumer = EmaFactory.createOmmConsumer(config);
            }
            
            dispatcher = new Dispatcher.Builder()
                                .withOmmConsumer(ommConsumer)
                                .build();
        } 
        catch (OmmException exception)
        {
            System.out.println("      ERROR - Can't create the OmmConsumer because of the following error: " + exception.getMessage());
            exit(-1);
        }                        
    }
    
    /**
     * Prints JSON brackets if required by the output modes
     */        
    private static void printJsonBracketsIfRequired() 
    {
        if(!verboseMode && jsonOutputMode)
        {
            System.out.print("[");
            addShutdownHookForJsonClosingBrackets();
        }
    }

    /**
     * Setup, build and return the <code>MarketPrice</code> used by the application.
     * Implements the lambda expressions called on images, updates and states 
     * reception.
     * @return the build <code>MarketPrice</code>
     */        
    private static MarketPrice createMarkePrice() 
    {
        MarketPrice.OnImageFunction printImage = (marketPrice, image, state) -> 
        {
            if(verboseMode)
                System.out.println("  >>> New Image/Refresh received");

            print(marketPrice, IMAGE, image);
        };

        MarketPrice.OnUpdateFunction printUpdate = (marketPrice, update) -> 
        {
            if(verboseMode)
                System.out.println("  >>> New Update received");

            print(marketPrice, UPDATE, update);
            print(marketPrice, "ALLFIELDS", marketPrice.getFields());
        };

        MarketPrice.OnStateFunction printState = (marketPrice, state) -> 
        {
            if(verboseMode)
                System.out.println("  >>> New State received");

            print(marketPrice, state);
        };
        
        MarketPrice theMarketPrice = new MarketPrice.Builder()
                .withOmmConsumer(ommConsumer)
                .withName(instrumentName)
                .withServiceName(serviceName)
                .withUpdates(withUpdates)
                .onImage(printImage)
                .onUpdate(printUpdate)
                .onState(printState)
                .build();
        return theMarketPrice;
    }
    
    /**
     * Dispatch events until the <code>MarkePrice</code> is complete.
     * @param marketPrice the <code>MarkePrice</code>
     */
    private static void dispatchEventsUntilComplete(MarketPrice marketPrice)
    {
        try
        {
            dispatcher.dispatchEventsUntilComplete(marketPrice);
        } 
        catch (OmmException exception)
        {
            System.out.println("      ERROR - OmmConsumer event dispatching failed: " + exception.getMessage());
            exit(-1);
        }                
    }    
    
    /**
     * Dispatch events until the user presses &lt;Ctrl-c&gt;.
     */    
    private static void dispatchEventsUntilCtrlC()
    {
        try
        {
            dispatcher.dispatchEventsForever();
        } 
        catch (OmmException exception)
        {
            System.out.println("      ERROR - OmmConsumer event dispatching failed: " + exception.getMessage());
            exit(-1);
        }                
    }
    
    /**
     * Uninitialize the <code>OmmConsumer</code>.
     */
    private static void uninitializeOmmConsumer()
    {
        if(ommConsumer != null)
        {
            ommConsumer.uninitialize();        
            ommConsumer = null;
        }
    }    
    
    /**
     * Print a collection of fields received by a <code>MarkePrice</code>
     * @param marketPrice the <code>MarkePrice</code> that received the fields
     * @param eventType the type of the event that transported these fields
     * @param fields the fields to print
     */
    private static void print(MarketPrice marketPrice, String eventType, Collection<Field> fields)
    {
        if(jsonOutputMode)
        {
            printInJsonFormat(marketPrice, eventType, fields);
        }
        else
        {
            printInTextFormat(marketPrice, eventType, fields);
        }
    }
    
    /**
     * Print a <code>State</code> received by a <code>MarkePrice</code>
     * @param marketPrice the <code>MarkePrice</code> that received the state
     * @param fields the fields to print
     */
    private static void print(MarketPrice marketPrice, OmmState state)
    {
        if(jsonOutputMode)
        {
            printInJsonFormat(marketPrice, state);
        }
        else
        {
            printInTextFormat(marketPrice, state);
        }
    }    
    
    /**
     * Print a collection of fields received by a <code>MarkePrice</code> in text format.
     * @param marketPrice the <code>MarkePrice</code> that received these fields
     * @param eventType the type of the event that transported these fields
     * @param fields the fields to print
     */    
    private static void printInTextFormat(MarketPrice marketPrice, String eventType, Collection<Field> fields)
    {
        String tabs = "";
        if(verboseMode)
            tabs = "\t";    
        final String indent = tabs;
        
        out.println(indent + "Name        : " + marketPrice.getName());
        out.println(indent + "Service     : " + marketPrice.getServiceName());
        out.println(indent + "Type        : " + eventType);
        out.println(indent + "Id          : " + marketPrice.getStreamId());       
        out.println(indent + "Fields      : ");
        fields.forEach(
            (field) ->
                    
                out.println(indent + "\t" + field.description().acronym() + " (" + field.description().fid() + ") = " + field.value())
        );     
    }    
    
    /**
     * Print a collection of fields received by a <code>MarkePrice</code> in JSON format.
     * @param marketPrice the <code>MarkePrice</code> that received these fields
     * @param eventType the type of the event that transported these fields
     * @param fields the fields to print
     */       
    private static void printInJsonFormat(MarketPrice marketPrice, String eventType, Collection<Field> fields)
    {
        if(verboseMode)
            System.out.print("\t");
        else
            printCommaIfNotFirstObjectToPrint();
         
        JSONObject json = new JSONObject();
        json.put("Type", eventType);
        json.put("Id", marketPrice.getStreamId());
        json.put("Name", marketPrice.getName());  
        json.put("Service", marketPrice.getServiceName());
        
        JSONObject jsonFields = new JSONObject();
        fields.forEach(
            (field) ->
            {
                switch(field.value().dataType())
                {
                    case DataTypes.INT:
                        {
                            long value = ((OmmInt)field.value()).intValue();
                            jsonFields.put(field.description().acronym(), value);
                        }
                        break;
                    case DataTypes.UINT:
                        {
                            long value = ((OmmUInt)field.value()).longValue();
                            jsonFields.put(field.description().acronym(), value);
                        }
                        break;
                    case DataTypes.FLOAT:
                        {
                            double value = ((OmmFloat)field.value()).floatValue();
                            jsonFields.put(field.description().acronym(), value);
                        }
                        break;
                    case DataTypes.DOUBLE:
                        {
                            double value = ((OmmDouble)field.value()).doubleValue();
                            jsonFields.put(field.description().acronym(), value);
                        }
                        break;
                    case DataTypes.REAL:
                        {
                            double value = ((OmmReal)field.value()).asDouble();
                            jsonFields.put(field.description().acronym(), value);
                        }
                        break;
                    default:
                        jsonFields.put(field.description().acronym(), field.value());
                        break;
                }
                
            }
        ); 
        json.put("Fields", jsonFields);
        
        System.out.println(json);
    }
    
    /**
     * Print a <code>State</code> received by a <code>MarkePrice</code> in text format
     * @param marketPrice the <code>MarkePrice</code> that received the state
     * @param fields the fields to print
     */    
    private static void printInTextFormat(MarketPrice marketPrice, OmmState state)
    {
        String indent = "";
        
        if(verboseMode)
            indent = "\t";
        
        System.out.println(indent + "Name        : " + marketPrice.getName());
        System.out.println(indent + "Service     : " + marketPrice.getServiceName());
        System.out.println(indent + "Type        : State");
        System.out.println(indent + "Id          : " + marketPrice.getStreamId());       
        System.out.println(indent + "Stream state: " + state.streamStateAsString());
        System.out.println(indent + "Data state  : " + state.dataStateAsString());
        System.out.println(indent + "Status Text : " + state.statusText());    
    }    
    
    /**
     * Print a <code>State</code> received by a <code>MarkePrice</code> in JSON format
     * @param marketPrice the <code>MarkePrice</code> that received the state
     * @param fields the fields to print
     */    
    private static void printInJsonFormat(MarketPrice marketPrice, OmmState state)
    {
        if(verboseMode)
            System.out.print("\t");
        else
            printCommaIfNotFirstObjectToPrint();
        
        JSONObject json = new JSONObject();
        json.put("Type", "State");
        json.put("Id", marketPrice.getStreamId());
        json.put("Name", marketPrice.getName());  
        json.put("Service", marketPrice.getServiceName());
        
        JSONObject jsonState = new JSONObject();
        jsonState.put("Stream", state.streamStateAsString());
        jsonState.put("Data", state.dataStateAsString());
        jsonState.put("Text", state.statusText());
 
        json.put("State", jsonState);
        
        System.out.println(json);
    }
    
    /**
     * Analyze application's arguments
     * @param args the arguments to analyse
     */
    private static void analyzeArguments(String[] args)
    {        
        String syntax = "marketprice-subscriber [-nv] [-wu] [-s service-name] [-u user-name] [-p password] [-kf keyfile] [-kp keypassword] [-c clientid] marketprice-name";
        String header = "options:";

        Options options = new Options();

        Option serviceNameOption = new Option("s", "service-name", true, "Elektron or TREP service name\nDefault value: ELEKTRON_DD");
        serviceNameOption.setRequired(false);
        options.addOption(serviceNameOption);

        Option userNameOption = new Option("u", "user-name", true, "DACS user name or RRTO machineId\nDefault value: System user name");
        userNameOption.setRequired(false);
        options.addOption(userNameOption);
        
        Option passwordOption = new Option("p", "password", true, "RRTO password\nDefault value: no");
        passwordOption.setRequired(false);
        options.addOption(passwordOption);
        
        Option keyfileOption = new Option("kf", "keyfile", true, "keyfile");
        keyfileOption.setRequired(false);
        options.addOption(keyfileOption);
        
        Option keypasswordOption = new Option("kp", "keypassword", true, "keypassword");
        keypasswordOption.setRequired(false);
        options.addOption(keypasswordOption);
        
        Option clientidOption = new Option("c", "clientid", true, "clientid");
        clientidOption.setRequired(false);
        options.addOption(clientidOption);

        Option optimizationOption = new Option("wu", "with-updates", false, "Enables updates. When this option activated, the market price is opened in streaming mode. The applications displays received images and updates until it's terminated.");
        optimizationOption.setRequired(false);
        options.addOption(optimizationOption);

        Option nonVerboseOption = new Option("nv", "non-verbose", false, "Enables the non verbose mode. Only images (a.k.a. Refresh) and updates are displayed.");
        nonVerboseOption.setRequired(false);
        options.addOption(nonVerboseOption);

        Option jsonOutputOption = new Option("j", "json-output-mode", false, "Outputs images (a.k.a. Refresh) and updates in JSON format.");
        jsonOutputOption.setRequired(false);
        options.addOption(jsonOutputOption);

        String footer = "examples:\n" 
                      + " marketprice-subscriber -nv -s ELEKTRON_DD EUR=\n"
                      + " marketprice-subscriber -nv -j -s ELEKTRON_DD JPY=";
        
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try 
        {
            cmd = parser.parse(options, args);
        } 
        catch (ParseException e) 
        {
            System.out.println(e.getMessage());
            formatter.printHelp(syntax, header, options, footer);
            System.exit(1);
            return;
        }

        List<String> parsedArguments = cmd.getArgList();
        if(parsedArguments.size() != 1)
        {
            System.out.println("Missing required chain-name\n");
            formatter.printHelp(syntax, header, options, footer);
            System.exit(1);
            return;
        }
        
        instrumentName = parsedArguments.get(0);
        if(cmd.hasOption("service-name"))
        {
            serviceName = cmd.getOptionValue("service-name");
        }
        if(cmd.hasOption("user-name"))
        {        
            userName = cmd.getOptionValue("user-name");
        }
        if(cmd.hasOption("password"))
        {        
            password = cmd.getOptionValue("password");
        }
        if(cmd.hasOption("keyfile"))
        {        
            keyfile = cmd.getOptionValue("keyfile");
        }
        if(cmd.hasOption("keypassword"))
        {        
            keypassword = cmd.getOptionValue("keypassword");
        }
        if(cmd.hasOption("clientid"))
        {        
            clientid = cmd.getOptionValue("clientid");
        }
        if(cmd.hasOption("with-updates"))
        {        
            withUpdates = true;
        }
        if(cmd.hasOption("non-verbose"))
        {        
            verboseMode = false;
        }
        if(cmd.hasOption("json-output-mode"))
        {        
            jsonOutputMode = true;
        }
    }    
    
    /**
     * Display the application's arguments and options.
     */
    private static void displayArgumentsAndOptions()
    {
        System.out.println();
        System.out.println("  >>> Input arguments and options:");
        System.out.println("\tmarketprice-name: \"" + instrumentName + "\"");
        System.out.println("\tservice-name    : \"" + serviceName + "\"");
        System.out.println("\tuser-name       : \"" + userName + "\"");
        System.out.println("\tpassword       : \"" + password + "\"");
        System.out.println("\tkeyfile       : \"" + keyfile + "\"");
        System.out.println("\tkeypassword       : \"" + keypassword + "\"");
        System.out.println("\tclientid       : \"" + clientid + "\"");
        if(withUpdates)
        {
            System.out.println("\twith-updates    : enabled");       
        }
        else
        {
            System.out.println("\twith-updates    : disabled");    
        }
        if(verboseMode)
        {
            System.out.println("\tnon-verbose     : disabled");       
        }
        else
        {
            System.out.println("\tnon-verbose     : enabled");
        }
        if(jsonOutputMode)
        {
            System.out.println("\tjson-output-mode: enabled");       
        }
        else
        {
            System.out.println("\tjson-output-mode: disabled");    
        }
    }    
    
    /**
     * Add a shutdown hook to print a closing JSON brackets on exit.
     */
    private static void addShutdownHookForJsonClosingBrackets()
    {
        Runtime.getRuntime().addShutdownHook(new Thread() 
        {
            @Override
            public void run() 
            { 
                System.out.println("]");
            }
        });
    }
    
    /**
     * Print a comma if the JSON object to print is not the first one
     */
    private static void printCommaIfNotFirstObjectToPrint()
    {
        if(firstObjectToPrint) 
        {
            firstObjectToPrint = false;
            return;
        }
        System.out.print(",");
    }    
}
