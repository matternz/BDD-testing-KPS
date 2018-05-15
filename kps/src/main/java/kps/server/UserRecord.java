package kps.server;

import java.io.Serializable;

import kps.server.UserRecord.Role;

public class UserRecord implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum Role {
		CLERK,
		MANAGER
	}

	private String username;
	private String password;
	private Role role;

	public UserRecord(String username, String password, Role role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getUsername() {
		return this.username;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserRecord other = (UserRecord) obj;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}
