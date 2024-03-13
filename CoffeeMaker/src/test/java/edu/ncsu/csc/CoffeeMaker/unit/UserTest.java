/**
 *
 */
package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.models.enums.Role;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
class UserTest {

    @Test
    @Transactional
    void baseUserTest () {
        final User u1 = new User( "first", "last", "user", "password", Role.CUSTOMER );
        final User u2 = new User( "first", "last", "user", "password", Role.CUSTOMER );
        final User u3 = new User( "first1", "last", "user", "password", Role.CUSTOMER );
        final User u4 = new User( "first", "last1", "user", "password", Role.CUSTOMER );
        final User u5 = new User( "first", "last", "user1", "password", Role.CUSTOMER );
        final User u6 = new User( "first", "last", "user", "password1", Role.CUSTOMER );
        final User u7 = new User( "first", "last", "user", "password", Role.BARISTA );

        assertTrue( u1.hashCode() == u2.hashCode() );
        assertFalse( u1.hashCode() == u3.hashCode() );
        assertFalse( u1.hashCode() == u4.hashCode() );
        assertFalse( u1.hashCode() == u5.hashCode() );
        assertFalse( u1.hashCode() == u6.hashCode() );
        assertFalse( u1.hashCode() == u7.hashCode() );

        assertTrue( u1.equals( u2 ) );
        assertFalse( u1.equals( u3 ) );
        assertFalse( u1.equals( u4 ) );
        assertFalse( u1.equals( u5 ) );
        assertFalse( u1.equals( u6 ) );
        assertFalse( u1.equals( u7 ) );

        u2.setRole( Role.MANAGER );
        assertFalse( u1.equals( u2 ) );
    }

}
