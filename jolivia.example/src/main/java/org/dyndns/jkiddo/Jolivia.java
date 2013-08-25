/*******************************************************************************
 * Copyright (c) 2013 Jens Kristian Villadsen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Jens Kristian Villadsen - Lead developer, owner and creator
 ******************************************************************************/
package org.dyndns.jkiddo;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TextArea;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.util.EnumSet;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.dyndns.jkiddo.dmap.chunks.audio.SongAlbum;
import org.dyndns.jkiddo.dmap.chunks.audio.SongArtist;
import org.dyndns.jkiddo.dmap.chunks.media.ItemName;
import org.dyndns.jkiddo.dmap.chunks.media.ListingItem;
import org.dyndns.jkiddo.guice.JoliviaServer;
import org.dyndns.jkiddo.jetty.extension.DmapConnector;
import org.dyndns.jkiddo.logic.desk.DeskMusicStoreReader;
import org.dyndns.jkiddo.logic.desk.GoogleStoreReader;
import org.dyndns.jkiddo.logic.interfaces.IImageStoreReader;
import org.dyndns.jkiddo.logic.interfaces.IMusicStoreReader;
import org.dyndns.jkiddo.raop.ISpeakerListener;
import org.dyndns.jkiddo.raop.server.IPlayingInformation;
import org.dyndns.jkiddo.service.daap.client.IClientSessionListener;
import org.dyndns.jkiddo.service.daap.client.Session;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.inject.servlet.GuiceFilter;

public class Jolivia
{
	static Logger logger = LoggerFactory.getLogger(Jolivia.class);

