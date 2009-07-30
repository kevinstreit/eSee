package esee.core.util;

import org.eclipse.core.resources.IResource;

import esee.core.SCMSystem;


public class SCMDeterminer {
	public static SCMSystem getSystem(IResource resource) {
		return SCMSystem.Subversive;
	}
}
