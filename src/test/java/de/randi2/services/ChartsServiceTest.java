package de.randi2.services;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.randi2.model.Person;
import de.randi2.model.TreatmentArm;
import de.randi2.model.Trial;
import de.randi2.model.TrialSite;
import de.randi2.model.TrialSubject;
import de.randi2.model.enumerations.Gender;
import de.randi2.model.randomization.BlockRandomizationConfig;
import de.randi2.model.randomization.ChartData;

public class ChartsServiceTest extends AbstractServiceTest{

	@Autowired private TrialService trialService;
	@Autowired private TrialSiteService trialSiteService;
	@Autowired private ChartsService chartsService;
	
	private Trial validTrial;
	
	
	@Override
	public void setUp() {
		
		super.setUp();
		validTrial = factory.getTrial();
		validTrial.setLeadingSite(admin.getPerson().getTrialSite());
		validTrial.setSponsorInvestigator(admin.getPerson());

		
//		if(validTrial.getParticipatingSites().size()<2){
//		
//		Person cp2 = new Person();
//		cp2.setFirstname("Contact");
//		cp2.setSurname("Person");
//		cp2.setEmail("randi2@action.ms");
//		cp2.setPhone("1234567");
//		cp2.setSex(Gender.MALE);
//		TrialSite trialSite1 = new TrialSite();
//		trialSite1.setCity("Heidelberg");
//		trialSite1.setCountry("Germany");
//		trialSite1.setName("NCT");
//		trialSite1.setPostcode("69120");
//		trialSite1.setStreet("INF");
//		trialSite1.setPassword("1$heidelberg");
//		trialSite1.setContactPerson(cp2);
//
//		trialSiteService.create(trialSite1);
//		validTrial.addParticipatingSite(trialSite1);
//		}
	
	}
	
	
	
	private void randomizeInValidTrialOneYear(){
		validTrial.setStartDate(new GregorianCalendar(2009, 0, 1));
		validTrial.setEndDate(new GregorianCalendar(2009, 11, 1));
		int blocksize = 4;
		int randomizations = 120;
		TreatmentArm arm1 = new TreatmentArm();
		arm1.setPlannedSubjects(randomizations/2);
		arm1.setName("arm1");
		arm1.setTrial(validTrial);
		TreatmentArm arm2 = new TreatmentArm();
		arm2.setPlannedSubjects(randomizations/2);
		arm2.setName("arm2");
		arm2.setTrial(validTrial);
		List<TreatmentArm> arms = new ArrayList<TreatmentArm>();
		arms.add(arm1);
		arms.add(arm2);
	
		trialService.create(validTrial);
		validTrial.setTreatmentArms(arms);
		BlockRandomizationConfig config =  new BlockRandomizationConfig();
		config.setMaximum(blocksize);
		config.setMinimum(blocksize);
		validTrial.setRandomizationConfiguration(config);
		trialService.update(validTrial);
		assertTrue(validTrial.getId()>0);
		assertEquals(2,validTrial.getTreatmentArms().size());
	
		for(int i=0;i<randomizations;i++){
			TrialSubject subject = new TrialSubject();
			 subject.setIdentification("identification" + i);
			 subject.setTrialSite(validTrial.getLeadingSite());
			 trialService.randomize(validTrial,subject );
			 subject.setCreatedAt(new GregorianCalendar(2009, i%12, 1));
			 sessionFactory.getCurrentSession().update(subject);
			if((i%blocksize)==(blocksize-1)){
			assertEquals(validTrial.getTreatmentArms().get(0).getSubjects().size() ,validTrial.getTreatmentArms().get(1).getSubjects().size());
			}
			
			int diff=validTrial.getTreatmentArms().get(0).getSubjects().size() -validTrial.getTreatmentArms().get(1).getSubjects().size();
			assertTrue((blocksize/2)>=diff && (-1)*(blocksize/2)<=diff);
		}
		
		Trial dbTrial = trialService.getObject(validTrial.getId());
		assertNotNull(dbTrial);
		assertEquals(validTrial.getName(), dbTrial.getName());
		assertEquals(2, dbTrial.getTreatmentArms().size());
		assertEquals(randomizations, dbTrial.getTreatmentArms().get(0).getSubjects().size() + dbTrial.getTreatmentArms().get(1).getSubjects().size());
		assertEquals(randomizations/2, dbTrial.getTreatmentArms().get(0).getSubjects().size());
		assertEquals(randomizations/2, dbTrial.getTreatmentArms().get(1).getSubjects().size());
	}
	
