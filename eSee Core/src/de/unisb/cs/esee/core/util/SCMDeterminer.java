package de.unisb.cs.esee.core.util;

import org.eclipse.core.resources.IResource;

import de.unisb.cs.esee.core.SCMSystem;

public class SCMDeterminer {
    public static SCMSystem getSystem(IResource resource) {
	return SCMSystem.Subversive;
    }
}
