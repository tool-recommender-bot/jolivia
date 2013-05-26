package org.dyndns.jkiddo.service.dpap.server;

import java.io.IOException;
import java.util.HashMap;

import javax.jmdns.JmmDNS;
import javax.jmdns.ServiceInfo;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.dyndns.jkiddo.dmap.DmapUtil;
import org.dyndns.jkiddo.dmap.chunks.media.DatabaseCount;
import org.dyndns.jkiddo.dmap.chunks.media.ItemName;
import org.dyndns.jkiddo.dmap.chunks.media.LoginRequired;
import org.dyndns.jkiddo.dmap.chunks.media.ServerInfoResponse;
import org.dyndns.jkiddo.dmap.chunks.media.Status;
import org.dyndns.jkiddo.dmap.chunks.media.SupportsAutoLogout;
import org.dyndns.jkiddo.dmap.chunks.media.SupportsIndex;
import org.dyndns.jkiddo.dmap.chunks.media.TimeoutInterval;
import org.dyndns.jkiddo.service.dmap.DMAPResource;
import org.dyndns.jkiddo.service.dmap.Util;

import com.google.inject.Inject;
import com.google.inject.name.Named;

@Consumes(MediaType.WILDCARD)
//@Produces(MediaType.WILDCARD)
public class DPAPResource extends DMAPResource<ImageItemManager> implements IImageLibrary
{
	public static final String DPAP_SERVER_PORT_NAME = "DPAP_SERVER_PORT_NAME";
	public static final String DPAP_RESOURCE = "DPAP_IMPLEMENTATION";

	private static final String TXT_VERSION = "1";
	private static final String TXT_VERSION_KEY = "txtvers";
	private static final String DPAP_VERSION_KEY = "Version";
	private static final String MACHINE_ID_KEY = "Machine ID";
	private static final String IPSH_VERSION_KEY = "iPSh Version";
	private static final String PASSWORD_KEY = "Password";
	public static final String DAAP_PORT_NAME = "DAAP_PORT_NAME";

	@Inject
	public DPAPResource(JmmDNS mDNS, @Named(DPAP_SERVER_PORT_NAME) Integer port, @Named(Util.APPLICATION_NAME) String applicationName, @Named(DPAP_RESOURCE) ImageItemManager itemManager) throws IOException
	{
		super(mDNS, port, itemManager);
		this.name = applicationName;
		this.signUp();
	}

	@Override
	@Path("server-info")
	@GET
	public Response serverInfo(@Context HttpServletRequest httpServletRequest, @Context HttpServletResponse httpServletResponse) throws IOException
	{
		ServerInfoResponse serverInfoResponse = new ServerInfoResponse();
		serverInfoResponse.add(new Status(200));
		serverInfoResponse.add(itemManager.getDmapProtocolVersion());
		serverInfoResponse.add(itemManager.getProtocolVersion());
		serverInfoResponse.add(new ItemName(name));
		serverInfoResponse.add(new LoginRequired(false));
		serverInfoResponse.add(new TimeoutInterval(1800));
		serverInfoResponse.add(new SupportsAutoLogout(false));
		serverInfoResponse.add(new SupportsIndex(false));
		serverInfoResponse.add(new DatabaseCount(itemManager.getDatabases().size()));

		return Util.buildResponse(serverInfoResponse, itemManager.getDMAPKey(), name);
	}

	@Override
	protected ServiceInfo getServiceInfoToRegister()
	{
		String hash = Integer.toHexString(hostname.hashCode()).toUpperCase();
		hash = (hash + hash).substring(0, 13);
		HashMap<String, String> records = new HashMap<String, String>();
		records.put(TXT_VERSION_KEY, TXT_VERSION);
		records.put(DPAP_VERSION_KEY, DmapUtil.PPRO_VERSION_200 + "");
		records.put(IPSH_VERSION_KEY, DmapUtil.PPRO_VERSION_200 + "");
		records.put(MACHINE_ID_KEY, hash);
		records.put(PASSWORD_KEY, "0");
		return ServiceInfo.create(DPAP_SERVICE_TYPE, name, port, 0, 0, records);
	}

}