	private void randomizeInValidTrialTwoYears(){
		validTrial.setStartDate(new GregorianCalendar(2009, 0, 1));
		validTrial.setEndDate(new GregorianCalendar(2010, 11, 1));
		int blocksize = 4;
		int randomizations = 240;
		TreatmentArm arm1 = new TreatmentArm();
		arm1.setPlannedSubjects(randomizations/2);
		arm1.setName("arm1");
		arm1.setTrial(validTrial);
		TreatmentArm arm2 = new TreatmentArm();
		arm2.setPlannedSubjects(randomizations/2);
		arm2.setName("arm2");
		arm2.setTrial(validTrial);
		List<TreatmentArm> arms = new ArrayList<TreatmentArm>();
		arms.add(arm1);
		arms.add(arm2);
	
		trialService.create(validTrial);
		validTrial.setTreatmentArms(arms);
		BlockRandomizationConfig config =  new BlockRandomizationConfig();
		config.setMaximum(blocksize);
		config.setMinimum(blocksize);
		validTrial.setRandomizationConfiguration(config);
		trialService.update(validTrial);
		assertTrue(validTrial.getId()>0);
		assertEquals(2,validTrial.getTreatmentArms().size());
	
		for(int i=0;i<randomizations;i++){
			TrialSubject subject = new TrialSubject();
			 subject.setIdentification("identification" + i);
			 subject.setTrialSite(validTrial.getLeadingSite());
			 trialService.randomize(validTrial,subject );
			 subject.setCreatedAt(new GregorianCalendar(2009 + (i>=120? 1:0), i%12, 1));
			 sessionFactory.getCurrentSession().update(subject);
			if((i%blocksize)==(blocksize-1)){
			assertEquals(validTrial.getTreatmentArms().get(0).getSubjects().size() ,validTrial.getTreatmentArms().get(1).getSubjects().size());
			}
			
			int diff=validTrial.getTreatmentArms().get(0).getSubjects().size() -validTrial.getTreatmentArms().get(1).getSubjects().size();
			assertTrue((blocksize/2)>=diff && (-1)*(blocksize/2)<=diff);
		}
		
		Trial dbTrial = trialService.getObject(validTrial.getId());
		assertNotNull(dbTrial);
		assertEquals(validTrial.getName(), dbTrial.getName());
		assertEquals(2, dbTrial.getTreatmentArms().size());
		assertEquals(randomizations, dbTrial.getTreatmentArms().get(0).getSubjects().size() + dbTrial.getTreatmentArms().get(1).getSubjects().size());
		assertEquals(randomizations/2, dbTrial.getTreatmentArms().get(0).getSubjects().size());
		assertEquals(randomizations/2, dbTrial.getTreatmentArms().get(1).getSubjects().size());
	}
	
	
	
	
	@Test
	public void testGenerateRecruitmentChart1(){
		randomizeInValidTrialOneYear();
		ChartData chartData = chartsService.generateRecruitmentChart(validTrial);
		assertEquals(12, chartData.getXLabels().size());
		assertEquals(12, chartData.getData().size());
		for(int i = 0;i< chartData.getData().size();i++){
			assertEquals(10.0*(i+1), chartData.getData().get(i)[0]);
			assertEquals(10.0*(i+1), chartData.getData().get(i)[1]);
		}
		
	}
	
