1 Project objective and scope

The AirENAC airline has launched the SkySplit project consisting in the separation of baggage and passengers travels. In order to be profitable, this change of paradigm involves a great number of changes in the management of passengers and, more importantly, of luggage. AirENAC has issued a call for tenders to implement the SkySplit project. The call for tenders is organized into sub-projects. One of them consists of the design and manufacturing of a new remotely piloted cargo aircraft.
You work for Planenac, the aircraft manufacturer that has won this cargo aircraft sub-project of the call for tenders. You and your team are in charge of the design and creation of the system that will allow a remote pilot to maneuver the aircraft and communicate with it.

You need to provide a first, very basic, proof of concept before the end of June to the client, focusing only on operational phases. In this version the remote-pilot will control only one aircraft at a time.

The system is split into two separate sub-systems connected by datalink:

1 A Ground System that allows the remote pilot to send guidance orders to the aircraft and provides situational awareness. The graphical interface of the Ground Tool consists of two different parts:

• The first part, mainly dedicated to situational awareness (aircraft location, planned route, surrounding traffic...), will have the look of a kind of navigation display (ND), as the example showed in Figure 1. In this document we will refer to this interface as ND.

• The second part will display information related to aircraft status (altitude, speed, vertical speed, heading...) and guidance modes and targets. It will also allow the pilot to transmit guidance orders and other requests to the aircraft through data- link messages.

Figure 1: Look of a standard navigation display (ND)

 2 A compatible Airborne System, based on the avionics of an existing manned aircraft (referred to as “Baseline Avionics” in this document), that processes and executes the guidance orders commanded from the Ground system. It will also downlink the aircraft data that will be displayed by the Ground System for situational awareness.
Concerning the exchanges between the Ground System and the Airborne System:

• A new application APDLC (Aircraft Pilot Data-link Communications), based on the preformatted messages
used in CPDLC, will be used to exchange data between the aircraft and the remote pilot

• A new application Automatic Aircraft Reporting (AAR), based on the principles of ADS-C application will
be used to transmit data from the aircraft, notably for situational awareness

For this first prototypic version, you are expected to address only the operational scenarios described in Section 2 and provide:
• An analysis of the operational need

• A security analysis of the Global System (Ground System + Airborne System + Datalink) 

• A design, functional architecture and implementation of the Airborne System

• A design and implementation of a first prototype of the Ground System
