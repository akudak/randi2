/* 
 * (c) 2008- RANDI2 Core Development Team
 * 
 * This file is part of RANDI2.
 * 
 * RANDI2 is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * RANDI2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * RANDI2. If not, see <http://www.gnu.org/licenses/>.
 */
package de.randi2.model.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import lombok.Data;

import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.acls.model.UnloadedSidException;
import org.springframework.util.Assert;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

@Entity
@NamedQuery(name = "acl.findAclByObjectIdentityAndSid", query = "select acl from AclHibernate acl where acl.owner.sidname = ? and acl.objectIdentity.identifier = ? and acl.objectIdentity.type = ?")
@Data
@SuppressWarnings
public class AclHibernate implements Acl, Serializable {

	private static final long serialVersionUID = 253176536526673664L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;
	@ManyToOne(targetEntity = AclHibernate.class)
	private Acl parentAcl;
	@ManyToOne(cascade = CascadeType.ALL)
	private ObjectIdentityHibernate objectIdentity;
	@OneToMany(mappedBy = "acl", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<AccessControlEntryHibernate> aces = new ArrayList<AccessControlEntryHibernate>();
	@ManyToOne
	private SidHibernate owner;
	private boolean entriesInheriting = true;
	//private String roleName;
	@Transient
	private Sid[] loadedSids = null;

	@Override
	public List<AccessControlEntry> getEntries() {
		return new ArrayList<AccessControlEntry>(aces);
	}


	@Override
	public boolean isGranted(List<Permission> permission, List<Sid> sids, boolean administrativeMode)
			throws NotFoundException, UnloadedSidException {
		Assert.notEmpty(permission, "Permissions required");
		Assert.notEmpty(sids, "SIDs required");

		if (!this.isSidLoaded(sids)) {
			throw new UnloadedSidException("ACL was not loaded for one or more SID");
		}

		AccessControlEntry firstRejection = null;

		for (int i = 0; i < permission.size(); i++) {
			for (int x = 0; x < sids.size(); x++) {
				// Attempt to find exact match for this permission mask and SID
				Iterator<AccessControlEntryHibernate> acesIterator = aces.iterator();
				boolean scanNextSid = true;
				while (acesIterator.hasNext()) {
					AccessControlEntry ace =  acesIterator.next();
					if ((ace.getPermission().getMask() == permission.get(i).getMask()) && ace.getSid().equals(sids.get(x))) {
						// Found a matching ACE, so its authorization decision will prevail
						if (ace.isGranting()) {
							// Success
							//if (!administrativeMode) {
							//   auditLogger.logIfNeeded(true, ace);
							//}

							return true;
						} else {
							// Failure for this permission, so stop search
							// We will see if they have a different permission
							// (this permission is 100% rejected for this SID)
							if (firstRejection == null) {
								// Store first rejection for auditing reasons
								firstRejection = ace;
							}

							scanNextSid = false; // helps break the loop

							break; // exit "aceIterator" while loop
						}
					}
				}

				if (!scanNextSid) {
					break; // exit SID for loop (now try next permission)
				}
			}
		}

		if (firstRejection != null) {
			// We found an ACE to reject the request at this point, as no
			// other ACEs were found that granted a different permission
			//if (!administrativeMode) {
			//  auditLogger.logIfNeeded(false, firstRejection);
			//}

			return false;
		}

		// No matches have been found so far
		if (isEntriesInheriting() && (parentAcl != null)) {
			// We have a parent, so let them try to find a matching ACE
			return parentAcl.isGranted(permission, sids, false);
		} else {
			// We either have no parent, or we're the uppermost parent
			throw new NotFoundException("Unable to locate a matching ACE for passed permissions and SIDs");
		}
	}

	@Override
	public boolean isSidLoaded(List<Sid> sids) {
		// If loadedSides is null, this indicates all SIDs were loaded
		// Also return true if the caller didn't specify a SID to find
		if ((this.loadedSids == null) || (sids == null) || (sids.size() == 0)) {
			return true;
		}

		// This ACL applies to a SID subset only. Iterate to check it applies.
		for (Sid sid : sids) {
			boolean found = false;

			for (int y = 0; y < this.loadedSids.length; y++) {
				if (sid.equals(this.loadedSids[y])) {
					// this SID is OK
					found = true;

					break; // out of loadedSids for loop
				}
			}

			if (!found) {
				return false;
			}
		}

		return true;
	}

	public void insertAce(PermissionHibernate permission, String roleName) {
		AccessControlEntryHibernate ace = new AccessControlEntryHibernate();
		ace.setAcl(this);
		ace.setPermission(permission);
		ace.setSid(owner);
		ace.setRoleName(roleName);
		aces.add(ace);
	}
}
