package PlanetaryDefense.PD.web;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;

import PlanetaryDefense.PD.driver.MysqlDriver;

@Component
public class CustomLdapAuthoritiesPopulator
implements LdapAuthoritiesPopulator {
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
      System.out.println(username);
      HashSet<SimpleGrantedAuthority> gas = new HashSet<SimpleGrantedAuthority>();
      MysqlDriver mysql = new MysqlDriver();
      mysql.getConnection();
      List<String> roles = mysql.getRoleByUser(username);        
      for (String role : roles) {
            gas.add(new SimpleGrantedAuthority(role));
      }
      gas.add(new SimpleGrantedAuthority("ROLE_USER"));
      return gas;
    }
}
