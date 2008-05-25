package de.randi2test.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.util.GregorianCalendar;
import java.util.Random;
import java.util.Vector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import de.randi2.dao.CenterDao;
import de.randi2.dao.LoginDao;
import de.randi2.dao.PersonDao;
import de.randi2.dao.TrialDao;
import de.randi2.model.Center;
import de.randi2.model.Login;
import de.randi2.model.Person;
import de.randi2.model.PersonRole;
import de.randi2.model.Righ;
import de.randi2.model.Role;
import de.randi2.model.Trial;
import de.randi2.model.enumerations.Gender;
import de.randi2.model.enumerations.Titel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/de/randi2/applicationContext.xml"})
@Transactional
public class LoginDaoTest {

	@Autowired
	private LoginDao loginDao;
	
	@Autowired
	private CenterDao centerDao;
	
	@Autowired
	private PersonDao personDao;
	
	@Autowired
	private TrialDao trialDao;
	
	@Test
	public void CreateAndSaveTest(){
		
		//Step one - creating a new person object
		Person p = new Person();
		p.setFirstname("test");
		p.setSurname("test");
		p.setGender(Gender.MALE);
		p.setTitle(Titel.PhD);
		p.setEMail("test@test.com");
		p.setFax("11221122");
		p.setMobile("222233332222");
		p.setPhone("334422112233");
		
		//Step two - trying to set center & assistant properties
		Center c = centerDao.get(1);
		p.setCenter(c);
		Person a = personDao.get(1);
		p.setAssistant(a);
		
		//Step three - setting the role
		PersonRole pr = new PersonRole();
		pr.setPerson(p);
		Role r = new Role();
		r.setName("testRole");
		Vector<Righ> rights = new Vector<Righ>(1);
		rights.add(Righ.EDIT_TRIAL);
		r.setRights(rights);
		Trial t = trialDao.get(1);
		
		pr.setTrial(t);
		pr.setRole(r);
		pr.setPerson(p);
		
		//Step four - creating a new login object
		Login l = new Login();
		l.setActive(true);
		l.setUsername("test"+ new Random().nextInt());
		l.setPerson(p);
		l.setPassword("test"); //TODO It should be changed, after clarifying the password topic.
		l.setFirstLoggedIn(new GregorianCalendar());
		l.setLastLoggedIn(new GregorianCalendar());
		
		//Step five - saving the new object
		loginDao.save(l);
		
		assertNotNull(loginDao.get(l.getId()));
		assertEquals(l, loginDao.get(l.getId()));
	}
	
}