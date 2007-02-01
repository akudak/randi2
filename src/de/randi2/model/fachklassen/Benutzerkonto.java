package de.randi2.model.fachklassen;

import java.util.Vector;
import org.apache.log4j.Logger;
import de.randi2.datenbank.DatenbankFactory;
import de.randi2.datenbank.exceptions.DatenbankFehlerException;
import de.randi2.model.exceptions.BenutzerkontoException;
import de.randi2.model.fachklassen.beans.BenutzerkontoBean;
import de.randi2.utility.PasswortUtil;

/**
 * Diese Klasse bietet Methoden zur Verwaltung eines bestimmten Benutzerkontos
 * an. Beinhaltet auch es.
 * 
 * @version $Id$
 * @author Lukasz Plotnicki <lplotni@stud.hs-heilbronn.de>
 * @author Frederik Reifschneider <Reifschneider@stud.hs-heidelberg.de>
 * 
 * 
 */
public class Benutzerkonto {
	
	/**
	 * Das zugehörige BenutzerkontoBean-Objekt.
	 */
	private BenutzerkontoBean aBenutzerkonto;

	/**
	 * Ein Konstruktor dieser Klasse.
	 * 
	 * @param aBenutzerkonto
	 *            das zugeöhrige BenutzerkontoBean
	 */
	public Benutzerkonto(BenutzerkontoBean aBenutzerkonto) {
		this.aBenutzerkonto = aBenutzerkonto;
	}

	/**
	 * Diese statische Methode sucht die gewünschten Objekte und falls sie
	 * vorhanden sind, liefert sie zurück.
	 * 
	 * @param sBenutzerkonto
	 *            ein BenutzerkontoBean mit gesuchten Eigenschaften (alle
	 *            irrelevante Felder entsprechen den Null-Werten aus der
	 *            de.randi2.utility.NullKonstanten Klasse)
	 * @return ein Vector mit gefundenen Objekten
	 */
	public static Vector<BenutzerkontoBean> suchenBenutzer(BenutzerkontoBean sBenutzerkonto) {
		
		Vector<BenutzerkontoBean> gefundeneKonten= new Vector<BenutzerkontoBean>();

		try {
			gefundeneKonten = DatenbankFactory.getAktuelleDBInstanz().suchenObjekt(sBenutzerkonto);
		} catch (IllegalArgumentException e) {
			Logger.getLogger("de.randi2.model.Benutzerkonto").error("IllegalArgumentException ist aufgetreten.",e);
		} catch (DatenbankFehlerException e) {
			Logger.getLogger("de.randi2.model.Benutzerkonto").warn("Fehler in Datenbank aufgetreten",e);
		}
		return gefundeneKonten;
	}

	/**
	 * Mit Hilfe dieser Methode, kann ein Benutzerkonto angelegt werden.
	 * Darunter ist zu verstehen, dass das Benutzerkonto gespeichert wird und
	 * die Aktivierungsnachricht an den entsprechenden Benutzer gesendet wird.
	 * 
	 * @param aBenutzerkonto
	 *            das Bentuzerkonto das angelegt werden soll.
	 * @return
	 */
	public static Benutzerkonto anlegenBenutzer(BenutzerkontoBean aBenutzerkonto) {
		
		BenutzerkontoBean aktualisierterBenutzer=null;
		try {
			aktualisierterBenutzer = DatenbankFactory.getAktuelleDBInstanz().schreibenObjekt(aBenutzerkonto);
		} catch (IllegalArgumentException e) {
			Logger.getLogger("de.randi2.model.Benutzerkonto").error("IllegalArgumentException ist aufgetreten.",e);
		} catch (DatenbankFehlerException e) {
			Logger.getLogger("de.randi2.model.Benutzerkonto").warn("Fehler in Datenbank aufgetreten",e);
		}
		return new Benutzerkonto(aktualisierterBenutzer);
	}

	/**
	 * Diese Methode generiert die Aktivierungsnachricht und schickt dem
	 * Benutzer zu.
	 */
	private void sendenAktivierungsMail() {
		//TODO nicht fuer Release 1?
	}

	/**
	 * Diese statische Methode liefert das Bentutzerkonto Objekt zu dem
	 * eingegebenenem Benutzername.
	 * 
	 * @param benutzername
	 *            String, der dem Benutzername entspricht.
	 * @return Ein BenutzerkontoBean Objekt zu diesem Benutzername
	 * @throws BenutzerkontoException
	 *             wenn kein Benutzer mit diesem Banutzername vorhanden ist
	 */
	public static BenutzerkontoBean getBenutzer(String benutzername) throws BenutzerkontoException {
		BenutzerkontoBean bk = new BenutzerkontoBean();
		Vector< BenutzerkontoBean> konten;
		bk.setBenutzername(benutzername);
		konten = suchenBenutzer(bk);
		if(konten==null || konten.size()==0) {
			throw new BenutzerkontoException(BenutzerkontoException.BENUTZER_NICHT_VORHANDEN);
		}
		return konten.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.aBenutzerkonto.toString();
	}

	/**
	 * Diese Methode dient zum Verlgeich von 2 Objekten dieser Klasse.
	 * 
	 * @param zuvergleichendesObjekt
	 *            Objekt mit dem verglichen werden soll.
	 * @return true - wenn die beiden Objekte identisch sind, false wenn das
	 *         nicht der Fall ist.
	 */
	public boolean equals(Benutzerkonto zuvergleichendesObjekt) {
		if (this.aBenutzerkonto.equals(zuvergleichendesObjekt))
			return true;
		return false;
	}

	public BenutzerkontoBean getBenutzerkontobean(){
		return this.aBenutzerkonto;
	}
	
	/**
	 * Diese Methode prueft, ob das uebergebene Passwort richtig ist.
	 * 
	 * @param passwort
	 *            Das Passwort, das ueberprueft werden soll.
	 * @return true, wenn das Passwort richtig ist. False, bei falchem Passwort.
	 */
	public boolean pruefenPasswort(String passwort) {
		if (PasswortUtil.getInstance().hashPasswort(passwort).equals(this.getBenutzerkontobean().getPasswort()))
			return true;
		return false;
	}
}
