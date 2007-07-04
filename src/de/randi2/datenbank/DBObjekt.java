package de.randi2.datenbank;

import org.apache.log4j.Logger;

import de.randi2.datenbank.exceptions.DatenbankExceptions;
import de.randi2.model.exceptions.BenutzerException;
import de.randi2.model.exceptions.BenutzerkontoException;
import de.randi2.model.fachklassen.beans.BenutzerkontoBean;
import de.randi2.utility.Config;
import de.randi2.utility.NullKonstanten;
import static de.randi2.utility.Config.Felder;

/**
 * <p>
 * Diese Klasse ermoeglicht es, nach Klassen, welche von Filter erben, in der
 * Datenbak zu speichern, bzw. nach diesen zu suchen.
 * </p>
 * <p>
 * Beans, die als Suchfilter dienen, muessen die Flag {@link #isFilter} auf
 * <code>true</code> gesetzt bekommen, anderenfalls werden sie mit einer
 * {@link DatenbankExceptions} (Msg:
 * {@link DatenbankExceptions#SUCHOBJEKT_IST_KEIN_FILTER}) abgewiesen.
 * </p>
 * <p>
 * per Default ist die Flag {@link #isFilter} auf <code>false</code> gesetzt.
 * Deshalb ist es erforderlich, nach dem Erzeugen eines SuchBeans
 * <code>{@link #setFilter(boolean)}</code> mit <code>true</code>
 * aufzurufen.
 * </p>
 * <p>
 * Beispiel:<br>
 * <code> 
 * BenutzerkontoBean sBean = new BenutzerkontoBean();<br>
 * sBean.setFilter(true);<br>
 * sBean.setBenutzername("s");
 * </code>
 * </p>
 * 
 * @author Benjamin Theel [BTheel@stud.hs-heilbronn.de]
 * @author Frederik Reifschneider [Reifschneider@stud.uni-heidelberg.de]
 * 
 * @version $Id: Filter.java 1828 2007-04-06 18:31:47Z jthoenes $
 * 
 */
public abstract class DBObjekt {

	/**
	 * Repraesentiert das System bei schreibenden Datenbankzugriffen
	 */
	private static BenutzerkontoBean SYSTEMDUMMY_KONTO = null;

	/**
	 * Dieses Benutzerkonto muss gesetzt werden, wenn Aktionen mit dem Objekt
	 * durchgeführt werden die geloggt werden müssen.
	 */
	private BenutzerkontoBean benutzerkontoLogging = null;

	/**
	 * Die eindeutige Id des Objektes, die dem Primary-Key aus der Datenbank
	 * entspricht. Bei noch nicht gespeicherten Objekten ist das Attribut gleich
	 * der DUMMY_ID Konstante aus der NullKonstanten Klasse.
	 */
	private long id = NullKonstanten.DUMMY_ID;

	/**
	 * Flag, die anzeigt, ob ein Bean als Filter eingesetzt werden soll oder
	 * nicht
	 */
	private boolean isFilter = false;

	/**
	 * Leerer Standartkonstruktor, {@link #isFilter} entspricht dem Defaultwert
	 */
	public DBObjekt() {
	}

	/**
	 * Konstrukter Setzt die Flag {@link #isFilter} entsprechend des
	 * uebergebenen Parameters
	 * 
	 * @param filter
	 *            Wert, den die Flag {@link #isFilter} annehmen soll
	 */
	public DBObjekt(boolean filter) {
		this.isFilter = filter;
	}

	/**
	 * Legt den Systemdummy an
	 */
	public static BenutzerkontoBean getSystemdummy() {
		if (SYSTEMDUMMY_KONTO == null) {
			SYSTEMDUMMY_KONTO = new BenutzerkontoBean();
			SYSTEMDUMMY_KONTO.setFilter(true);
			try {
				SYSTEMDUMMY_KONTO.setBenutzername(Config
						.getProperty(Felder.RELEASE_SYSTEMDUMMY_NAME));
				/*
				 * SYSTEMDUMMY_KONTO.setId(Long.valueOf(Config
				 * .getProperty(Felder.RELEASE_SYSTEMDUMMY_NAME)));
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Logger.getLogger(DBObjekt.class).debug("Lege Systemdummy an");
		return SYSTEMDUMMY_KONTO;
	}

	/**
	 * Setzt die Flag {@link #isFilter} entsprechend des Parameters
	 * 
	 * @param filter
	 *            Wert, den die Flag {@link #isFilter} annehmen soll
	 */
	public void setFilter(boolean filter) {
		this.isFilter = filter;
	}

	/**
	 * Liefert den Status der Flag
	 * 
	 * @return <code>true</code>, wenn Objekt als Filter markiert
	 */
	public boolean isFilter() {
		return this.isFilter;
	}

	/**
	 * Die set-Methode fuer die Id - die uebergebene Id darf nicht negativ o.
	 * gleich 0 sein.
	 * 
	 * @param id -
	 *            die neue Id des Objektes (muss ein positiver long Wert sein!)
	 * @throws DatenbankExceptions -
	 *             bei einer uebergebener Id, die <=0 ist.
	 */
	public void setId(long id) throws DatenbankExceptions {
		if (id > 0 || id == NullKonstanten.NULL_LONG) {
			this.id = id;
		} else {
			throw new DatenbankExceptions(DatenbankExceptions.ID_FALSCH);
		}
	}

	/**
	 * Die get-Methode fuer das Id-Attribut der Klasse.
	 * 
	 * @return die eindeutige Id des Objektes.
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Liefert das Benutzerkonto, dass die Logging Informationen enthält.
	 * 
	 * @return Benutzerkonto, das die Logging Informationen enthält.
	 * @throws BenutzerkontoException
	 */
	public BenutzerkontoBean getBenutzerkontoLogging() {
		return benutzerkontoLogging;
	}

	/**
	 * Muss gesetzt werden, wenn Aktionen durchgeführt werden, die geloggt
	 * werden müssen.
	 * 
	 * @param benutzerkontoLogging
	 *            Benutzerkonto, dessen Informationen geloggt werden muessen.
	 */
	public void setBenutzerkontoLogging(BenutzerkontoBean benutzerkontoLogging) {
		this.benutzerkontoLogging = benutzerkontoLogging;
	}

	/**
	 * Diese Methode wird vor dem Speichern in der Datenbank aufgerufen. Sie
	 * stellt sicher, dass alle Pflichtfelder mit Set-Methoden belegt worden
	 * sind.
	 * 
	 * @throws BenutzerException
	 *             Wenn etwas fehlschlaegt eine Exception des jeweiligen Beans.
	 */
	public abstract void validate() throws BenutzerException;

}