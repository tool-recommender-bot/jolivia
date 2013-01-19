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
package org.dyndns.jkiddo.protocol.dmap.chunks.com.apple.itunes;

import org.dyndns.jkiddo.protocol.dmap.chunks.UByteChunk;

public class SpecialPlaylist extends UByteChunk
{
	public SpecialPlaylist()
	{
		this(0);
	}

	public SpecialPlaylist(int mode)
	{
		super("aePS", "com.apple.itunes.special-playlist", mode);
	}
}