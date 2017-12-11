============================================
The marketprice-subscriber command line tool
============================================

This application allows you to subscribe from the command line to market prices
published on Thomson Reuters real-time platforms (TREP or Elektron). You can
subscribe either in streaming mode or in snapshot mode (without updates).
Fields values received for the market price are displayed on the consoled 
either in text or JSON format. The application accepts options and arguments
that allow you to set the name of the market price, the service name and the
DACS user name used to connect to the platform.
You can also activate the non verbose mode and redirect the console ourput
to a file so that it can be processed by another application or script.


Running the marketprice-Subscriber
----------------------------------

Before you start the application you must configure the EmaConfig.xml file
to specify the host name of the server of the TREP or Elektron platform to
which the marketprice-subscriber connects to. This is set thanks to the value
of the <ChannelGroup><ChannelList><Channel><Host> node. This value can be a
remote host name or IP address.

The marketprice-subscriber application also requires the JAVA_HOME environment
variable to be set. The minimum java version required is 1.8.0.0.

usage: marketprice-subscriber [-nv] [-wu] [-s service-name] [-u user-name]
                              marketprice-name
options:
 -j,--json-output-mode     Outputs images (a.k.a. Refresh) and updates in
                           JSON format.
 -nv,--non-verbose         Enables the non verbose mode. Only images
                           (a.k.a. Refresh) and updates are displayed.
 -s,--service-name <arg>   Elektron or TREP service name
                           Default value: ELEKTRON_DD
 -u,--user-name <arg>      DACS user name
                           Default value: System user name
 -wu,--with-updates        Enables updates. When this option activated,
                           the market price is opened in streaming mode.
                           The applications displays received images and
                           updates until it's terminated.
examples:
 marketprice-subscriber -nv -s ELEKTRON_DD EUR=
 marketprice-subscriber -nv -j -s ELEKTRON_DD JPY=

