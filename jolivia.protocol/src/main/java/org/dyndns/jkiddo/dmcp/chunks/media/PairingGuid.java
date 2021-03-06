package org.dyndns.jkiddo.dmcp.chunks.media;
import org.dyndns.jkiddo.dmp.chunks.RawChunk;

import org.dyndns.jkiddo.dmp.IDmapProtocolDefinition.DmapChunkDefinition;
import org.dyndns.jkiddo.dmp.DMAPAnnotation;

@DMAPAnnotation(type=DmapChunkDefinition.cmpg)
public class PairingGuid extends RawChunk {

	public PairingGuid()
	{
		this(new byte[] {});
	}

	public PairingGuid(byte[] value)
	{
		super("cmpg", "com.apple.itunes.unknown-pg", value);
	}
}