	@Test
	public void testGenerateRecruitmentChart2(){
		randomizeInValidTrialTwoYears();
		ChartData chartData = chartsService.generateRecruitmentChart(validTrial);
		assertEquals(24, chartData.getXLabels().size());
		assertEquals(24, chartData.getData().size());
		for(int i = 0;i< chartData.getData().size();i++){
			assertEquals(10.0*(i+1), chartData.getData().get(i)[0]);
			assertEquals(10.0*(i+1), chartData.getData().get(i)[1]);
		}
		
	}
	
	
	private void randomizeInValidTrialTwoTrialSites(){
		Person cp1 = new Person();
		cp1.setFirstname("Contact");
		cp1.setSurname("Person");
		cp1.setEmail("randi2@action.ms");
		cp1.setPhone("1234567");
		cp1.setSex(Gender.MALE);
		TrialSite trialSite1 = new TrialSite();
		trialSite1.setCity("Heidelberg");
		trialSite1.setCountry("Germany");
		trialSite1.setName("NCT");
		trialSite1.setPostcode("69120");
		trialSite1.setStreet("INF");
		trialSite1.setPassword("1$heidelberg");
		trialSite1.setContactPerson(cp1);
		trialSiteService.create(trialSite1);
		
		Person cp2 = new Person();
		cp2.setFirstname("Contact");
		cp2.setSurname("Person");
		cp2.setEmail("randi2@action.ms");
		cp2.setPhone("1234567");
		cp2.setSex(Gender.MALE);
		TrialSite trialSite2 = new TrialSite();
		trialSite2.setCity("Heidelberg");
		trialSite2.setCountry("Germany");
		trialSite2.setName("NCT2");
		trialSite2.setPostcode("69120");
		trialSite2.setStreet("INF");
		trialSite2.setPassword("1$heidelberg");
		trialSite2.setContactPerson(cp2);
		trialSiteService.create(trialSite2);
		
		validTrial.addParticipatingSite(trialSite1);
		validTrial.addParticipatingSite(trialSite2);
		validTrial.setStartDate(new GregorianCalendar(2009, 0, 1));
		validTrial.setEndDate(new GregorianCalendar(2009, 11, 1));
		
		int blocksize = 4;
		int randomizations = 120;
		TreatmentArm arm1 = new TreatmentArm();
		arm1.setPlannedSubjects(randomizations/2);
		arm1.setName("arm1");
		arm1.setTrial(validTrial);
		TreatmentArm arm2 = new TreatmentArm();
		arm2.setPlannedSubjects(randomizations/2);
		arm2.setName("arm2");
		arm2.setTrial(validTrial);
		List<TreatmentArm> arms = new ArrayList<TreatmentArm>();
		arms.add(arm1);
		arms.add(arm2);
	
		trialService.create(validTrial);
		validTrial.setTreatmentArms(arms);
		BlockRandomizationConfig config =  new BlockRandomizationConfig();
		config.setMaximum(blocksize);
		config.setMinimum(blocksize);
		validTrial.setRandomizationConfiguration(config);
		trialService.update(validTrial);
		assertTrue(validTrial.getId()>0);
		assertEquals(2,validTrial.getTreatmentArms().size());
		Iterator<TrialSite> it = validTrial.getParticipatingSites().iterator();
		TrialSite site1 = it.next();
		TrialSite site2 = it.next();
		for(int i=0;i<randomizations;i++){
			TrialSubject subject = new TrialSubject();
			 subject.setIdentification("identification" + i);
			 if(i<60){
				 subject.setTrialSite(site1);
			 }else{
				 subject.setTrialSite(site2);
			 }
			 trialService.randomize(validTrial,subject );
			 subject.setCreatedAt(new GregorianCalendar(2009, i%12, 1));
			 sessionFactory.getCurrentSession().update(subject);
			if((i%blocksize)==(blocksize-1)){
			assertEquals(validTrial.getTreatmentArms().get(0).getSubjects().size() ,validTrial.getTreatmentArms().get(1).getSubjects().size());
			}
			
			int diff=validTrial.getTreatmentArms().get(0).getSubjects().size() -validTrial.getTreatmentArms().get(1).getSubjects().size();
			assertTrue((blocksize/2)>=diff && (-1)*(blocksize/2)<=diff);
		}
		
		Trial dbTrial = trialService.getObject(validTrial.getId());
		assertNotNull(dbTrial);
		assertEquals(validTrial.getName(), dbTrial.getName());
		assertEquals(2, dbTrial.getTreatmentArms().size());
		assertEquals(randomizations, dbTrial.getTreatmentArms().get(0).getSubjects().size() + dbTrial.getTreatmentArms().get(1).getSubjects().size());
		assertEquals(randomizations/2, dbTrial.getTreatmentArms().get(0).getSubjects().size());
		assertEquals(randomizations/2, dbTrial.getTreatmentArms().get(1).getSubjects().size());
	}
	
