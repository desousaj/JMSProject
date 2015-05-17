package web;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import meserreurs.MonException;
import metier.Inscription;
import ejb.DemandeInscriptionTopic;

@WebServlet("/Controleur")
public class Controleur extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String ACTION_TYPE = "action";

	// Action sur les stages
	private static final String ENVOI_INSCRIPTION = "envoiInscription";
	private static final String AJOUTE_INSCRIPTION = "ajouteInscription";
	private static final String AFFICHE_INSCRIPTION = "afficheInscriptions";

	private static final Logger log = Logger.getLogger(Controleur.class
			.getName());

	// Set up all the default values

	private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
	private static final String DEFAULT_DESTINATION = "java:/jms/topic/DemandeInscriptionJmsTopic";

	private static final String DEFAULT_USERNAME = "jmsuser";
	private static final String DEFAULT_PASSWORD = "jmsepul98!";

	@EJB(lookup = DEFAULT_DESTINATION)
	private DemandeInscriptionTopic unEjbInscription = null;

	private Context ctx;

	private static String getLookupNom() {
		/*
		 * appname désigne le nom de l'EAR qui es déployé. Le suffixe ear est
		 * absent. Ce nom peut être laissé vide mais il est préférable de le
		 * renseigner
		 */
		String appName = "";
		/*
		 * modulename désigne le nom du JAR dans le quel l'EJB est encapsulé.
		 */
		String moduleName = "";
		/*
		 * ce nom n'est pas renseigné
		 */
		String distinctName = "";
		// Le nom de la classe Bean de l'EJB
		String beanName = Inscription.class.getSimpleName();
		// on crée la chaîne pour former le nom de la recherche
		String nom = "ejb:" + appName + "/" + moduleName + "/" + distinctName
				+ "/" + beanName + "!";
		return nom;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			System.out.println("firefoiheehghreghreihghreigfrejifjriejfgr");
			// on appelle l'EJB
			ctx = JBossContext.getInitialContext();
			String nom = getLookupNom();
			unEjbInscription = (DemandeInscriptionTopic) ctx.lookup(nom);
		} catch (NamingException ne) {
			request.setAttribute("MesErreurs", ne.getMessage());
			unEjbInscription.EcritureErreur("Impossible de récupérer l'EJB...");
		}
		try {
			processusTraiteRequete(request, response);
		} catch (Exception e) {
			String destinationPage = "/Erreur.jsp";
			unEjbInscription
					.EcritureErreur("Impossible de traiter la requête : "
							+ e.getMessage());
			request.setAttribute("MesErreurs", e.getMessage());
			RequestDispatcher dispatcher = request.getServletContext()
					.getRequestDispatcher(destinationPage);
			dispatcher.forward(request, response);
		}
	}

	// L'appel de cette procédure se fait avec :

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			System.out.println("firefoiheehghreghreihghreigfrejifjriejfgr");

			processusTraiteRequete(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void processusTraiteRequete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			NamingException, JMSException, ParseException {

		String destinationPage = "";
		String actionName = request.getParameter(ACTION_TYPE);
		// execute l'action
		switch (actionName) {
		case AJOUTE_INSCRIPTION:
			destinationPage = "/PostMessage.jsp";
			break;
		case AFFICHE_INSCRIPTION:
			try {
				Inscription ins = new Inscription();
				request.setAttribute("liste", ins.recupererDmdInscription());
				destinationPage = "/AfficherInscriptions.jsp";
			} catch (MonException e) {
				unEjbInscription
						.EcritureErreur("Impossible de récuperer la liste d'inscription ! ");
				destinationPage = "/AfficherInscriptions.jsp";
			}
			break;
		case ENVOI_INSCRIPTION:
			Inscription ins = new Inscription();
			ins.setAdresse(request.getParameter("adresse"));
			ins.setCpostal(request.getParameter("cpostal"));

			String date = request.getParameter("datenaissance");
			Date result = parseDate(date);

			ins.setDatenaissance(result);
			ins.setNomcandidat(request.getParameter("nom"));
			ins.setPrenoncandidat(request.getParameter("prenom"));
			ins.setVille(request.getParameter("ville"));
			sendInscription(ins);
			destinationPage = "/AfficherInscriptions.jsp";
			break;
		}

		RequestDispatcher dispatcher = request.getServletContext()
				.getRequestDispatcher(destinationPage);
		dispatcher.forward(request, response);
	}

	private Date parseDate(String date) throws ParseException {
		DateFormat df = new SimpleDateFormat("MMM dd yyyy", Locale.FRENCH);
		Date result = df.parse(date);
		return result;
	}

	@SuppressWarnings("null")
	private void sendInscription(Inscription ins) throws NamingException,
			JMSException {

		ConnectionFactory connectionFactory = null;
		Connection connection = null;
		Session session = null;
		ObjectMessage objectMessage = null;
		MessageProducer producer = null;
		Destination destination;
		try {
			// On charge le contexte pour une recherche dans l'annuaire JNDI
			ctx = JBossContext.getInitialContext();
			// On construit l'environnemenent à partir
			// des recherches JNDI
			String connectionFactoryString = System.getProperty(
					"connection.factory", DEFAULT_CONNECTION_FACTORY);
			log.info("Attempting to acquire connection factory \""
					+ connectionFactoryString + "\"");
			connectionFactory = (ConnectionFactory) ctx
					.lookup(connectionFactoryString);
			log.info("Found connection factory \"" + connectionFactoryString
					+ "\" in JNDI");

			// Destination
			String destinationString = System.getProperty("destination",
					DEFAULT_DESTINATION);
			log.info("Attempting to acquire destination \"" + destinationString
					+ "\"");
			destination = (Destination) ctx.lookup(destinationString);
			log.info("Found destination \"" + destinationString + "\" in JNDI");

			// On crée la connexion JMS , session, producteur et message;
			connection = connectionFactory.createConnection(
					System.getProperty("username", DEFAULT_USERNAME),
					System.getProperty("password", DEFAULT_PASSWORD));
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			objectMessage = session.createObjectMessage(ins);
			connection.start();
			producer.send(objectMessage);
		} catch (JMSException e) {
			log.severe(e.getMessage());
			throw e;
		} catch (NamingException e) {
			log.severe(e.getMessage());
			throw e;
		} catch (Exception e) {
			log.severe(e.getMessage());
			throw e;
		} finally {
			if (ctx != null) {
				ctx.close();
			}

			// closing the connection takes care of the session, producer, and
			// consumer
			if (connection != null) {
				connection.close();
			}
		}
	}
}
