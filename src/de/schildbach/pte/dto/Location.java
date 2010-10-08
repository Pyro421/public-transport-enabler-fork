/*
 * Copyright 2010 the original author or authors.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.schildbach.pte.dto;

/**
 * @author Andreas Schildbach
 */
public final class Location
{
	public final LocationType type;
	public final int id;
	public final int lat, lon;
	public final String name;

	public Location(final LocationType type, final int id, final int lat, final int lon, final String name)
	{
		this.type = type;
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name; // invoked by AutoCompleteTextView in landscape orientation
	}

	public String toDebugString()
	{
		return "[" + type + " " + id + " '" + name + "']";
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
			return true;
		if (!(o instanceof Location))
			return false;
		final Location other = (Location) o;
		if (this.type != other.type)
			return false;
		if (this.id != other.id)
			return false;
		if (this.id != 0)
			return true;
		return this.name.equals(other.name);
	}

	@Override
	public int hashCode()
	{
		return type.hashCode(); // FIXME not very discriminative
	}
}