	public static void main(String[] args)
	{
		try
		{
			IMusicStoreReader reader = null;
			if(args.length == 2)
			{
				reader = new GoogleStoreReader(args[0], args[1]);
			}
			else
				reader = new DeskMusicStoreReader();
			new Jolivia.JoliviaBuilder().port(4000).pairingCode(1337).musicStoreReader(reader).build();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static class JoliviaBuilder
	{
		private Integer port = 4000;
		private Integer airplayPort = 5000;
		private Integer pairingCode = 1337;
		private String name = "Jolivia";
		private ISpeakerListener speakerListener;
		private IClientSessionListener clientSessionListener = new DefaultClientSessionListener();
		private IMusicStoreReader musicStoreReader = new DefaultMusicStoreReader();
		private IImageStoreReader imageStoreReader = new DefaultImageStoreReader();
		private IPlayingInformation iplayingInformation = new DefaultIPlayingInformation();

		public JoliviaBuilder port(int port)
		{
			this.port = port;
			return this;
		}

		public JoliviaBuilder pairingCode(int pairingCode)
		{
			this.pairingCode = pairingCode;
			return this;
		}

		public JoliviaBuilder airplayPort(int airplayPort)
		{
			this.airplayPort = airplayPort;
			return this;
		}

		public JoliviaBuilder name(String name)
		{
			this.name = name;
			return this;
		}

		public JoliviaBuilder musicStoreReader(IMusicStoreReader musicStoreReader)
		{
			this.musicStoreReader = musicStoreReader;
			return this;
		}

		public JoliviaBuilder imageStoreReader(IImageStoreReader imageStoreReader)
		{
			this.imageStoreReader = imageStoreReader;
			return this;
		}

		public JoliviaBuilder playingInformation(IPlayingInformation iplayingInformation)
		{
			this.iplayingInformation = iplayingInformation;
			return this;
		}

		public JoliviaBuilder clientSessionListener(IClientSessionListener clientSessionListener)
		{
			this.clientSessionListener = clientSessionListener;
			return this;
		}

		public Jolivia build() throws Exception
		{
			return new Jolivia(this);
		}

		class DefaultClientSessionListener implements IClientSessionListener
		{
			private Session session;

			@Override
			public void registerNewSession(Session session) throws Exception
			{
				this.session = session;
			}

			@Override
			public void tearDownSession(String server, int port)
			{
				try
				{
					session.logout();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		class DefaultIPlayingInformation implements IPlayingInformation
		{
			private JFrame frame;
			private JLabel label;

			public DefaultIPlayingInformation()
			{
				frame = new JFrame("Cover");
				label = new JLabel();
				frame.getContentPane().add(label, BorderLayout.CENTER);
				frame.pack();
				frame.setVisible(false);
			}

			@Override
			public void notify(BufferedImage image)
			{
				try
				{
					ImageIcon icon = new ImageIcon(image);
					label.setIcon(icon);
					frame.pack();
					frame.setSize(icon.getIconWidth(), icon.getIconHeight());
					frame.setVisible(true);
				}
				catch(Exception e)
				{
					logger.debug(e.getMessage(), e);
				}
			}

			@Override
			public void notify(ListingItem listingItem)
			{
				String title = listingItem.getSpecificChunk(ItemName.class).getValue();
				String artist = listingItem.getSpecificChunk(SongArtist.class).getValue();
				String album =  listingItem.getSpecificChunk(SongAlbum.class).getValue();
				frame.setTitle("Playing: " + title + " - " + album + " - " + artist);
			}
		}

		class DefaultImageStoreReader implements IImageStoreReader
		{
			@Override
			public Set<IImageItem> readImages() throws Exception
			{
				return Sets.newHashSet();
			}

			@Override
			public URI getImage(IImageItem image) throws Exception
			{
				return null;
			}

			@Override
			public byte[] getImageThumb(IImageItem image) throws Exception
			{
				return null;
			}
		}

		class DefaultMusicStoreReader implements IMusicStoreReader
		{
			@Override
			public Set<IMusicItem> readTunes() throws Exception
			{
				return Sets.newHashSet();
			}

			@Override
			public URI getTune(IMusicItem tune) throws Exception
			{
				return null;
			}
		}
	}

	private Jolivia(JoliviaBuilder builder) throws Exception
	{
		setupGui();
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		Preconditions.checkArgument(!(builder.pairingCode > 9999 || builder.pairingCode < 0), "Pairingcode must be expressed within 4 ciphers");
		logger.info("Starting " + builder.name + " on port " + builder.port);
		Server server = new Server(builder.port);
		// Server server = new
		// Server(InetSocketAddress.createUnresolved("0.0.0.0", port));
		Connector dmapConnector = new DmapConnector();
		dmapConnector.setPort(builder.port);
		// ServerConnector dmapConnector = new ServerConnector(server, new
		// DmapConnectionFactory());
		// dmapConnector.setPort(port);
		server.setConnectors(new Connector[] { dmapConnector });

		// Guice
		ServletContextHandler sch = new ServletContextHandler(server, "/");
		sch.addEventListener(new JoliviaServer(builder.port, builder.airplayPort, builder.pairingCode, builder.name, builder.clientSessionListener, builder.speakerListener, builder.imageStoreReader, builder.musicStoreReader, builder.iplayingInformation));
		sch.addFilter(GuiceFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
		sch.addServlet(DefaultServlet.class, "/");

		server.start();
		logger.info(builder.name + " started");
		server.join();
	}

	private void setupGui()
	{
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run()
			{
				onShutdown();
			}

		}));

		try
		{
			/* Create about dialog */
			final Dialog aboutDialog = new Dialog((Dialog) null);
			final GridBagLayout aboutLayout = new GridBagLayout();
			aboutDialog.setLayout(aboutLayout);
			aboutDialog.setVisible(false);
			aboutDialog.setTitle("About Jolivia");
			aboutDialog.setResizable(false);
			{
				/* Message */
				final TextArea title = new TextArea(AboutMessage.split("\n").length + 1, 64);
				title.setText(AboutMessage);
				title.setEditable(false);
				final GridBagConstraints titleConstraints = new GridBagConstraints();
				titleConstraints.gridx = 1;
				titleConstraints.gridy = 1;
				titleConstraints.fill = GridBagConstraints.HORIZONTAL;
				titleConstraints.insets = new Insets(0, 0, 0, 0);
				aboutLayout.setConstraints(title, titleConstraints);
				aboutDialog.add(title);
			}
			{
				/* Done button */
				final Button aboutDoneButton = new Button("Done");
				aboutDoneButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent evt)
					{
						aboutDialog.setVisible(false);
					}
				});
				final GridBagConstraints aboutDoneConstraints = new GridBagConstraints();
				aboutDoneConstraints.gridx = 1;
				aboutDoneConstraints.gridy = 2;
				aboutDoneConstraints.anchor = GridBagConstraints.PAGE_END;
				aboutDoneConstraints.fill = GridBagConstraints.NONE;
				aboutDoneConstraints.insets = new Insets(0, 0, 0, 0);
				aboutLayout.setConstraints(aboutDoneButton, aboutDoneConstraints);
				aboutDialog.add(aboutDoneButton);
			}
			aboutDialog.setVisible(false);
			aboutDialog.setLocationByPlatform(true);
			aboutDialog.pack();

			/* Create tray icon */
			final URL trayIconUrl = Jolivia.class.getClassLoader().getResource("icon_32.png");
			if(trayIconUrl == null)
			{
				throw new Exception("No image found");
			}
			TrayIcon trayIcon = new TrayIcon((new ImageIcon(trayIconUrl, "Jolivia").getImage()));
			trayIcon.setToolTip("Jolivia");
			trayIcon.setImageAutoSize(true);
			final PopupMenu popupMenu = new PopupMenu();
			final MenuItem aboutMenuItem = new MenuItem("About");
			aboutMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent evt)
				{
					aboutDialog.setLocationByPlatform(true);
					aboutDialog.setVisible(true);
				}
			});
			popupMenu.add(aboutMenuItem);
			final MenuItem exitMenuItem = new MenuItem("Quit");
			exitMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent evt)
				{
					onShutdown();
					System.exit(0);
				}
			});
			popupMenu.add(exitMenuItem);
			trayIcon.setPopupMenu(popupMenu);
			SystemTray.getSystemTray().add(trayIcon);

			logger.info("Running with GUI, created system tray icon and menu");
		}
		catch(Exception e)
		{
			logger.info("Running headless", e);
		}
	}

	protected void onShutdown()
	{
		// TODO Auto-generated method stub

	}

	private final String AboutMessage = "   * Jolivia *\n" + "\n" + "Copyright (c) 2013 Jens Kristian Villadsen\n" + "\n" + "Jolivia is free software: you can redistribute it and/or modify\n" + "it under the terms of the GNU General Public License as published by\n" + "the Free Software Foundation, either version 3 of the License, or\n" + "(at your option) any later version.\n" + "\n" + "didms is distributed in the hope that it will be useful,\n" + "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" + "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the\n" + "GNU General Public License for more details.\n" + "\n" + "You should have received a copy of the GNU General Public License\n" + "along with didms.  If not, see <http://www.gnu.org/licenses/>." + "\n\n";
	/*
	 * @Override public void update(Observable arg0, Object arg1) { if(trayIcon != null) { trayIcon.displayMessage(null, arg1.toString(), MessageType.INFO); } }
	 */
}