	private void randomizeInValidTrialTwoTrialSites31(){
		Person cp1 = new Person();
		cp1.setFirstname("Contact");
		cp1.setSurname("Person");
		cp1.setEmail("randi2@action.ms");
		cp1.setPhone("1234567");
		cp1.setSex(Gender.MALE);
		TrialSite trialSite1 = new TrialSite();
		trialSite1.setCity("Heidelberg");
		trialSite1.setCountry("Germany");
		trialSite1.setName("NCT");
		trialSite1.setPostcode("69120");
		trialSite1.setStreet("INF");
		trialSite1.setPassword("1$heidelberg");
		trialSite1.setContactPerson(cp1);
		trialSiteService.create(trialSite1);
		
		Person cp2 = new Person();
		cp2.setFirstname("Contact");
		cp2.setSurname("Person");
		cp2.setEmail("randi2@action.ms");
		cp2.setPhone("1234567");
		cp2.setSex(Gender.MALE);
		TrialSite trialSite2 = new TrialSite();
		trialSite2.setCity("Heidelberg");
		trialSite2.setCountry("Germany");
		trialSite2.setName("NCT2");
		trialSite2.setPostcode("69120");
		trialSite2.setStreet("INF");
		trialSite2.setPassword("1$heidelberg");
		trialSite2.setContactPerson(cp2);
		trialSiteService.create(trialSite2);
		
		validTrial.addParticipatingSite(trialSite1);
		validTrial.addParticipatingSite(trialSite2);
		validTrial.setStartDate(new GregorianCalendar(2009, 0, 1));
		validTrial.setEndDate(new GregorianCalendar(2009, 11, 1));
		
		int blocksize = 4;
		int randomizations = 120;
		TreatmentArm arm1 = new TreatmentArm();
		arm1.setPlannedSubjects(randomizations/2);
		arm1.setName("arm1");
		arm1.setTrial(validTrial);
		TreatmentArm arm2 = new TreatmentArm();
		arm2.setPlannedSubjects(randomizations/2);
		arm2.setName("arm2");
		arm2.setTrial(validTrial);
		List<TreatmentArm> arms = new ArrayList<TreatmentArm>();
		arms.add(arm1);
		arms.add(arm2);
	
		trialService.create(validTrial);
		validTrial.setTreatmentArms(arms);
		BlockRandomizationConfig config =  new BlockRandomizationConfig();
		config.setMaximum(blocksize);
		config.setMinimum(blocksize);
		validTrial.setRandomizationConfiguration(config);
		trialService.update(validTrial);
		assertTrue(validTrial.getId()>0);
		assertEquals(2,validTrial.getTreatmentArms().size());
		Iterator<TrialSite> it = validTrial.getParticipatingSites().iterator();
		TrialSite site1 = it.next();
		TrialSite site2 = it.next();
		for(int i=0;i<randomizations;i++){
			TrialSubject subject = new TrialSubject();
			 subject.setIdentification("identification" + i);
			 if(i<90){
				 subject.setTrialSite(site1);
			 }else{
				 subject.setTrialSite(site2);
			 }
			 trialService.randomize(validTrial,subject );
			 subject.setCreatedAt(new GregorianCalendar(2009, i%12, 1));
			 sessionFactory.getCurrentSession().update(subject);
			if((i%blocksize)==(blocksize-1)){
			assertEquals(validTrial.getTreatmentArms().get(0).getSubjects().size() ,validTrial.getTreatmentArms().get(1).getSubjects().size());
			}
			
			int diff=validTrial.getTreatmentArms().get(0).getSubjects().size() -validTrial.getTreatmentArms().get(1).getSubjects().size();
			assertTrue((blocksize/2)>=diff && (-1)*(blocksize/2)<=diff);
		}
		
		Trial dbTrial = trialService.getObject(validTrial.getId());
		assertNotNull(dbTrial);
		assertEquals(validTrial.getName(), dbTrial.getName());
		assertEquals(2, dbTrial.getTreatmentArms().size());
		assertEquals(randomizations, dbTrial.getTreatmentArms().get(0).getSubjects().size() + dbTrial.getTreatmentArms().get(1).getSubjects().size());
		assertEquals(randomizations/2, dbTrial.getTreatmentArms().get(0).getSubjects().size());
		assertEquals(randomizations/2, dbTrial.getTreatmentArms().get(1).getSubjects().size());
	}
	
	
	@Test
	public void testGenerateRecruitmentChartTrialSite1(){
		randomizeInValidTrialTwoTrialSites();
		ChartData chartData = chartsService.generateRecruitmentChartTrialSite(validTrial);
		assertEquals(2, chartData.getXLabels().size());
		assertEquals(2, chartData.getData().size());
			assertEquals(60.0, chartData.getData().get(0)[0]);
			assertEquals(60.0, chartData.getData().get(1)[0]);
		
	}
	
	@Test
	@Ignore
	public void testGenerateRecruitmentChartTrialSite2(){
		randomizeInValidTrialTwoTrialSites31();
		ChartData chartData = chartsService.generateRecruitmentChartTrialSite(validTrial);
		assertEquals(2, chartData.getXLabels().size());
		assertEquals(2, chartData.getData().size());
			assertEquals(90.0, chartData.getData().get(0)[0]);
			assertEquals(30.0, chartData.getData().get(1)[0]);
		
	}
	
}
