package com.hellokoding.auth.service;

import com.hellokoding.auth.model.Role;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.repository.UserRepository;
import com.novell.ldap.LDAPConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	String pass = "";
		try {
			pass = ldapAuth(username);
			System.out.println(pass);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//        User user = userRepository.findByUsername(username);
//
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
//        for (Role role : user.getRoles()){
//            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
//        }
        
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(pass);

		
        return new org.springframework.security.core.userdetails.User(username, hashedPassword, grantedAuthorities);
    }
    
    public String ldapAuth(String username) throws NamingException, InterruptedException
	{
		String ldapUrl = "ldap://54.215.205.181:389";

		String serviceUserDN = "cn=admin,dc=myhclbg,dc=click";
		String serviceUserPassword = "admin";

		System.out.println("............................................................................................\n............................................................................................");
		System.out.println("Working on LDAP...");
		Thread.sleep(10000);

		Properties serviceEnv = new Properties();
		serviceEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		serviceEnv.put(Context.PROVIDER_URL, ldapUrl);
		serviceEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
		serviceEnv.put(Context.SECURITY_PRINCIPAL, serviceUserDN);
		serviceEnv.put(Context.SECURITY_CREDENTIALS, serviceUserPassword);
		InitialDirContext serviceCtx = new InitialDirContext(serviceEnv);

		String MY_ATTRS[] = {"cn", "uid", "sn", "userpassword"};

		Attributes ar = serviceCtx.getAttributes("cn=" + username + " " + username + ",cn=adGrp,dc=myhclbg,dc=click", MY_ATTRS);
		Attribute pwd = ar.get("userPassword");
        System.out.println("=> userPassword : " + new String((byte[])pwd.get()));
		return new String((byte[])pwd.get());
	}
}
