jolivia
=======

A Java application/library implementation of the DMAP family (DAAP, DACP, DPAP) and RAOP with Guice + Jetty + Zeroconf/Bonojur (through [jmDNS](http://sourceforge.net/projects/jmdns/)). The functionality is planned to resemble what is provided by eg. [dmapd](http://www.flyn.org/projects/dmapd/index.html). It should however be thought of as an integration library, not a final application itself. jolivia is scoped to support the following proprietary protocols:

 - Digital Media Access Protocol (DMAP)
 - Digital Audio Access Protocol (DAAP)
 - Digital Photo Access Protocol (DPAP)
 - Digital Audio Control Protocol (DACP)
 - Remote Audio Output Protocol (RAOP)

## Q/A (How do I ... -)

##### Q: How do I use the DACP feature?
##### A:
I'll just give you a brief introduction to how the DACP protocol works (you can read more on [http://jsharkey.org/blog/2009/06/21/itunes-dacp-pairing-hash-is-broken/](http://jsharkey.org/blog/2009/06/21/itunes-dacp-pairing-hash-is-broken/), [http://dacp.jsharkey.org/](http://dacp.jsharkey.org/), [http://jinxidoru.blogspot.dk/2009/06/itunes-remote-pairing-code.html](http://jinxidoru.blogspot.dk/2009/06/itunes-remote-pairing-code.html) and [http://www.awilco.net/doku/dacp](http://www.awilco.net/doku/dacp)).

1. The client side of DACP announces itself through [Bonjour](http://en.wikipedia.org/wiki/Bonjour_(software) ). This is done by Jolivia
2. The announcement is 'caught/read' by iTunes. At this point you should be able to see the 'like to pair?'-button in iTunes
3. iTunes makes a request to the webservice that the client is required to implement. This service is implemented by Jolivia.
4. The service responds with a OK/NotOK if the correct pairing code is submitted and a negotiation key is stored in both Jolivia and iTunes. This is implemented by Jolivia.
5. iTunes announces itself through Bonjour as paired.
6. Joliva detects the iTunes announcement. If the iTunes library/user is detected as a paired iTunes, Jolivia initiates a session and calls the registered ClientSessionListener that is registered in the constuctor of Jolivia.

The session is your 'remote control' instance. On a session you can do the remote control stuff or eg. traverse the library. See the following code example:

		new Jolivia(new IClientSessionListener() {

			@Override
			public void tearDownSession(String server, int port) {
				// TODO Auto-generated method stub

			}

			@Override
			public void registerNewSession(Session session) throws Exception {
				RemoteControl remoteControl = session.getRemoteControl();
				Library library = session.getLibrary();

				// Now do stuff :)
				remoteControl.play();
				remoteControl.getNowPlaying();
				library.getAllAlbums();
			}
		});
	}

## Current functionality ##

 * DAAP share as provided by iTunes including Zeroconf service discovery/publication.
 * DACP pairing and remote control functions. Jolivia implements serverside and clientside, meaning that you can use Jolivia for remote control but you can also use eg. Apple Remote against it.
 * RAOP Streaming aka. Airport Express emulation.
## Planned functionality ##

 * RAOP could be implemented as in [RPlay](https://github.com/bencall/RPlay), [AirReceiver](https://github.com/fgp/AirReceiver), [AP4J-Player](https://github.com/carsonmcdonald/AP4J-Player), [qtunes](https://launchpad.net/qtunes) or [JAirPort](https://github.com/froks/JAirPort).
 * DPAP implementation (ongoing ... ).

## Far future functionality ##
 * DLNA/DMAP Gateway translation.

## Inspiration
This project has found great inspiration in many projects and the people behind them, among those are the following:

 - [TunesRemote+](http://code.google.com/p/tunesremote-plus/)
 - [TunesRemote SE](http://code.google.com/p/tunesremote-se/)
 - [jems - Java Extensible Media Server](http://code.google.com/p/jems/)
 - [ytrack](http://code.google.com/p/ytrack/)
 - [RPlay](https://github.com/bencall/RPlay)
 - [AirReceiver](https://github.com/fgp/AirReceiver)
 - [AP4J-Player](https://github.com/carsonmcdonald/AP4J-Player)
 - [JAirPort](https://github.com/froks/JAirPort)
 - [rkapsi/daap](https://github.com/rkapsi/daap)
 - [qtunes](https://launchpad.net/qtunes)

This project is licensed under the license presented in the license.txt file.

Copyright 2013 [Jens Kristian Villadsen](http://www.genuswillehadus.net). 
