/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import edu.ncsu.csc.CoffeeMaker.models.enums.Role;

/**
 * Users are the main people within the system. They are assigned a role,
 * Customer, Barista, or Manager, and this designates the frontend stuff you are
 * allowed to see. This User Object, holds their name, username password and
 * role.
 *
 * @author Nolan Wright
 *
 */
@Entity
public class User extends DomainObject {

    private String  firstName;

    private String  lastName;

    private String  username;

    private String  password;

    @Enumerated ( EnumType.STRING )
    private Role    role;

    @Min ( 0 )
    @Max ( 1 )
    private Integer enabled;

    @Id
    @GeneratedValue
    private long    id;

    public User () {
        super();
    }

    /**
     * @param firstName
     *            First Name
     * @param lastName
     *            Last Name
     * @param username
     *            UserNames for the User
     * @param password
     *            Password for the User
     * @param role
     *            Role for the User
     */
    public User ( final String firstName, final String lastName, final String username, final String password,
            final Role role ) {
        setFirstName( firstName );
        setLastName( lastName );
        setUsername( username );
        setRole( role );
        setPassword( password );
        setEnabled( 1 );
    }

    @Override
    public Serializable getId () {
        return id;
    }

    public String getPassword () {
        return password;
    }

    public Integer isEnabled () {
        return enabled;
    }

    public void setEnabled ( final @Min ( 0 ) @Max ( 1 ) Integer enabled ) {
        this.enabled = enabled;
    }

    public Role getRole () {
        return role;
    }

    public String getFirstName () {
        return firstName;
    }

    public String getLastName () {
        return lastName;
    }

    public String getUsername () {
        return username;
    }

    /**
     * @param firstName
     *            the firstName to set
     */
    public void setFirstName ( final String firstName ) {
        this.firstName = firstName;
    }

    /**
     * @param lastName
     *            the lastName to set
     */
    public void setLastName ( final String lastName ) {
        this.lastName = lastName;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername ( final String username ) {
        this.username = username;
    }

    /**
     * @param role
     *            the role to set
     */
    public void setRole ( final Role role ) {
        this.role = role;
    }

    /**
     * @param password
     *            the password to set
     */
    private void setPassword ( final String password ) {
        this.password = password;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId ( final long id ) {
        this.id = id;
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( firstName == null ) ? 0 : firstName.hashCode() );
        result = prime * result + ( ( lastName == null ) ? 0 : lastName.hashCode() );
        result = prime * result + ( ( password == null ) ? 0 : password.hashCode() );
        result = prime * result + ( ( role == null ) ? 0 : role.hashCode() );
        result = prime * result + ( ( username == null ) ? 0 : username.hashCode() );
        return result;
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final User other = (User) obj;
        if ( firstName == null ) {
            if ( other.firstName != null ) {
                return false;
            }
        }
        else if ( !firstName.equals( other.firstName ) ) {
            return false;
        }
        if ( lastName == null ) {
            if ( other.lastName != null ) {
                return false;
            }
        }
        else if ( !lastName.equals( other.lastName ) ) {
            return false;
        }
        if ( password == null ) {
            if ( other.password != null ) {
                return false;
            }
        }
        else if ( !password.equals( other.password ) ) {
            return false;
        }
        if ( role != other.role ) {
            return false;
        }
        if ( username == null ) {
            if ( other.username != null ) {
                return false;
            }
        }
        else if ( !username.equals( other.username ) ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString () {
        // TODO Auto-generated method stub
        return username + " " + password + " " + enabled + " " + role;
    }
}
