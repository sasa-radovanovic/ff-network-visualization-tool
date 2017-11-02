# Network Visualization Tool (ff-nvt)

# About application

[For actual wireframes from application follow this link](http://sasa-radovanovic.github.io/nvt.html)


Network visualization tool has 3 major parts:

 # 1. Schedule Visualization

Using NVT you can create schedule of your choice (i.e. this can be seasonal schedule of your airline) and have it presented either as static animation or as dynamic one. 

Static animation only shows routes on map (with additional airport data).

Dynamic animation can show you how network actually "breathes". You will have visualization of your network on weekly level (you can select speed of animation and starting point of the week - from Monday 00:00 UTC until Sunday 23:59 UTC). Note that dynamic visualization runs on UTC time, therefore your local departure time will be translated to UTC time (i.e. if your flight departs from Amsterdam on Monday at 00:30, in dynamic visualization that flight will depart on Sunday 23:30 during winter time and on 22:30 during summer time). Your flights will actually fly for number of minutes you specified and you can keep track of the progress in table above map. You can pause animation at any time, change animation speed, restart it, etc... 

This feature is especially useful if you are working for an airline and you are creating seasonal schedule since you can visualize all weekly trafic and perform some high-level analisys.

 # 2. Airline Network Visualization

Using NVT you can visualize network of all major airlines in the world and cross-compare up to 4 of them on a single map. You can even compare airline network against your own schedule/combination. 

 # 3. Airport Visualization and Stats
 
 Using NVT you can visualize all routes from a certain airport, access the list of operating carriers, compare the airport against others, but also get statistics of all airports in vicinity (i.e. If you select Seattle as target airport and radius of 700km, you can compare SEA airport with other airports in that circle), check wireframes for more details.

# Data

Application uses data from openflights.org and icons from daisycon.io.
Bear in mind that data might not be up to date (especially concerning airline routes and airlines operating from certain airport). I am not the owner of the data and you can always adapt files if you have up-to-date files.

# Software stack

Backend is implemented in Java 8 using Spring Boot 1.5 framework in combination with Hibernate.

Frontend is developed using VueJS 2.5, Vuetify framework and Google Maps v3 API. 


# Installation and setup

In order to run application you need to have following installed on your machine:

- Java 8
- Maven
- MySQL (or Postgresql [1])

[1] - If you plan on running the app against Postgresql make sure to change application.properties file

Create database "nvt" (or other, but make sure that application.properties file is in sync with your database)
By default application targets "nvt" database, Hibernate will create tables and application will fill airports/airlines/airline routes on first run.

- **Google Maps API key (in index.html make sure to change API KEY to your own if you plan on using application on a long run)**

After cloning project from repo, run
```sh
mvn clean install
```
Afterwards navigate to nvt-backend/target and just execute:
```sh
java -jar nvt-backend-1.0.0-SNAPSHOT.jar
```
# Development

If you want to run frontend independently, you need npm, node for that (check package.json for further details)

# Authors

I am the sole author of this application. 
Inspired by outgoing Fokker F70 from KLM fleet and for the sheer love of aviation.

sasa1kg@yahoo.com
sasa.radovanovic@live.com

# Licence 

MIT


